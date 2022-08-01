package hygge.blog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Xavier
 * @date 2022/8/1
 */
@Getter
@Setter
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    /**
     * byte → mb 进制
     */
    public static final BigDecimal byteToMb = new BigDecimal(1048576);

    private String src;
    private String name;
    private String extension;
    private BigDecimal fileSize;

    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize.setScale(2, RoundingMode.FLOOR);
    }

    public void setFileSizeWithByte(BigDecimal fileSizeByte) {
        this.fileSize = fileSizeByte.divide(byteToMb, 2, RoundingMode.FLOOR);
    }
}
