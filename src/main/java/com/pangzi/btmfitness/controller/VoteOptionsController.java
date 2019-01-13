package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.entity.VoteOptions;
import com.pangzi.btmfitness.service.VoteOptionsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zxw
 * @date 2018-11-10
 */
@RestController
@RequestMapping(value = "v1/mini/voting")
public class VoteOptionsController {

    @Autowired
    private VoteOptionsService optionsService;

    @RequestMapping(value = "options")
    public String getOptions(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("vote");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(id)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        Long vote = Long.valueOf(id);
        List<VoteOptions> list = optionsService.findList(new VoteOptions(vote));
        object.put("code", 200);
        object.put("msg", "success");
        object.put("list", list);
        return object.toJSONString();
    }
}
