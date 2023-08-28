package hygge.blog.config.util.http;

import hygge.commons.exception.UtilRuntimeException;
import hygge.web.util.http.configuration.HttpHelperConfiguration;
import hygge.web.util.http.configuration.HttpHelperRequestConfiguration;
import hygge.web.util.http.impl.DefaultHttpHelperRestTemplateFactory;
import hygge.web.util.http.impl.RestTemplateKeeper;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

/**
 * @author Xavier
 * @date 2023/8/28
 */
public class HttpHelperRestTemplateFactoryForSpringBoot3 extends DefaultHttpHelperRestTemplateFactory {
    public HttpHelperRestTemplateFactoryForSpringBoot3(HttpHelperConfiguration httpHelperConfiguration) {
        super(httpHelperConfiguration);
    }

    public HttpHelperRestTemplateFactoryForSpringBoot3(HttpHelperConfiguration httpHelperConfiguration, RestTemplateKeeper restTemplateKeeper) {
        super(httpHelperConfiguration, restTemplateKeeper);
    }

    @Override
    public RestTemplate getInstance(HttpHelperRequestConfiguration config) {
        if (config == null) {
            config = defaultRequestConfiguration;
        }

        RestTemplate result = restTemplateKeeper.getValue(config);
        if (result == null) {
            result = newInstance(config);
        }
        return result;
    }

    private synchronized RestTemplate newInstance(HttpHelperRequestConfiguration config) {
        RestTemplate result = restTemplateKeeper.getValue(config);
        if (result != null) {
            return result;
        }

        HttpClientBuilder httpClientBuilder = HttpClientBuilder
                .create()
                .setConnectionManager(getHttpClientConnectionManager(config));

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClientBuilder.build());
        httpRequestFactory.setConnectTimeout(parameterHelper.integerFormat("connectTimeOutMilliseconds", config.connectTimeOutMilliseconds()));

        result = new RestTemplate(httpRequestFactory);

        toSupportUTF8(result);
        result.setErrorHandler(DEFAULT_RESPONSE_ERROR_HANDLER);

        restTemplateKeeper.saveValue(config, result);
        return result;
    }

    private HttpClientConnectionManager getHttpClientConnectionManager(HttpHelperRequestConfiguration config) {
        PoolingHttpClientConnectionManager poolingConnectionManager;

        if (config.ignoreSSL()) {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext;
            try {
                sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            } catch (Exception e) {
                throw new UtilRuntimeException("Fail to init DefaultHttpHelperRestTemplateFactory.", e);
            }
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", connectionSocketFactory)
                    .build();
            poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            poolingConnectionManager = new PoolingHttpClientConnectionManager();
        }

        SocketConfig.Builder builder = SocketConfig.custom();

        SocketConfig socketConfig = builder.setSoTimeout(parameterHelper.integerFormat("readTimeOutMilliseconds", config.readTimeOutMilliseconds()), TimeUnit.MILLISECONDS)
                .build();

        poolingConnectionManager.setDefaultSocketConfig(socketConfig);

        poolingConnectionManager.setMaxTotal(httpHelperConfiguration.getConnection().getMaxTotal());
        poolingConnectionManager.setDefaultMaxPerRoute(httpHelperConfiguration.getConnection().getMaxPerRoute());
        return poolingConnectionManager;
    }
}
