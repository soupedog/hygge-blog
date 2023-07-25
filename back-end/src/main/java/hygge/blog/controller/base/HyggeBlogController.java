package hygge.blog.controller.base;

import hygge.blog.domain.local.bo.HyggeBlogControllerResponse;
import hygge.commons.constant.enums.GlobalHyggeCodeEnum;
import hygge.commons.template.definition.HyggeInfo;
import hygge.web.template.definition.HyggeController;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Xavier
 * @date 2022/7/17
 */
public interface HyggeBlogController<R extends ResponseEntity<?>> extends HyggeController<R> {
    HyggeControllerResponseWrapper<?> LIGHT_ERROR_WRAPPER = (httpStatus, headers, hyggeCode, msg, entity, throwable) -> {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(httpStatus);
        builder.contentType(MediaType.APPLICATION_JSON);
        HyggeBlogControllerResponse<?> hyggeControllerResponse = new HyggeBlogControllerResponse<>();
        hyggeControllerResponse.setCode(hyggeCode.getCode());
        hyggeControllerResponse.setMsg(msg != null ? msg : hyggeCode.getPublicMessage());

        ResponseEntity<?> response;
        if (headers != null) {
            response = builder.headers(headers).body(hyggeControllerResponse);
        } else {
            response = builder.body(hyggeControllerResponse);
        }
        return response;
    };

    /**
     * 请求类型不对
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    default ResponseEntity<?> requestHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return fail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, GlobalHyggeCodeEnum.CLIENT_END_EXCEPTION, e.getMessage(), null, e, (HyggeControllerResponseWrapper<R>) LIGHT_ERROR_WRAPPER);
    }

    /**
     * 请求内容不能解析
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    default ResponseEntity<?> requestHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return fail(HttpStatus.BAD_REQUEST, null, GlobalHyggeCodeEnum.CLIENT_END_EXCEPTION, e.getMessage(), null, e, (HyggeControllerResponseWrapper<R>) LIGHT_ERROR_WRAPPER);
    }

    /**
     * 请求类型不支持
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    default ResponseEntity<?> requestHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return fail(HttpStatus.METHOD_NOT_ALLOWED, null, GlobalHyggeCodeEnum.CLIENT_END_EXCEPTION, e.getMessage(), null, e, (HyggeControllerResponseWrapper<R>) LIGHT_ERROR_WRAPPER);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({TypeMismatchException.class})
    default ResponseEntity<?> requestTypeMismatch(TypeMismatchException e) {
        return fail(HttpStatus.BAD_REQUEST, null, GlobalHyggeCodeEnum.CLIENT_END_EXCEPTION, e.getMessage(), null, e, (HyggeControllerResponseWrapper<R>) LIGHT_ERROR_WRAPPER);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    default ResponseEntity<?> requestMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return fail(HttpStatus.BAD_REQUEST, null, GlobalHyggeCodeEnum.CLIENT_END_EXCEPTION, e.getMessage(), null, e, (HyggeControllerResponseWrapper<R>) LIGHT_ERROR_WRAPPER);
    }

    /**
     * 取消非严重异常的日志打印
     */
    @Override
    default boolean printNonSeriousExceptionLog(HyggeInfo exception) {
        return false;
    }

    /**
     * 只要服务端进行了响应 HttpStatus 固定为 200
     *
     * @param httpStatus 预期状态码
     * @return HttpStatus 固定为 200 响应构造器
     */
    @Override
    default ResponseEntity.BodyBuilder getBuilder(HttpStatus httpStatus) {
        ResponseEntity.BodyBuilder result = ResponseEntity.status(HttpStatus.OK);
        result.contentType(MediaType.APPLICATION_JSON);
        return result;
    }
}
