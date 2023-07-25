package hygge.blog.service.local;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.FileInfo;
import hygge.blog.domain.local.enums.FileTypeEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.User;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.definition.FileHelper;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Service
public class FileServiceImpl extends HyggeWebUtilContainer {
    private static final FileHelper fileHelper = UtilCreator.INSTANCE.getDefaultInstance(FileHelper.class);
    @Value("${file.upload.path}")
    private String filePath;
    @Autowired
    private UserServiceImpl userService;

    public List<FileInfo> uploadFile(FileTypeEnum fileType, List<MultipartFile> filesList) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        List<FileInfo> result = new ArrayList<>();

        for (MultipartFile temp : filesList) {
            String fileName = temp.getOriginalFilename();

            String path = filePath + fileType.getPath() + fileName;
            File file = new File(path);
            if (file.exists()) {
                throw new LightRuntimeException("File(" + fileName + ") was duplicate.", BlogSystemCode.FAIL_TO_UPLOAD_FILE);
            }
            try {
                fileHelper.getOrCreateDirectoryIfNotExit(filePath + fileType.getPath());
                file.createNewFile();
                temp.transferTo(file);
            } catch (Exception e) {
                throw new LightRuntimeException("Fail to upload " + fileName + ".", BlogSystemCode.FAIL_TO_UPLOAD_FILE, e);
            }
            FileInfo item = FileInfo.builder()
                    .src(fileType.getPath() + fileName)
                    .name(fileName)
                    .extension(Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".")))
                    .build();
            item.setFileSizeWithByte(new BigDecimal(file.length()));

            result.add(item);
        }
        return result;
    }

    public List<FileInfo> findFileInfo(List<FileTypeEnum> fileTypes) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        List<FileInfo> result = new ArrayList<>();

        for (FileTypeEnum fileType : fileTypes) {
            String actualPath = filePath + fileType.getPath();
            File file = fileHelper.getOrCreateDirectoryIfNotExit(actualPath);
            List<File> files = fileHelper.getFileFromDirectory(file, (pathname) -> true);

            files.forEach(item -> {
                String absolutePath = item.getAbsolutePath();

                FileInfo fileInfo = FileInfo.builder()
                        .src(absolutePath.substring(filePath.length()))
                        .name(absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1, absolutePath.lastIndexOf(".")))
                        .build();

                fileInfo.setSrc(fileInfo.getSrc().replace(File.separator,"/"));

                fileInfo.setFileSizeWithByte(new BigDecimal(item.length()));
                result.add(fileInfo);
            });
        }
        return result;
    }

}
