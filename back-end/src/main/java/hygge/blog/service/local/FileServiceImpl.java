package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.FileInfoForFrontEnd;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.FileInfoDao;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.definition.FileHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Service
public class FileServiceImpl extends HyggeJsonUtilContainer {
    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);

    private static final List<FileTypeEnum> TYPE_FOR_ALL = collectionHelper.createCollection(FileTypeEnum.values());

    @Value("${file.upload.path}")
    private String filePath;
    private final CategoryServiceImpl categoryService;
    private final FileInfoDao fileInfoDao;

    @Autowired
    public FileServiceImpl(CategoryServiceImpl categoryService, FileInfoDao fileInfoDao) {
        this.categoryService = categoryService;
        this.fileInfoDao = fileInfoDao;
    }

    public List<FileInfoForFrontEnd> uploadFile(String cid, FileTypeEnum fileType, List<MultipartFile> filesList) {
        if (cid != null) {
            // 目标类别必须存在
            categoryService.findCategoryByCid(cid, false);
        }

        boolean needCopyToHardDisk = cid != null;

        List<FileInfoForFrontEnd> result = new ArrayList<>();

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        for (MultipartFile temp : filesList) {
            String fileName = temp.getOriginalFilename();
            String extension = null;
            // 不带扩展名的文件名
            String name = null;

            int indexOfLastPoint = fileName.lastIndexOf(".");
            if (indexOfLastPoint > 0 && indexOfLastPoint < fileName.length() - 1) {
                extension = fileName.substring(indexOfLastPoint + 1);
                name = fileName.substring(0, indexOfLastPoint);
            }

            if (fileInfoDao.existsByName(name)) {
                throw new LightRuntimeException("File(" + fileName + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            String fileNo = randomHelper.getUniversallyUniqueIdentifier(true);

            try {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileNo(fileNo);
                fileInfo.setUserId(currentUser.getUserId());
                fileInfo.setCid(cid);
                fileInfo.setExtension(extension);
                fileInfo.setName(name);
                fileInfo.setFileType(fileType);
                fileInfo.setFileSize(temp.getSize());
                fileInfo.setContent(temp.getBytes());

                // 持久化文件到数据库
                fileInfoDao.save(fileInfo);

                FileInfoForFrontEnd item = FileInfoForFrontEnd.builder()
                        .src(fileType.getPath() + fileName)
                        .name(fileName)
                        .extension(fileInfo.getExtension())
                        .fileNo(fileNo)
                        .build();
                item.setFileSizeWithByte(new BigDecimal(fileInfo.getFileSize()));

                result.add(item);
                // 没有权限控制的文件允许 NGINX 作为静态资源，拷贝到磁盘
                if (needCopyToHardDisk) {
                    copyFileToHardDisk(fileInfo);
                }
            } catch (LightRuntimeException le) {
                // 主动抛出的已知异常已经标记了错误原因
                throw le;
            } catch (Exception e) {
                throw new LightRuntimeException("Fail to upload " + fileName + ".", BlogSystemCode.FAIL_TO_UPLOAD_FILE, e);
            }
        }
        return result;
    }

    public List<FileInfoForFrontEnd> findFileInfo(List<FileTypeEnum> fileTypes, Integer currentPage, Integer pageSize) {
        List<FileTypeEnum> actualFileTypes = fileTypes == null ? TYPE_FOR_ALL : fileTypes;

        List<FileInfoForFrontEnd> result = new ArrayList<>();

        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        List<String> cidList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCid);

        Sort sort = Sort.by(Sort.Order.asc("createTs"));
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<FileInfo> resultTemp = fileInfoDao.findFileInfoMultiple(actualFileTypes, cidList, pageable);

        resultTemp.stream().forEach(item -> {
            FileInfoForFrontEnd fileInfoForFrontEnd = FileInfoForFrontEnd.builder()
                    .fileNo(item.getFileNo())
                    .name(item.getName())
                    .extension(item.getExtension())
                    .description(item.getDescription())
                    .build();

            fileInfoForFrontEnd.setSrc(getFileCacheSrc(item).replace(File.separator, "/"));

            fileInfoForFrontEnd.setFileSizeWithByte(new BigDecimal(item.getFileSize()));
            result.add(fileInfoForFrontEnd);
        });
        return result;
    }

    public void copyFileToHardDisk(FileInfo fileInfo) {
        String path = filePath + fileInfo.getFileType().getPath() + fileInfo.getName() + "." + fileInfo.getExtension();
        try {
            File file = new File(path);
            fileHelper.getOrCreateDirectoryIfNotExit(filePath);
            boolean createComplete = file.createNewFile();
            if (!createComplete) {
                throw new LightRuntimeException("File(" + fileInfo.getName() + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            if (file.isAbsolute() && !file.exists()) {
                // 拷贝文件到磁盘
                FileCopyUtils.copy(fileInfo.getContent(), Files.newOutputStream(file.toPath()));
            }
        } catch (LightRuntimeException le) {
            // 主动抛出的已知异常已经标记了错误原因
            throw le;
        } catch (Exception e) {
            throw new LightRuntimeException("Fail to copy file:[" + path + "].", BlogSystemCode.FAIL_TO_UPLOAD_FILE, e);
        }
    }

    public Optional<FileInfo> findFileFromDB(String fileNo) {
        return fileInfoDao.findOne(Example.of(FileInfo.builder().fileNo(fileNo)
                .build()));
    }

    public String getFileCacheSrc(FileInfo fileInfo) {
        return fileInfo.getFileType().getPath() + fileInfo.getName() + "." + fileInfo.getExtension();
    }
}
