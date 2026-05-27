package hygge.blog.service.local.inner.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Xavier
 * @date 2026/5/21
 */
@Slf4j
public class FileOperationTool {
    /**
     * 将传入的文件拷贝到指定目录，自动验证并创建绝对路径之前的父目录
     * 异常不会自动向外抛出，而是记录到 FileOperationResult，调用方需要自行处理
     *
     * @param forceOverWrite 如果为 ture，当文件已存在时会先删除旧的再写入
     * @param absolutePath   例如 G:\Pictures\图片0.jpg
     * @param fileName       例如 图片0，用于在失败时构造错误信息
     * @param content        目标文件内容二进制数组
     * @return 文件操作结果
     */
    public static FileOperationResult copyFile(boolean forceOverWrite, String absolutePath, String fileName, byte[] content) {
        // 参数校验
        if (absolutePath == null || absolutePath.trim().isEmpty()) {
            return FileOperationResult.builder()
                    .resultType(FileOperationResult.ResultType.FAIL)
                    .msg("文件路径不能为空" + (fileName != null ? "，文件名：" + fileName : ""))
                    .throwable(null)
                    .build();
        }

        if (content == null || content.length == 0) {
            String msg = "文件内容为空" + (fileName != null ? "，文件名：" + fileName : "");
            return FileOperationResult.builder()
                    .resultType(FileOperationResult.ResultType.FAIL)
                    .msg(msg)
                    .throwable(null)
                    .build();
        }

        try {
            // 规范化路径
            Path targetPath = Paths.get(absolutePath).normalize();

            // 检查文件是否已存在
            if (Files.exists(targetPath)) {
                if (forceOverWrite) {
                    deleteFile(absolutePath);
                } else {
                    return FileOperationResult.builder()
                            .resultType(FileOperationResult.ResultType.ALREADY_EXISTS)
                            .msg("文件已存在" + (fileName != null ? "，文件名：" + fileName : "") + "，路径：" + targetPath)
                            .throwable(null)
                            .build();
                }
            }

            // 创建父目录（如果不存在）
            Path parentPath = targetPath.getParent();
            if (parentPath != null && !Files.exists(parentPath)) {
                try {
                    Files.createDirectories(parentPath);
                } catch (IOException e) {
                    return FileOperationResult.builder()
                            .resultType(FileOperationResult.ResultType.FAIL)
                            .msg("创建父目录失败" + (fileName != null ? "，文件名：" + fileName : "") + "，目录：" + parentPath)
                            .throwable(e)
                            .build();
                }
            }

            // 使用 Spring FileCopyUtils 拷贝文件
            FileCopyUtils.copy(content, targetPath.toFile());

            return FileOperationResult.builder()
                    .resultType(FileOperationResult.ResultType.SUCCESS)
                    .msg("文件保存成功" + (fileName != null ? "，文件名：" + fileName : "") + "，路径：" + targetPath)
                    .throwable(null)
                    .build();
        } catch (IOException e) {
            String msg = "文件操作失败" + (fileName != null ? "，文件名：" + fileName : "") + "：" + e.getMessage();
            return FileOperationResult.builder()
                    .resultType(FileOperationResult.ResultType.FAIL)
                    .msg(msg)
                    .throwable(e)
                    .build();
        } catch (Exception e) {
            String msg = "未知错误" + (fileName != null ? "，文件名：" + fileName : "") + "：" + e.getMessage();
            return FileOperationResult.builder()
                    .resultType(FileOperationResult.ResultType.FAIL)
                    .msg(msg)
                    .throwable(e)
                    .build();
        }
    }

    public static boolean deleteFile(String absolutePath) {
        if (absolutePath == null || absolutePath.isEmpty()) {
            return false;
        }
        File file = new File(absolutePath);
        return deleteFile(file);
    }

    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        }
        boolean deleteSuccess = file.delete();
        if (!deleteSuccess) {
            log.info("Delete file({}) in HardDisk failed.", file.getPath());
        } else {
            log.info("Delete file({}) in HardDisk success.", file.getPath());
        }
        return deleteSuccess;
    }
}