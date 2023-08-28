package hygge.blog.config.util.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import hygge.commons.exception.UtilRuntimeException;
import hygge.web.util.http.bo.HttpResponse;
import hygge.web.util.http.configuration.HttpHelperRequestConfiguration;
import hygge.web.util.http.definition.HttpHelperLogger;
import hygge.web.util.http.definition.HttpHelperResponseEntityReader;
import hygge.web.util.http.definition.HttpHelperRestTemplateFactory;
import hygge.web.util.http.impl.DefaultHttpHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Xavier
 * @date 2023/8/28
 */
public class HttpHelperForSpringBoot3 extends DefaultHttpHelper {
    public HttpHelperForSpringBoot3(HttpHelperRestTemplateFactory httpHelperRestTemplateFactory, HttpHelperLogger httpHelperLogger, HttpHelperResponseEntityReader httpHelperResponseEntityReader) {
        super(httpHelperRestTemplateFactory, httpHelperLogger, httpHelperResponseEntityReader);
    }

    @Override
    public <R, E, T> HttpResponse<R, T> sendRequest(HttpHelperRequestConfiguration configuration, String url, HttpMethod httpMethod, HttpHeaders requestHeaders, R requestObject, Class<E> responseClassInfo, Object dataClassInfo) {
        if (configuration == null) {
            configuration = httpHelperRestTemplateFactory.getDefaultConfiguration();
        }

        Long startTs = System.currentTimeMillis();
        HttpResponse<R, T> result = new HttpResponse<>(startTs, url, httpMethod.name());
        RestTemplate restTemplate = httpHelperRestTemplateFactory.getInstance(configuration);
        E originalResponse = null;

        try {
            HttpEntity<R> requestEntity = new HttpEntity<>(requestObject, requestHeaders);
            result.setRequestHeaders(requestHeaders);
            result.setRequestData(requestObject);
            ResponseEntity<E> responseEntity = restTemplate.exchange(url, httpMethod, requestEntity, responseClassInfo);
            result.initResponse(responseEntity.getStatusCode().value(), responseEntity.getHeaders());
            originalResponse = responseEntity.getBody();
            @SuppressWarnings("unchecked")
            T data = (T) httpHelperResponseEntityReader.readAsObjectSmart(originalResponse, dataClassInfo);
            result.setData(data);
        } catch (JsonProcessingException e) {
            result.setExceptionOccurred(true);
            if (originalResponse instanceof String) {
                result.setOriginalResponse((String) originalResponse);
            }
        } catch (Exception e) {
            result.setExceptionOccurred(true);
            throw new UtilRuntimeException("HttpHelper fail to request.", e);
        } finally {
            httpHelperLogger.logOutput(configuration, result);
        }
        return result;
    }
}
