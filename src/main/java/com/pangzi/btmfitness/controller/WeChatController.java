package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.entity.BtmAccount;
import com.pangzi.btmfitness.entity.FitnessUser;
import com.pangzi.btmfitness.service.BtmAccountService;
import com.pangzi.btmfitness.service.FitnessUserService;
import com.pangzi.btmfitness.utils.JwtUtils;
import com.pangzi.btmfitness.utils.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.pangzi.btmfitness.utils.JwtUtils.USER_NAME;

/**
 * @author zhangxuewen
 * @date 2018.09.28
 */
@RestController
@RequestMapping(value = "v1/mini")
public class WeChatController {

    @Autowired
    private WeChatUtil weChatUtil;
    @Autowired
    private FitnessUserService fitnessUserService;
    @Autowired
    private BtmAccountService btmAccountService;
    @Autowired
    private BtmApiService btmApiService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String iv = request.getParameter("iv");
        String code = request.getParameter("code");
        String userInfo = request.getParameter("userInfo");
        String encryptedData = request.getParameter("encryptedData");
        if (StringUtils.isBlank(iv) || StringUtils.isBlank(code) ||
                StringUtils.isBlank(userInfo) || StringUtils.isBlank(encryptedData)) {
            object.put("code", 500);
            object.put("msg", "params is error.");
            return object.toJSONString();
        }
        JSONObject info = JSON.parseObject(userInfo);
        String openId = weChatUtil.getOpenId(code);
        if (openId == null) {
            object.put("code", 500);
            object.put("msg", "微信解密出错.");
            return object.toJSONString();
        }
        FitnessUser user = fitnessUserService.getByOpenId(openId);
        if (user == null) {
            FitnessUser fitnessUser = new FitnessUser();
            setUser(fitnessUser, info);
            fitnessUser.setOpenId(openId);
            fitnessUserService.save(fitnessUser);
        }
        setUser(user, info);
        fitnessUserService.updateWithId(user);
        object.put("code", 200);
        object.put("msg", "success");
        Map<String, Object> map = new HashMap<>(16);
        map.put(USER_NAME, openId);
        String token = JwtUtils.createJWT(map, null);
        BtmAccount btmAccount = btmAccountService.get(new BtmAccount(openId));
        object.put("token", token);
        object.put("openId", openId);
        if (btmAccount == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("BTM", "0.00");
            jsonObject.put("FITNESS", "0.00");
            object.put("asset", jsonObject);
            return object.toJSONString();
        }
        JSONObject asset = btmApiService.getBalanceByAccount(btmAccount.getAccountAlias());
        asset.put("BTM", new BigDecimal(asset.getString("BTM")).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        asset.put("FITNESS", new BigDecimal(asset.getString("FITNESS")).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        object.put("asset", asset);

        return object.toJSONString();
    }

    private FitnessUser setUser(FitnessUser user, JSONObject object) {
        user.setAvatarUrl(object.getString("avatarUrl"));
        user.setCity(object.getString("city"));
        user.setCountry(object.getString("country"));
        switch (object.getString("gender")) {
            case "1":
                user.setGender("男");
                break;
            case "2":
                user.setGender("女");
                break;
            case "0":
                user.setGender("未知");
                break;
            default:
        }
        user.setLanguage(object.getString("language"));
        user.setNickName(object.getString("nickName"));
        user.setProvince(object.getString("province"));
        return user;
    }
}
