package com.pangzi.btmfitness.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhangxuewen
 * @date 2018-10-01
 */

@Service
public class WeChatUtil {

    @Value("${weChat.app.id}")
    private String appId;
    @Value("${weChat.app.secret}")
    private String secret;
    @Value("${weChat.app.grant.type}")
    private String grantType;
    private String url = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private AesCbcUtil aesCbcUtil;

    Logger logger = LoggerFactory.getLogger(getClass());


    public String getOpenId(String code) {
        String params = "appid=" + appId +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=" + grantType;
        String str = RequestUtil.httpsRequest(url, "GET", params);
        try {
            JSONObject result = JSONObject.parseObject(str);
            logger.info("getOpenId:{}",result);
            String openId = result.get("openid").toString();
            return openId;
        } catch (Exception e) {
            return null;
        }
    }

    public String decodeEncryptedData(String code, String data, String iv) {
        String params = "appid=" + appId +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=" + grantType;
        String str = RequestUtil.httpsRequest(url, "GET", params);
        JSONObject object = JSON.parseObject(str);
        String sessionKey = object.getString("session_key");
        String result = aesCbcUtil.decrypt(data, sessionKey, iv, "UTF-8");
        if (result == null) {
            return null;
        }
        return result;
    }

}
