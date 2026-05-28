package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.MapToAnyMapper;
import hygge.blog.common.mapper.OverrideMapper;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.bo.CacheObjectContainer;
import hygge.blog.domain.local.dto.FileInfoDto;
import hygge.blog.domain.local.dto.FileInfoInfo;
import hygge.blog.domain.local.enums.FileCacheTypeEnum;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.Category;
import hygge.blog.domain.local.po.FileInfo;
import hygge.blog.domain.local.po.User;
import hygge.blog.domain.local.po.base.FileInfoBase;
import hygge.blog.domain.local.po.view.FileInfoView;
import hygge.blog.repository.database.FileInfoDao;
import hygge.blog.repository.database.FileInfoViewDao;
import hygge.blog.service.local.inner.file.CacheFileKeyKeeper;
import hygge.blog.service.local.inner.file.FileOperationResult;
import hygge.blog.service.local.inner.file.FileOperationTool;
import hygge.blog.service.local.inner.file.FileUrlBuilder;
import hygge.blog.service.local.normal.CategoryServiceImpl;
import hygge.blog.service.local.normal.PermissionServiceImpl;
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
import java.io.IOException;
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

    @Value("${file.root-in-server.path}")
    private String fileRootPath;

    private final UserServiceImpl userService;
    private final PermissionServiceImpl permissionService;
    private final CategoryServiceImpl categoryService;
    private final FileInfoDao fileInfoDao;
    private final FileInfoViewDao fileInfoViewDao;
    private final FileUrlBuilder fileUrlBuilder;
    private final CacheFileKeyKeeper fileKeyKeeper;

    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo(true, true, "description", null));
        forUpdate.add(new ColumnInfo(true, true, "permissionId", null, Integer.MIN_VALUE, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "name", null).toStringColumn(1, 255));
        forUpdate.add(new ColumnInfo(true, false, "fileType", null).toStringColumn(1, 30));
        forUpdate.add(new ColumnInfo(true, false, "fileCacheType", null).toStringColumn(1, 30));
    }

    @Autowired
    public FileServiceImpl(UserServiceImpl userService, PermissionServiceImpl permissionService, CategoryServiceImpl categoryService, FileInfoDao fileInfoDao, FileInfoViewDao fileInfoViewDao, FileUrlBuilder fileUrlBuilder, CacheFileKeyKeeper fileKeyKeeper) {
        this.userService = userService;
        this.permissionService = permissionService;
        this.categoryService = categoryService;
        this.fileInfoDao = fileInfoDao;
        this.fileInfoViewDao = fileInfoViewDao;
        this.fileUrlBuilder = fileUrlBuilder;
        this.fileKeyKeeper = fileKeyKeeper;
    }

    public String generateOneTimeFileKey(String fileNo) {
        if (fileNo == null || fileNo.isEmpty()) {
            throw new InternalRuntimeException("[fileNo] can't be empty.");
        }
        return fileKeyKeeper.generateOneTimeFileKey(fileNo);
    }

    public String getFileCacheLink(String fileNo) {
        if (parameterHelper.isEmpty(fileNo)) {
            return null;
        }
        Optional<FileInfoView> fileInfoViewTemp = findFileViewFromDB(fileNo);

        return fileInfoViewTemp.map(this::getFileCacheLink).orElse(null);
    }

    public String getFileCacheLink(FileInfoBase fileInfoBase) {
        if (fileInfoBase == null
                || fileInfoBase.getDescription() == null
                || fileInfoBase.getDescription().getCacheLink() == null
                || fileInfoBase.getDescription().getCacheLink().trim().isEmpty()) {
            return null;
        }

        return fileInfoBase.getDescription().getCacheLink();
    }

    public String getFileApiLink(String fileNo) {
        if (parameterHelper.isEmpty(fileNo)) {
            return null;
        }

        return fileUrlBuilder.getFileApiLinkByFileNo(fileNo);
    }

    public CacheObjectContainer.FileAccessUrl getFileAccessUrl(String fileNo) {
        if (parameterHelper.isEmpty(fileNo)) {
            return null;
        }

        Optional<FileInfoView> fileInfoViewTemp = findFileViewFromDB(fileNo);

        return fileInfoViewTemp.map(this::getFileAccessUrl).orElse(null);
    }

    public CacheObjectContainer.FileAccessUrl getFileAccessUrl(FileInfoBase fileInfoBase) {
        if (fileInfoBase == null) {
            return null;
        }

        boolean isApiLink = false;
        // 优先查缓存文件
        String result = getFileCacheLink(fileInfoBase);

        if (result == null) {
            result = getFileApiLink(fileInfoBase.getFileNo());
            if (!parameterHelper.isEmpty(result)) {
                isApiLink = true;
            }
        }

        if (result == null) {
            return null;
        }

        return CacheObjectContainer.FileAccessUrl.builder()
                .src(result)
                .isApiLink(isApiLink)
                .build();
    }

    public FileInfoView findFileInfoView(FileTypeEnum fileType, String name, String extension) {
        return fileInfoViewDao.findByFileTypeAndNameAndExtension(fileType, name, extension);
    }

    public List<FileInfoDto> uploadFile(Integer permissionId, String cid, FileTypeEnum fileType, List<MultipartFile> filesList) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();

        Integer actualPermissionId = null;

        switch (fileType) {
            // 句子收藏和系统核心组件默认都是公开可见
            case CORE, QUOTE -> actualPermissionId = PermissionServiceImpl._PUBLIC.getPermissionId();
        }

        if (actualPermissionId == null && permissionService.isPermissionPassed(permissionId, currentUser, null)) {
            actualPermissionId = permissionId;
        }

        if (actualPermissionId == null) {
            if (cid != null) {
                // 目标类别必须存在，权限就以类别为准
                Category category = categoryService.findCategoryByCid(cid, false);
                actualPermissionId = category.getPermissionId();
            } else {
                // 类别不存在，权限默认为仅自己可见
                actualPermissionId = permissionService.getPersonalPermissionIdOfUser(currentUser);
            }
        }

        if (actualPermissionId == null) {
            throw new LightRuntimeException("Please log in and try again.", BlogSystemCode.INSUFFICIENT_PERMISSIONS);
        }

        // 是 PUBLIC 则默认创建缓存拷贝
        boolean needCreateCache = actualPermissionId.equals(PermissionServiceImpl._PUBLIC.getPermissionId());

        List<FileInfoDto> result = new ArrayList<>();

        for (MultipartFile temp : filesList) {
            String fileName = temp.getOriginalFilename();
            if (fileName == null) {
                throw new LightRuntimeException("Please confirm that the file name is not empty.");
            }

            String extension = null;
            // 不带扩展名的文件名
            String name = null;

            int indexOfLastPoint = fileName.lastIndexOf(".");
            if (indexOfLastPoint > 0 && indexOfLastPoint < fileName.length() - 1) {
                extension = fileName.substring(indexOfLastPoint + 1);
                name = fileName.substring(0, indexOfLastPoint);
            }

            if (extension == null) {
                throw new LightRuntimeException("Please confirm the file name has an extension.");
            }

            if (fileInfoViewDao.existsByFileTypeAndNameAndExtension(fileType, name, extension)) {
                throw new LightRuntimeException("File(" + fileName + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }

            String fileNo = randomHelper.getUniversallyUniqueIdentifier(true);

            try {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileNo(fileNo);
                fileInfo.setUserId(currentUser.getUserId());
                fileInfo.setPermissionId(permissionId);
                fileInfo.setExtension(extension);
                fileInfo.setName(name);
                fileInfo.setFileType(fileType);

                if (needCreateCache) {
                    // 文件所属于公开类别则使用 Nginx 创建副本
                    fileInfo.setFileCacheType(FileCacheTypeEnum.NGINX);
                }

                fileInfo.setFileSize(temp.getSize());
                fileInfo.setContent(temp.getBytes());

                // 持久化文件到数据库
                fileInfoDao.save(fileInfo);

                FileInfoDto item = fileInfo.toDto();

                // NGINX 作为静态资源，绝对路径指向 Nginx 根目录即可，将文件拷贝到磁盘
                if (needCreateCache) {
                    createCacheFile(getAbsolutePath(fileInfo), fileInfo);
                }

                // 存在检测 Nginx 缓存，需要在缓存之后再初始化
                initLink(item);
                result.add(item);
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

        // 公开可见类型，才允许缓存
        boolean isAllowCaching = PermissionServiceImpl._PUBLIC.getPermissionId().equals(fileInfoView.getPermissionId());

        Integer permissionId = (Integer) finalData.get("permissionId");
        if (permissionId != null) {
            if (!permissionService.isPermissionPassed(permissionId, currentUser, null)) {
                throw new LightRuntimeException("Please confirm the permissionId is correct.");
            }
            // 用户主动指定了授权类型，需要重新检测
            isAllowCaching = PermissionServiceImpl._PUBLIC.getPermissionId().equals(permissionId);
        }

        FileInfo oldAndBeenOverwrite = new FileInfo();
        OverrideMapper.INSTANCE.viewOverrideToPo(fileInfoView, oldAndBeenOverwrite);

        FileInfo newOne = MapToAnyMapper.INSTANCE.mapToFileInfo(finalData);

        OverrideMapper.INSTANCE.overrideToAnother(newOne, oldAndBeenOverwrite);

        // 非公开的文章类别不允许创建 Nginx 文件副本
        if (newOne.getFileCacheType() != null && newOne.getFileCacheType().equals(FileCacheTypeEnum.NGINX) && !isAllowCaching) {
            throw new LightRuntimeException("File(" + fileInfoView.getName() + ") can't be updated to Permission(negative) with FileCacheType.NGINX.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
        }

        boolean isPathChanged = !fileInfoView.returnRelativePath().equals(oldAndBeenOverwrite.returnRelativePath());

        if (isPathChanged) {
            pathConflictCheck(oldAndBeenOverwrite);
        }

        boolean copyTypeChanged = newOne.getFileCacheType() != null && !fileInfoView.getFileCacheType().equals(newOne.getFileCacheType());

        if (copyTypeChanged) {
            if (fileInfoView.getFileCacheType().equals(FileCacheTypeEnum.DEFAULT)) {
                // 无副本切换到有副本，仅新增副本
                Optional<FileInfo> fileInfoTemp = fileInfoDao.findOne(Example.of(FileInfo.builder().fileNo(fileNo).build()));
                if (fileInfoTemp.isEmpty()) {
                    throw new InternalRuntimeException("FileInfo(" + fileNo + ") was not found.");
                }

                oldAndBeenOverwrite.setContent(fileInfoTemp.get().getContent());

                // 缓存更新流程允许文件覆盖
                FileOperationTool.copyFile(true, getAbsolutePath(oldAndBeenOverwrite), oldAndBeenOverwrite.getName(), oldAndBeenOverwrite.getContent());
            } else {
                // 有副本切换到无副本，仅删除旧副本
                String oldCachePath = fileRootPath + fileInfoView.returnRelativePath();
                File oldFile = new File(oldCachePath);
                FileOperationTool.deleteFile(oldFile);
            }
        } else {
            if (fileInfoView.getFileCacheType().equals(FileCacheTypeEnum.NGINX)) {
                // 未切换副本类型，属于 Nginx，可能存在路径变更
                // 检测是否存在硬盘副本
                String newCachePath = fileRootPath + oldAndBeenOverwrite.returnRelativePath();
                String oldCachePath = fileRootPath + fileInfoView.returnRelativePath();

                File oldFile = new File(oldCachePath);
                if (oldFile.exists()) {
                    File newFile = new File(newCachePath);
                    // 保障所需文件夹被创建
                    fileHelper.getOrCreateDirectoryIfNotExit(fileRootPath + oldAndBeenOverwrite.getFileType().getPath());
                    try {
                        FileCopyUtils.copy(oldFile, newFile);
                        FileOperationTool.deleteFile(oldFile);
                        log.info("Copy file({}) to file({}) success.", oldCachePath, newCachePath);
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
        Optional<FileInfoView> fileInfoViewTemp = findFileViewFromDB(fileNo);

        if (fileInfoViewTemp.isEmpty()) {
            return null;
        }

        FileInfoView fileInfoView = fileInfoViewTemp.get();
        FileInfoDto resultTempItem = fileInfoView.toDto();
        initLink(resultTempItem);

        return resultTempItem;
    }

    public FileInfoInfo findFileInfoPageQuery(List<FileTypeEnum> fileTypes, Integer currentPage, Integer pageSize) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        // 是否有文件查询权限
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        List<FileTypeEnum> actualFileTypes = fileTypes == null ? TYPE_FOR_ALL : fileTypes;

        List<FileInfoDto> fileInfoDtoList = new ArrayList<>();
        List<Integer> activePermissionIdList = permissionService.getActivePermissionIdListOfUser(currentUser, null);

        Sort sort = Sort.by(Sort.Order.asc("createTs"));
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<FileInfoView> resultTemp = fileInfoViewDao.findFileInfoMultiple(actualFileTypes, activePermissionIdList, pageable);

        resultTemp.stream().forEach(item -> {
            FileInfoDto resultTempItem = item.toDto();
            initLink(resultTempItem);
            fileInfoDtoList.add(resultTempItem);
        });

        return FileInfoInfo.builder()
                .fileInfoList(fileInfoDtoList)
                .totalCount(resultTemp.getTotalElements())
                .build();
    }

    private void initLink(FileInfoDto dto) {
        // 初始化 API 链接
        dto.setApiLink(fileUrlBuilder.getFileApiLinkByFileNo(dto.getFileNo()));
        // 检测是否存在硬盘副本
        String cachePath = fileRootPath + dto.getRelativePath();
        File file = new File(cachePath);
        if (file.exists()) {
            dto.setCacheLink(fileUrlBuilder.getFileCacheLinkByRelativePath(dto.getRelativePath()));
        }
    }

    public boolean createFileCopyFromDBToHardDisk(boolean forceOverWrite, String fileNo) {
        Optional<FileInfo> fileInfoTemp = findFileFromDB(fileNo);

        if (fileInfoTemp.isEmpty()) {
            log.warn("FileInfo({}) was empty.", fileNo);
            return false;
        }

        FileInfo fileInfo = fileInfoTemp.get();

        FileOperationResult copyResult = FileOperationTool.copyFile(forceOverWrite, getAbsolutePath(fileInfo), fileInfo.getName(), fileInfo.getContent());

        boolean result = copyResult.isSuccess();

        if (!forceOverWrite) {
            if (!result) {
                log.info(copyResult.getMsg());
            }
        }
        return result;
    }

    public void deleteFile(String fileNo) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        // 是否有文件删除权限
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Optional<FileInfoView> fileInfoViewTemp = fileInfoViewDao.findOne(Example.of(FileInfoView.builder().fileNo(fileNo).build()));

        fileInfoViewTemp.ifPresent((fileInfoView) -> {
            // 检测是否存在硬盘副本
            String cachePath = fileRootPath + fileInfoView.toDto().getRelativePath();

            File file = new File(cachePath);
            if (file.exists()) {
                FileOperationTool.deleteFile(file);
            }

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

    public void createCacheFile(String absolutePath, FileInfo fileInfo) {
        FileOperationResult copyResult = FileOperationTool.copyFile(false, absolutePath, fileInfo.getName(), fileInfo.getContent());
        if (!copyResult.getResultType().equals(FileOperationResult.ResultType.SUCCESS)) {
            if (copyResult.getThrowable() != null) {
                throw new InternalRuntimeException(copyResult.getMsg());
            } else {
                throw new LightRuntimeException(copyResult.getMsg());
            }
        }
    }

    public String getAbsolutePath(FileInfo fileInfo) {
        return fileRootPath + fileInfo.getFileType().getPath() + fileInfo.getName() + "." + fileInfo.getExtension();
    }
}
