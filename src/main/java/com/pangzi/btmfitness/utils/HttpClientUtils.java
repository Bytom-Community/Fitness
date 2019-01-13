package com.pangzi.btmfitness.utils;

import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.entity.RpcConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.*;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author 胖子
 * 依赖的jar包有：commons-lang-2.6.jar、httpclient-4.3.2.jar、httpcore-4.3.1.jar、commons-io-2.4.jar
 */
public class HttpClientUtils {

    public static final int CONNECT_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;
    public static final String DEFAULT_CHARSET = "UTF-8";
    private static HttpClient HTTP_CLIENT = null;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        HTTP_CLIENT = HttpClients.custom().setConnectionManager(cm).build();
    }

    public static String postParameters(String url, String parameterStr) throws ConnectTimeoutException, SocketTimeoutException, Exception {
        return post(url, parameterStr, null, "application/x-www-form-urlencoded", DEFAULT_CHARSET, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    public static String postParameters(String url, Map<String, String> params) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    public static String postParameters(String url, Map<String, String> params, Integer timeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, timeout, timeout);
    }

    public static String get(String url) throws Exception {
        return get(url, null, null, DEFAULT_CHARSET, null, null);
    }

    /**
     * 模拟浏览器提交
     */
    public static String getImitateBrowser(String url, int timeout) throws Exception {
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
        return get(url, null, headers, DEFAULT_CHARSET, timeout, timeout);
    }

    /**
     * 模拟浏览器提交
     */
    public static String postJsonImitateBrowser(String url, TreeMap<String, String> params, int timeout) throws ConnectTimeoutException, SocketTimeoutException, Exception {
        String jsonStr = JSONObject.toJSONString(params);
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
        return post(url, jsonStr, headers, "application/json", "utf-8", timeout, timeout);
    }

    public static String get(String url, int timeout) throws Exception {
        return get(url, null, null, DEFAULT_CHARSET, timeout, timeout);
    }

    public static String get(String url, Map<String, String> headers, Integer timeout) throws Exception {
        return get(url, null, headers, DEFAULT_CHARSET, timeout, timeout);
    }

    public static String postJson(String url, TreeMap<String, String> params, int timeout) throws ConnectTimeoutException, SocketTimeoutException, Exception {
        String jsonStr = JSONObject.toJSONString(params);
        return post(url, jsonStr, null, "application/json", "utf-8", timeout, timeout);
    }

    public static String postJson(String url, String json, int timeout) throws ConnectTimeoutException, SocketTimeoutException, Exception {
        return post(url, json, null, "application/json", "utf-8", timeout, timeout);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url
     * @param body        RequestBody
     * @param mimeType    例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @param charset     编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException  响应超时
     * @throws Exception
     */
    public static String post(String url, String body, Map<String, String> headers, String mimeType, String charset, Integer connTimeout, Integer readTimeout)
            throws ConnectTimeoutException, SocketTimeoutException, Exception {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.HTTP_CLIENT;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }


    /**
     * 提交form表单
     *
     * @param url
     * @param params
     * @param connTimeout
     * @param readTimeout
     * @return
     * @throws ConnectTimeoutException
     * @throws SocketTimeoutException
     * @throws Exception
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                Set<Entry<String, String>> entrySet = params.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                post.setEntity(entity);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse res = null;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.HTTP_CLIENT;
                res = client.execute(post);
            }
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }


    /**
     * 发送一个 GET 请求
     *
     * @param url
     * @param charset
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return
     * @throws ConnectTimeoutException 建立链接超时
     * @throws SocketTimeoutException  响应超时
     * @throws Exception
     */
    public static String get(String url, TreeMap<String, String> params, Map<String, String> headers, String charset, Integer connTimeout, Integer readTimeout)
            throws ConnectTimeoutException, SocketTimeoutException, Exception {

        if (params != null) {
            StringBuffer queryString = new StringBuffer("");
            Set<String> keys = params.keySet();
            for (String key : keys) {
                queryString.append("&").append(key).append("=").append(params.get(key));
            }
            if (queryString.length() != 0) {
                queryString.deleteCharAt(0);
                url = url + "?" + queryString.toString();
            }
        }

        HttpClient client = null;
        HttpGet get = new HttpGet(url);
        String result = "";
        try {
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());

            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    get.addHeader(entry.getKey(), entry.getValue());
                }
            }

            HttpResponse res = null;

            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.HTTP_CLIENT;
                res = client.execute(get);
            }

            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            get.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }


    /**
     * 从 response 里获取 DEFAULT_CHARSET
     *
     * @param ressponse
     * @return
     */
    @SuppressWarnings("unused")
    private static String getCharsetFromResponse(HttpResponse ressponse) {
        // Content-Type:text/html; DEFAULT_CHARSET=GBK
        if (ressponse.getEntity() != null && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null) {
            String contentType = ressponse.getEntity().getContentType().getValue();
            if (contentType.contains("DEFAULT_CHARSET=")) {
                return contentType.substring(contentType.indexOf("DEFAULT_CHARSET=") + 8);
            }
        }
        return null;
    }


    /**
     * 创建 SSL连接
     *
     * @return
     * @throws GeneralSecurityException
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl)
                        throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert)
                        throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns,
                                   String[] subjectAlts) throws SSLException {
                }

            });

            return HttpClients.custom().setSSLSocketFactory(sslsf).build();

        } catch (GeneralSecurityException e) {
            throw e;
        }
    }

    public static String getQueryString(TreeMap<String, String> parameters) {
        StringBuilder parameter = new StringBuilder();
        for (Entry<String, String> map : parameters.entrySet()) {
            if (parameter.length() > 0) {
                parameter.append("&");
            }
            parameter.append(map.getKey()).append("=").append(map.getValue());
        }
        return parameter.toString();
    }

    public static String getQueryString(Map<String, String> parameters) {
        StringBuilder parameter = new StringBuilder();
        for (Entry<String, String> map : parameters.entrySet()) {
            if (parameter.length() > 0) {
                parameter.append("&");
            }
            parameter.append(map.getKey()).append("=").append(map.getValue());
        }
        return parameter.toString();
    }

    /**
     * 构造rpc请求
     *
     * @param requestBody 请求数据
     * @return
     */
    public static String rpcRequest(String requestBody, RpcConfig rpcConfig) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(rpcConfig.getRpcUsername(), rpcConfig.getRpcPassword().toCharArray());
            }
        });
        String contentType = "application/json";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(rpcConfig.getRpcUrl());
            connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(requestBody.getBytes().length));
            connection.setUseCaches(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            OutputStream out = connection.getOutputStream();
            out.write(requestBody.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            if (connection != null) {
                connection.disconnect();
            }
            return null;
        }
        StringBuffer response = new StringBuffer();
        try {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
            } else {
                connection.disconnect();
            }
            return String.valueOf(response);
        } catch (IOException e) {
//            LOG.error("Rpc get hash code error: {}", e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {

        try {
            String str = post("https://localhost:443/ssl/test.shtml",
                    "name=12&page=34",
                    null,
                    "application/x-www-form-urlencoded",
                    "UTF-8",
                    10000,
                    10000);

            //String str= get("https://localhost:443/ssl/test.shtml?name=12&page=34","GBK");
            /*Map<String,String> map = new HashMap<String,String>();
            map.put("name", "111");
            map.put("page", "222");
            String str= postForm("https://localhost:443/ssl/test.shtml",map,null, 10000, 10000);*/
            System.out.println(str);
        } catch (ConnectTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}