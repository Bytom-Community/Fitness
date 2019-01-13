package com.pangzi.btmfitness.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

/**
 * @Author seven
 */
public class RequestUtil {

    Logger logger = LoggerFactory.getLogger(getClass());
    private static String http = "http";
    private static String https = "https";

    public static String appendHttpString(String url) {
        if (!url.startsWith(http)) {
            url = "http://" + url;
        }
        return url;
    }

    public static String appendHttpsString(String url) {
        if (!url.startsWith(https)) {
            url = "https://" + url;
        }
        return url;
    }

    public static String httpsRequest(String requestUrl, String requestMethod, String params) {
        StringBuffer buffer = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] trustManagers = {new MyX509TrustManager()};
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            URL url = new URL(appendHttpsString(requestUrl));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod);
            connection.setSSLSocketFactory(sslSocketFactory);
            connection.connect();
            if (StringUtils.isNotBlank(params)) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params.getBytes("utf-8"));
                outputStream.close();
            }
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            buffer = new StringBuffer();
            String line = null;
            while (StringUtils.isNotBlank(line = bufferedReader.readLine())) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
