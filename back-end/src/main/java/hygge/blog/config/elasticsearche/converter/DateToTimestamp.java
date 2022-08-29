package hygge.blog.config.elasticsearche.converter;

import hygge.blog.elasticsearch.dto.FuzzySearchCache;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

/**
 * @author Xavier
 * @date 2022/8/29
 * {@link FuzzySearchCache#createTs} 时间字段已经强制指定为 DateFormat.epoch_millis，所以从 ES 读取回来变成了 Date 类型，需要一个读过程转换器，把 Date 转换成 Timestamp
 */
@ReadingConverter
public class DateToTimestamp implements Converter<Date, Timestamp> {

    @Override
    public Timestamp convert(Date source) {
        return Timestamp.from(Instant.ofEpochMilli(source.getTime()));
    }
}
