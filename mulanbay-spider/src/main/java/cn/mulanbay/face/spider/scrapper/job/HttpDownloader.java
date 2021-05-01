package cn.mulanbay.face.spider.scrapper.job;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.face.spider.common.SpiderErrorCode;
import cn.mulanbay.face.spider.scrapper.SpiderHandler;
import cn.mulanbay.face.spider.scrapper.proxy.MyProxy;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * http下载
 */
public class HttpDownloader {

    private static final Logger logger = LoggerFactory.getLogger(SpiderHandler.class);

    private volatile static HttpDownloader singleton;

    /**
     * 超时时间（毫秒）
     */
    private final int timeOut = 5000;

    private final Set<Integer> defaultAcceptStatCode = Sets.newTreeSet(200);


    private HttpDownloader(){ }

    public static HttpDownloader instance() {
        if (singleton == null) {
            synchronized (HttpDownloader.class) {
                if (singleton == null) {
                    singleton = new HttpDownloader();
                }
            }
        }
        return singleton;
    }

    private CloseableHttpClient getHttpClient(String domain , String userAgent, boolean isUseGzip, Map<String, String> cookie) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        Registry<CookieSpecProvider> reg = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register("easy", new BestMatchSpecFactory())
                .build();
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig)
                .setDefaultCookieSpecRegistry(reg)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(new BasicCookieStore())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true));

        if (userAgent != null) {
            httpClientBuilder.setUserAgent(userAgent);
        } else {
            httpClientBuilder.setUserAgent("");
            //不能设置默认的 UserAgent, 可能导致数据不对. 要根据不同平台来实际设置
//            httpClientBuilder.setUserAgent("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        }

        if (isUseGzip) {
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
                @Override
                public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                    if ( !request.containsHeader("Accept-Encoding") ) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });
        }

        if( null != cookie && cookie.size()>0 ){
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> cookieEntry : cookie.entrySet()) {
                BasicClientCookie basicClientCookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                basicClientCookie.setDomain(domain);
                cookieStore.addCookie(basicClientCookie);
            }
            httpClientBuilder.setDefaultCookieStore(cookieStore);
        }

        CloseableHttpClient client = httpClientBuilder.build();
        return client;
    }

    /**
     * @param method 只能是 "get"或者是"post"
     * @return
     */
    protected HttpUriRequest getHttpRequest(
            String method, String url, Map<String, String> headers, MyProxy proxy, NameValuePair[] postParameters, byte[] bytesParam
    ) {
        RequestBuilder requestBuilder;
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) { //default get
            requestBuilder = RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            requestBuilder = RequestBuilder.post();
            if (postParameters != null && postParameters.length > 0) {
                requestBuilder.addParameters(postParameters);
            }
            if (bytesParam != null && bytesParam.length!= 0){
                ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytesParam);
                requestBuilder.setEntity(byteArrayEntity);
            }
        }else {
            throw new IllegalArgumentException("Illegal HTTP Method " + method);
        }

        requestBuilder.setUri(url);

        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH)
                .setConnectionRequestTimeout(timeOut).setSocketTimeout(timeOut).setConnectTimeout(timeOut);

        if (proxy != null) {
            HttpHost httpHost = new HttpHost(proxy.getHost(),proxy.getPort());
            requestConfigBuilder.setProxy(httpHost);
        }
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    //在此之前都是下载开始之前的相关初始化
    public String download( String url, MyProxy proxy ){
        return download(url, proxy, null, null, null, null, false, null, null, null, null,null);
    }

    public String download (
            String url, MyProxy proxy, String domain,
            Map<String, String> headers, Map<String, String> cookie, String userAgent, boolean isUseGzip,
            String charset, Set<Integer> acceptStatCode, String method, NameValuePair[] postParameters, byte[] bytes
    ) {
        logger.debug("Downloading  Url: " + url + "StartTime:" + System.currentTimeMillis());
        CloseableHttpResponse httpResponse = null;
        int statusCode;
        try {
            HttpUriRequest httpUriRequest = getHttpRequest(method, url, headers, proxy, postParameters,bytes);
            httpResponse = getHttpClient(domain, userAgent, isUseGzip, cookie).execute(httpUriRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            if(statusAccept(acceptStatCode, statusCode)) {
                logger.debug("Downloading  Url Finished: " + url + "EndTime:" + System.currentTimeMillis());
                return handleResponse(charset, httpResponse);
            } else {
                logger.warn("Status Code Error:" + statusCode + " URL:" + url + " Content:" + JSON.toJSONString(httpResponse));
                throw new ApplicationException(SpiderErrorCode.UNACCEPETD_HTTP_CODE,"Status Code Error:" + statusCode + " URL:" + url);
            }
        } catch (IOException e) {
            logger.error("download page " + url + " error", e);
            throw new ApplicationException(SpiderErrorCode.DOWNLOAD_ERROR,"download page " + url + " error");
        } finally {
            try {
                if (httpResponse != null) {
                    EntityUtils.consume(httpResponse.getEntity()); //ensure the connection is released back to pool
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    //在此之前都是下载开始之后的相关处理
    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        if( null == acceptStatCode ){
            return defaultAcceptStatCode.contains(statusCode);
        }else {
            return acceptStatCode.contains(statusCode);
        }
    }

    protected String handleResponse(String charset, HttpResponse httpResponse) throws IOException {
        String content;
        // todo use tools as cpdetector for content decode
        if (charset == null) {
            logger.debug("Charset autodetect failed, use {} as charset. Please specify charset", Charset.defaultCharset());
            content = new String(IOUtils.toByteArray(httpResponse.getEntity().getContent()));
        } else {
            content = IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
        return content;
    }
}
