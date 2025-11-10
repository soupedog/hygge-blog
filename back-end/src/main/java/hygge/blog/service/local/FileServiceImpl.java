package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.MapToAnyMapper;
import hygge.blog.common.mapper.OverrideMapper;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.FileInfoInfo;
import hygge.blog.domain.local.enums.AccessRuleTypeEnum;
import hygge.blog.domain.local.enums.FileCopyTypeEnum;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.base.FileInfoBase;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.repository.database.FileInfoDao;
import hygge.blog.repository.database.FileInfoViewDao;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.blog.service.local.normal.UserServiceImpl;
import hygge.commons.exception.InternalRuntimeException;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.bo.ColumnInfo;
import hygge.util.definition.DaoHelper;
import hygge.util.definition.FileHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Service
public class FileServiceImpl extends HyggeJsonUtilContainer {
    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);
    private static final DaoHelper daoHelper = UtilCreator.INSTANCE.getDefaultInstance(DaoHelper.class);

    private static final List<FileTypeEnum> TYPE_FOR_ALL = collectionHelper.createCollection(FileTypeEnum.values());
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value("${file.upload.path}")
    private String filePath;
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryService;
    private final FileInfoDao fileInfoDao;
    private final FileInfoViewDao fileInfoViewDao;
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo(true, true, "description", null));
        forUpdate.add(new ColumnInfo(true, true, "cid", null).toStringColumn(1, 255));
        forUpdate.add(new ColumnInfo(true, false, "name", null).toStringColumn(1, 255));
        forUpdate.add(new ColumnInfo(true, false, "fileType", null).toStringColumn(1, 30));
        forUpdate.add(new ColumnInfo(true, false, "fileCopyType", null).toStringColumn(1, 30));
    }

    public FileServiceImpl(UserServiceImpl userService, CategoryServiceImpl categoryService, FileInfoDao fileInfoDao, FileInfoViewDao fileInfoViewDao) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.fileInfoDao = fileInfoDao;
        this.fileInfoViewDao = fileInfoViewDao;
    }

    public List<FileInfoDto> uploadFile(String cid, FileTypeEnum fileType, List<MultipartFile> filesList) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        boolean needCopyToHardDisk = true;

        if (cid != null) {
            // 目标类别必须存在
            Category category = categoryService.findCategoryByCid(cid, false);
            // 有且全都是 AccessRuleTypeEnum.PUBLIC 的就说明可以公开拷贝
            needCopyToHardDisk = category.getAccessRuleList().stream()
                    .allMatch(accessRule -> AccessRuleTypeEnum.PUBLIC.equals(accessRule.getAccessRuleType()));
        }

        List<FileInfoDto> result = new ArrayList<>();

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

            if (fileInfoViewDao.existsByFileTypeAndNameAndExtension(fileType, name, extension)) {
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
                if (needCopyToHardDisk) {
                    // 文件所属于公开类别则使用 Nginx 创建副本
                    fileInfo.setFileCopyType(FileCopyTypeEnum.NGINX);
                }
                fileInfo.setFileSize(temp.getSize());
                fileInfo.setContent(temp.getBytes());

                // 持久化文件到数据库
                fileInfoDao.save(fileInfo);

                FileInfoDto item = fileInfo.toDto();
                result.add(item);
                // 没有权限控制的文件允许 NGINX 作为静态资源，拷贝到磁盘
                if (needCopyToHardDisk) {
                    createFileToHardDisk(fileInfo);
                }

                // 仅用于本地模拟启动统一化文件路径标识，本地调试是 Windows ，强行转换成 Linux
                if (isWindows) {
                    item.setSrc(item.getSrc().replace(File.separator, "/"));
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

    public void updateFileInfo(String fileNo, Map<String, Object> data) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        // 是否有文件查询权限
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Optional<FileInfoView> targetFileInfoTemp = fileInfoViewDao.findOne(Example.of(FileInfoView.builder()
                .fileNo(fileNo)
                .build()));

        if (targetFileInfoTemp.isEmpty()) {
            throw new LightRuntimeException("File(" + fileNo + ") was not found.", BlogSystemCode.FAIL_TO_QUERY_FILE);
        }
        FileInfoView fileInfoView = targetFileInfoTemp.get();
        User owner = userService.findUserByUserId(fileInfoView.getUserId(), false);

        // 是否有修改权限
        userService.checkUserRightOrHimself(owner, UserTypeEnum.ROOT);

        Integer currentUserId = currentUser.getUserId();
        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate, finalDataTemp -> {
            // 过滤成功回调，写回当前操作用户
            finalDataTemp.put("userId", currentUserId);
            return finalDataTemp;
        });

        boolean canUpdateToNginxCopyType = false;
        String articleCategoryName = null;

        // 存在 cid 修改时，需要验证新 cid 存在性
        String cid = (String) finalData.get("cid");
        if (cid != null) {
            Category category = categoryService.findCategoryByCid(cid, false);
            canUpdateToNginxCopyType = category.getAccessRuleList().stream().allMatch(accessRule -> AccessRuleTypeEnum.PUBLIC.equals(accessRule.getAccessRuleType()));
            articleCategoryName = category.getCategoryName();
        }

        FileInfo oldAndBeenOverwrite = new FileInfo();
        OverrideMapper.INSTANCE.viewOverrideToPo(fileInfoView, oldAndBeenOverwrite);

        FileInfo newOne = MapToAnyMapper.INSTANCE.mapToFileInfo(finalData);

        OverrideMapper.INSTANCE.overrideToAnother(newOne, oldAndBeenOverwrite);

        // 非公开的文章类别不允许创建 Nginx 文件副本
        if (newOne.getFileCopyType() != null && newOne.getFileCopyType().equals(FileCopyTypeEnum.NGINX) && !canUpdateToNginxCopyType) {
            throw new LightRuntimeException("File(" + articleCategoryName + ") can't be updated to ArticleCategory(" + articleCategoryName + ") with FileCopyType.NGINX.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
        }

        boolean isPathChanged = !fileInfoView.returnRelativePath().equals(oldAndBeenOverwrite.returnRelativePath());

        if (isPathChanged) {
            pathConflictCheck(oldAndBeenOverwrite);
        }

        boolean copyTypeChanged = newOne.getFileCopyType() != null && !fileInfoView.getFileCopyType().equals(newOne.getFileCopyType());

        if (copyTypeChanged) {
            if (fileInfoView.getFileCopyType().equals(FileCopyTypeEnum.DEFAULT)) {
                // 无副本切换到有副本，仅新增副本
                Optional<FileInfo> fileInfoTemp = fileInfoDao.findOne(Example.of(FileInfo.builder().fileNo(fileNo).build()));
                if (fileInfoTemp.isEmpty()) {
                    throw new InternalRuntimeException("FileInfo(" + fileNo + ") was not found.");
                }

                oldAndBeenOverwrite.setContent(fileInfoTemp.get().getContent());

                createFileToHardDisk(oldAndBeenOverwrite);
            } else {
                // 有副本切换到无副本，仅删除旧副本
                String oldCachePath = filePath + fileInfoView.returnRelativePath();
                File oldFile = new File(oldCachePath);
                deleteFileInHardDisk(true, oldFile, oldCachePath);
            }
        } else {
            if (fileInfoView.getFileCopyType().equals(FileCopyTypeEnum.NGINX)) {
                // 未切换副本类型，属于 Nginx，可能存在路径变更
                // 检测是否存在硬盘副本
                String newCachePath = filePath + oldAndBeenOverwrite.returnRelativePath();
                String oldCachePath = filePath + fileInfoView.returnRelativePath();

                File oldFile = new File(oldCachePath);
                if (oldFile.exists()) {
                    File newFile = new File(newCachePath);
                    // 保障所需文件夹被创建
                    fileHelper.getOrCreateDirectoryIfNotExit(filePath + oldAndBeenOverwrite.getFileType().getPath());
                    try {
                        FileCopyUtils.copy(oldFile, newFile);
                        log.info("Copy file({}) to file({}) success.", oldCachePath, newCachePath);
                        deleteFileInHardDisk(true, oldFile, oldCachePath);
                    } catch (IOException e) {
                        throw new InternalRuntimeException("Fail to copy old file to new space.", BlogSystemCode.FAIL_TO_UPDATE_FILE, e);
                    }
                } else {
                    throw new InternalRuntimeException("Fail to copy old file to new space, oldFile not exist.", BlogSystemCode.FAIL_TO_UPDATE_FILE);
                }
            }
        }

        fileInfoDao.save(oldAndBeenOverwrite);
    }

    public FileInfoDto findFileInfo(String fileNo) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        // 是否有文件查询权限
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        return findFileViewFromDB(fileNo).map(FileInfoBase::toDto).orElse(null);
    }

    public FileInfoInfo findFileInfoPageQuery(List<FileTypeEnum> fileTypes, Integer currentPage, Integer pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        // 是否有文件查询权限
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        List<FileTypeEnum> actualFileTypes = fileTypes == null ? TYPE_FOR_ALL : fileTypes;
        FileInfoInfo result = new FileInfoInfo();

        List<FileInfoDto> fileInfoDtoList = new ArrayList<>();
        List<Category> categoryList = categoryService.getAccessibleCategoryList(currentUser, null);

        List<String> cidList = collectionHelper.filterNonemptyItemAsArrayList(false, categoryList, Category::getCid);

        Sort sort = Sort.by(Sort.Order.asc("createTs"));
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<FileInfoView> resultTemp = fileInfoViewDao.findFileInfoMultiple(actualFileTypes, cidList, pageable);

        resultTemp.stream().forEach(item -> {
            FileInfoDto resultTempItem = item.toDto();

            // 检测是否存在硬盘副本
            String cachePath = filePath + resultTempItem.getSrc();
            File file = new File(cachePath);
            if (file.exists()) {
                resultTempItem.setIsInHardDisk(true);
            }

            // 仅用于本地模拟启动统一化文件路径标识，本地调试是 Windows ，强行转换成 Linux
            if (isWindows) {
                resultTempItem.setSrc(resultTempItem.getSrc().replace(File.separator, "/"));
            }
            fileInfoDtoList.add(resultTempItem);
        });

        result.setFileInfoList(fileInfoDtoList);
        result.setTotalCount(resultTemp.getTotalElements());
        return result;
    }

    public void createFileCopyFromDBToHardDisk(String fileNo) {
        Optional<FileInfo> fileInfoTemp = fileInfoDao.findOne(Example.of(FileInfo.builder().fileNo(fileNo).build()));

        if (fileInfoTemp.isEmpty()) {
            throw new InternalRuntimeException("FileInfo(" + fileNo + ") was not found.");
        }

        createFileToHardDisk(fileInfoTemp.get());
    }

    public void deleteFile(String fileNo) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        // 是否有文件删除权限
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Optional<FileInfoView> fileInfoViewTemp = fileInfoViewDao.findOne(Example.of(FileInfoView.builder().fileNo(fileNo).build()));

        fileInfoViewTemp.ifPresent((fileInfoView) -> {
            // 检测是否存在硬盘副本
            String cachePath = filePath + fileInfoView.toDto().getSrc();
            File file = new File(cachePath);
            deleteFileInHardDisk(file.exists(), file, cachePath);
            long affectedRows = fileInfoDao.deleteByFileNo(fileNo);
            if (affectedRows > 0) {
                log.info("delete file({}) success, affected rows:{}.", fileInfoView.getName(), affectedRows);
            }
        });
    }

    public Optional<FileInfo> findFileFromDB(String fileNo) {
        return fileInfoDao.findOne(Example.of(FileInfo.builder().fileNo(fileNo)
                .build()));
    }

    public Optional<FileInfoView> findFileViewFromDB(String fileNo) {
        return fileInfoViewDao.findOne(Example.of(FileInfoView.builder().fileNo(fileNo)
                .build()));
    }

    public void pathConflictCheck(FileInfoBase newFile) {
        Sort sort = Sort.by(Sort.Order.asc("createTs"));
        Pageable pageable = PageRequest.of(0, 1, sort);

        Page<FileInfoView> conflictResultTemp = fileInfoViewDao.findAll(Example.of(FileInfoView.builder()
                .fileType(newFile.getFileType())
                .name(newFile.getName())
                .extension(newFile.getExtension())
                .build()), pageable);

        long conflictCount = conflictResultTemp.getTotalElements();

        if (conflictCount > 1L) {
            // 超过 1 个已存在的冲突必然冲突
            throw new LightRuntimeException("File(" + newFile.getName() + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
        } else if (conflictCount == 1L && conflictResultTemp.get().noneMatch(item -> item.getFileNo().equals(newFile.getFileNo()))) {
            // 存在 1 个已存在的冲突且不是自己
            throw new LightRuntimeException("File(" + newFile.getName() + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
        }
    }

    public void createFileToHardDisk(FileInfo fileInfo) {
        String path = filePath + fileInfo.getFileType().getPath() + fileInfo.getName() + "." + fileInfo.getExtension();
        try {
            File file = new File(path);

            if (!file.isAbsolute()) {
                throw new LightRuntimeException("Ptah(" + path + ") of File(" + fileInfo.getName() + ") was unexpected.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            if (file.exists()) {
                throw new LightRuntimeException("File(" + fileInfo.getName() + ") already exist in HardDisk.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            // 保障所需文件夹被创建
            fileHelper.getOrCreateDirectoryIfNotExit(filePath + fileInfo.getFileType().getPath());
            boolean createNoConflict = file.createNewFile();
            if (!createNoConflict) {
                throw new LightRuntimeException("File(" + fileInfo.getName() + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            // 拷贝文件到磁盘
            FileCopyUtils.copy(fileInfo.getContent(), Files.newOutputStream(file.toPath()));
            log.info("Copy file " + path + " to hard disk success.");
        } catch (LightRuntimeException le) {
            // 主动抛出的已知异常已经标记了错误原因
            throw le;
        } catch (Exception e) {
            throw new LightRuntimeException("Fail to copy file:[" + path + "].", BlogSystemCode.FAIL_TO_UPLOAD_FILE, e);
        }
    }

    public void deleteFileInHardDisk(boolean needDelete, File file, String filePath) {
        if (needDelete) {
            boolean deleteSuccess = file.delete();
            if (!deleteSuccess) {
                log.info("Delete file({}) in HardDisk failed.", filePath);
            } else {
                log.info("Delete file({}) in HardDisk success.", filePath);
            }
        }
    }
}
