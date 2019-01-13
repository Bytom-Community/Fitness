package com.pangzi.btmfitness.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zxw
 * @date 2018-4-10
 */
public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);
    private static final String UN_KNOWN = "unKnown";

    /**
     * 通过request获取客户端IP
     *
     * @param request
     * @return
     */
    public static String getIpFromHttpRequest(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ip) && !UN_KNOWN.equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !UN_KNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getRemoteAddr();

        return ip;
    }

    public static boolean isIpV4(String ipv4) {
        String regExp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(ipv4);
        return matcher.matches();
    }
}
