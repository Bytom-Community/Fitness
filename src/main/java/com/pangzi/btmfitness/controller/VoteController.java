package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.common.PageQuery;
import com.pangzi.btmfitness.entity.VoteOptions;
import com.pangzi.btmfitness.entity.VoteRecord;
import com.pangzi.btmfitness.entity.VoteTheme;
import com.pangzi.btmfitness.service.VoteOptionsService;
import com.pangzi.btmfitness.service.VoteRecordService;
import com.pangzi.btmfitness.service.VoteThemeService;
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
@RequestMapping(value = "v1/mini/vote")
public class VoteController {

    @Autowired
    private VoteThemeService voteThemeService;
    @Autowired
    private VoteOptionsService voteOptionsService;
    @Autowired
    private VoteRecordService voteRecordService;

    @RequestMapping(value = "page")
    public String getPages(HttpServletRequest request, PageQuery<VoteTheme> pageQuery) {
        JSONObject object = new JSONObject();
        VoteTheme theme = new VoteTheme();
        theme.setStatus(1);
        PageQuery<VoteTheme> page = voteThemeService.findPage(pageQuery, theme);
        object.put("code", 200);
        object.put("msg", "success");
        object.put("page", page);
        return object.toJSONString();
    }

    @RequestMapping(value = "info")
    public String getVoteInfo(HttpServletRequest request, PageQuery<VoteTheme> pageQuery) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("voteId");
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
        VoteTheme theme = voteThemeService.get(new VoteTheme(Long.valueOf(id)));
        if (theme == null) {
            object.put("code", 500);
            object.put("msg", "投票不存在");
            return object.toJSONString();
        }
        Long vote = Long.valueOf(id);
        List<VoteOptions> list = voteOptionsService.findList(new VoteOptions(vote));
        if (list.size() != 0) {
            object.put("showType", list.get(0).getShowType());
        } else {
            object.put("showType", 0);
        }
        JSONArray array = new JSONArray();
        for (VoteOptions options : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", options.getId());
            jsonObject.put("voteId", options.getVoteId());
            jsonObject.put("type", options.getShowType());
            jsonObject.put("image", options.getImage());
            jsonObject.put("content", options.getContent());
            array.add(jsonObject);
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("info", theme);
        object.put("list", array);
        return object.toJSONString();
    }

    @RequestMapping(value = "voting")
    public String voting(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String voteId = request.getParameter("voteId");
        String power = request.getParameter("power");
        String powerId = request.getParameter("powerId");
        String optionId = request.getParameter("optionId");
        if (StringUtils.isBlank(voteId) || StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(power) || !StringUtils.isNumeric(powerId)
                || !StringUtils.isNumeric(voteId) || StringUtils.isNumeric(optionId)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        VoteRecord record = new VoteRecord();
        record.setOpenId(id);
        record.setThemeId(Long.valueOf(voteId));
        VoteRecord voteRecord = voteRecordService.get(record);
        if (voteRecord != null) {
            object.put("code", 500);
            object.put("msg", "您已投票");
            return object.toJSONString();
        } else {
            voteRecord.setThemeId(Long.valueOf(voteId));
            voteRecord.setOpenId(id);
            voteRecord.setOptionId(Long.valueOf(optionId));
            voteRecordService.save(voteRecord);
        }
        return object.toJSONString();
    }
}
