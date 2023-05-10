package com.yeshen.appcenter.config.httpclient;


import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date 2022/05/09  10:36
 * author  by HuBingKuan
 */
@Slf4j
@Configuration
public class RestTemplateConfig {
    private final static int MAX_TOTAL = 300;
    private final static int MAX_PER_ROUTE = 100;
    private final static int MAX_WAIT_TIMEOUT = 500;
    private final static int CONNECT_TIMEOUT = 5000;
    private final static int READ_TIMEOUT = 5000;
    private final static int RETRY_COUNT = 3;


    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyManagementException {
        //添加内容转换器
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        //设置编码格式
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        HttpMessageConverter<?> converter = null;
        //遍历看看有没有默认的编码格式
        for (HttpMessageConverter<?> item : converterList) {
            if (item.getClass() == StringHttpMessageConverter.class) {
                converter = item;
                break;
            }
        }
        //移除默认的编码格式
        if (converter != null) {
            converterList.remove(converter);
        }
        converterList.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                String errorMessage = "status : " + response.getStatusCode() + ", body: " + new String(StreamUtils.copyToByteArray(response.getBody()));
                log.warn("Http调用异常:{}", errorMessage);
                throw new BusinessException(ResultCode.REQUEST_ERROR, errorMessage);
            }
        });
        restTemplate.setInterceptors(Arrays.asList(new RestTemplateRequestInterceptor()));
        log.info("RestTemplate初始化完成");
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() throws NoSuchAlgorithmException, KeyManagementException {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() throws KeyManagementException, NoSuchAlgorithmException {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            // 该方法检查客户端的证书，若不信任该证书则抛出异常。由于我们不需要对客户端进行认证，
            // 因此我们只需要执行默认的信任管理器的这个方法(表示接受任意客户端证书)。JSSE中，默认的信任管理器类为TrustManager。
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates,
                                           String s) throws CertificateException {
            }

            // 该方法检查服务器的证书，若不信任该证书同样抛出异常。通过自己实现该方法，可以使之信任我们指定的任何证书。
            // 在实现该方法时，也可以简单的不做任何处理，即一个空的函数体，由于不会抛出异常，它就会信任任何证书.(表示接受任何服务端证书)
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates,
                                           String s) throws CertificateException {
            }

            // 返回受信任的X509证书数组。
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } }, new SecureRandom());
        //设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                // NoopHostnameVerifier.INSTANCE:不去验证ssl
                .register("https", new SSLConnectionSocketFactory(
                        sslContext,
                        NoopHostnameVerifier.INSTANCE))
                .build();
        //设置长连接最大生存时间30秒
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        //设置连接池最大连接数
        connectionManager.setMaxTotal(MAX_TOTAL);
        // 为每个域名设置单独的连接池数量
        //connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("www.test.cn")),100);
        //设置同路由的最大并发数
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        //RequestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                //从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(MAX_WAIT_TIMEOUT)
                //连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectTimeout(CONNECT_TIMEOUT)
                //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setSocketTimeout(READ_TIMEOUT)
                // cookie策略  DEFAULT:无法写入cookie    IGNORE_COOKIES:忽略cookie访问
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        SocketConfig socketConfig = SocketConfig.custom()
                // 开启监视TCP连接是否有效
                .setSoKeepAlive(true)
                // 是否立即发送数据 设置为true会关闭Socket缓冲 默认false
                .setTcpNoDelay(true)
                // 是否可以在一个进程关闭Socket后，即使它还没有释放端口 其他进程还可以重用端口
                .setSoReuseAddress(true)
                // 接收数据的等待超时时间,单位ms
                //.setSoTimeout(500)
                // 关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
                //.setSoLinger(60)
                .build();
        //添加headers
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN"));
        headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        headers.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
        ConnectionKeepAliveStrategy keepAliveStrategy = new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                // 优先使用服务端提供的timeout
                HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && "timeout".equalsIgnoreCase(param)) {
                        try {
                            return Long.parseLong(value) * 1000;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }
/*
                设置特定的域名的连接时间
                HttpHost target = (HttpHost) context.getAttribute(
                        HttpClientContext.HTTP_TARGET_HOST);
                if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
                    return 5 * 1000;
                }*/
                return 5 * 1000;
            }
        };
        // 开启回收空闲连接的线程
        new IdleConnectionMonitorThread(connectionManager).start();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setDefaultSocketConfig(socketConfig)
                .setDefaultHeaders(headers)
                // 长连接配置
                .setKeepAliveStrategy(keepAliveStrategy)
                //重试次数，空参默认是3次，没有开启(false)
                .setRetryHandler(new MyHttpRequestRetryHandler(RETRY_COUNT, true))
                .build();
    }
}