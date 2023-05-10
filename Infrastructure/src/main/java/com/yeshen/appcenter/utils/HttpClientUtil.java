package com.yeshen.appcenter.utils;

import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

/**
 * Date 2022/05/09  9:53
 * author  by HuBingKuan
 */
@Slf4j
public class HttpClientUtil {
    private static RestTemplate restTemplate = SpringContextUtil.getBean(RestTemplate.class);

    /**
     * @param url          请求的url
     * @param responseType 返回的响应类型
     * @return
     */
    public static <T> T getRequest(String url, Class<T> responseType) {
        return execute(HttpMethod.GET, url, null, null, responseType);
    }

    /**
     * @param url          请求的url
     * @param headerMap    请求头
     * @param map          url后面的参数 ?key={value}
     * @param responseType 返回的响应类型
     * @return
     */
    public static <T> T getRequest(String url, MultiValueMap<String, String> headerMap, Map<String, Object> map, Class<T> responseType) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!ObjectUtils.isEmpty(map)) {
            Set<String> keys = map.keySet();
            for (String name : keys) {
                uriComponentsBuilder.queryParam(name, map.get(name));
            }
        }
        return execute(HttpMethod.GET, uriComponentsBuilder.build().encode().toUriString(), headerMap, null, responseType);
    }

    /**
     * @param url          请求的url
     * @param responseType 返回的响应类型
     * @return
     */
    public static <T> T postRequest(String url, Class<T> responseType) {
        return execute(HttpMethod.POST, url, null, null, responseType);
    }

    /**
     * @param url          请求地址
     * @param headerMap    请求头(map中的value如果是list形式，请转换为json)
     * @param param        请求体(map中的value如果是list形式，请转换为json)
     * @param responseType 返回的响应类型
     * @return
     */
    public static <T> T postRequest(String url, @Nullable MultiValueMap<String, String> headerMap, @Nullable Map<String, Object> param, Class<T> responseType) {
        return execute(HttpMethod.POST, url, headerMap, param, responseType);
    }

    /**
     * @param url          请求地址
     * @param requestBody  请求体(Json格式)
     * @param responseType 返回的响应类型
     * @return
     */
    public static <T> T postRequest(String url, String requestBody, Class<T> responseType) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        return restTemplate.postForObject(url, requestEntity, responseType);
    }

    /**
     * @param httpMethod   请求方法
     * @param url          请求的url
     * @param headerMap    请求头
     * @param param        请求体
     * @param responseType 返回的响应类型
     * @return
     */
    public static <T> T execute(HttpMethod httpMethod, String url, MultiValueMap<String, String> headerMap, Map<String, Object> param, Class<T> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!ObjectUtils.isEmpty(headerMap)) {
            httpHeaders.addAll(headerMap);
        }
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(param, httpHeaders);
        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, requestEntity, responseType);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("请求成功,状态码:{},Content-Type:{}", response.getStatusCodeValue(), response.getHeaders().getContentType());
        } else {
            log.info("请求失败,状态码:{},Content-Type:{}", response.getStatusCodeValue(), response.getHeaders().getContentType());
        }
        return response.getBody();
    }

    public static ResponseEntity<byte[]> getPhotoBytesByUrl(String url) {
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
        // 如果图片连接失效则失败
        if (response.getHeaders().getContentType().toString().startsWith("text/html")) {
            log.warn("链接URL非图片:{}", url);
            throw new BusinessException(ResultCode.DOWNLOAD_ERROR);
        }
        return response;
    }
}