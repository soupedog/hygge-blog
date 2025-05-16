package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.FileInfoForFrontEnd;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.FileInfoDao;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.definition.FileHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Service
public class FileServiceImpl extends HyggeJsonUtilContainer {
    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);
    @Value("${file.upload.path}")
    private String filePath;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private FileInfoDao fileInfoDao;

    public List<FileInfoForFrontEnd> uploadFile(String cid, FileTypeEnum fileType, List<MultipartFile> filesList) {
        if (cid != null) {
            // 目标类别必须存在
            categoryService.findCategoryByCid(cid, false);
        }

        List<FileInfoForFrontEnd> result = new ArrayList<>();

        for (MultipartFile temp : filesList) {
            String fileName = temp.getOriginalFilename();
            String path = filePath + fileType.getPath() + fileName;
            File file = new File(path);
            if (file.exists()) {
                throw new LightRuntimeException("File(" + fileName + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            String fileNo = randomHelper.getUniversallyUniqueIdentifier(true);

            try {
                fileHelper.getOrCreateDirectoryIfNotExit(filePath + fileType.getPath());
                boolean createComplete = file.createNewFile();
                if (!createComplete) {
                    throw new LightRuntimeException("File(" + fileName + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
                }

                HyggeRequestContext context = HyggeRequestTracker.getContext();
                User currentUser = context.getCurrentLoginUser();

                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileNo(fileNo);
                fileInfo.setUserId(currentUser.getUserId());
                fileInfo.setCid(cid);
                fileInfo.setName(fileName);

                int indexOfLastPoint = fileName.lastIndexOf(".");
                if (indexOfLastPoint > 0 && indexOfLastPoint < fileName.length() - 1) {
                    String extension = fileName.substring(indexOfLastPoint + 1);
                    fileInfo.setExtension(extension);
                }
                fileInfo.setFileType(fileType);
                fileInfo.setFileSize(temp.getSize());
                fileInfo.setContent(temp.getBytes());
                temp.transferTo(file);

                if (file.isAbsolute() && !file.exists()) {
                    // 拷贝文件到磁盘
                    FileCopyUtils.copy(fileInfo.getContent(), Files.newOutputStream(file.toPath()));
                }
                // 持久化文件到数据库
                fileInfoDao.save(fileInfo);
            } catch (LightRuntimeException le) {
                // 主动抛出的已知异常已经标记了错误原因
                throw le;
            } catch (Exception e) {
                throw new LightRuntimeException("Fail to upload " + fileName + ".", BlogSystemCode.FAIL_TO_UPLOAD_FILE, e);
            }

            FileInfoForFrontEnd item = FileInfoForFrontEnd.builder()
                    .src(fileType.getPath() + fileName)
                    .name(fileName)
                    .extension(Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")))
                    .fileNo(fileNo)
                    .build();
            item.setFileSizeWithByte(new BigDecimal(file.length()));

            result.add(item);
        }
        return result;
    }

    public List<FileInfoForFrontEnd> findFileInfo(List<FileTypeEnum> fileTypes) {
        List<FileInfoForFrontEnd> result = new ArrayList<>();

        for (FileTypeEnum fileType : fileTypes) {
            String actualPath = filePath + fileType.getPath();
            File file = fileHelper.getOrCreateDirectoryIfNotExit(actualPath);
            List<File> files = fileHelper.getFileFromDirectory(file, pathname -> true);

            files.forEach(item -> {
                String absolutePath = item.getAbsolutePath();

                FileInfoForFrontEnd fileInfoForFrontEnd = FileInfoForFrontEnd.builder()
                        .src(absolutePath.substring(filePath.length()))
                        .name(absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1, absolutePath.lastIndexOf(".")))
                        .build();

                fileInfoForFrontEnd.setSrc(fileInfoForFrontEnd.getSrc().replace(File.separator, "/"));

                fileInfoForFrontEnd.setFileSizeWithByte(new BigDecimal(item.length()));
                result.add(fileInfoForFrontEnd);
            });
        }
        return result;
    }

    public Optional<FileInfo> findFileFromDB(String fileNo) {
        return fileInfoDao.findOne(Example.of(FileInfo.builder().fileNo(fileNo)
                .build()));
    }

}
