package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.entity.ContractRecord;
import com.pangzi.btmfitness.entity.Target;
import com.pangzi.btmfitness.entity.UnlockPower;
import com.pangzi.btmfitness.service.ContractRecordService;
import com.pangzi.btmfitness.service.TargetService;
import com.pangzi.btmfitness.service.UnlockPowerService;
import com.pangzi.btmfitness.utils.TimerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import static com.pangzi.btmfitness.utils.TimerUtils.*;

/**
 * @author zhangxuewen
 * @date 2018.11.07
 */
@RequestMapping(value = "v1/mini/power")
@RestController
public class PowerController {
    @Autowired
    private UnlockPowerService unlockPowerService;
    @Autowired
    private TargetService targetService;
    @Autowired
    private ContractRecordService contractRecordService;

    private final static Long DayTimes = 1000L * 60L * 60L * 24L;

    @RequestMapping(value = "info")
    public String getPower(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        List<UnlockPower> list = unlockPowerService.findList(new UnlockPower(id));
        Long spent = 0L, unspent = 0L, powerTotal = 0L;
        for (UnlockPower power : list) {
            spent += power.getSpentPower();
            unspent += power.getUnspentPower();
            powerTotal += power.getPower();
        }
        List<UnlockPower> powerList = unlockPowerService.findAllList();
        Long allSpent = 0L, allUnspent = 0L, allTotal = 0L;
        for (UnlockPower unlockPower : powerList) {
            allSpent += unlockPower.getSpentPower();
            allUnspent += unlockPower.getUnspentPower();
            allTotal += unlockPower.getPower();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("spent", formatNumber(spent.toString()));
        object.put("unspent", formatNumber(unspent.toString()));
        object.put("powerTotal", formatNumber(powerTotal.toString()));
        object.put("allSpent", formatNumber(allSpent.toString()));
        object.put("allUnspent", formatNumber(allUnspent.toString()));
        object.put("allTotal", formatNumber(allTotal.toString()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        object.put("daysStyle", getCalendarArray(id, year, month));
        return object.toJSONString();
    }

    @RequestMapping(value = "updateCalendar")
    public String getCalendarCssData(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(year) || StringUtils.isBlank(month)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(year) || !StringUtils.isNumeric(month)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        JSONArray array = getCalendarArray(id, Integer.valueOf(year), Integer.valueOf(month));
        object.put("code", 200);
        object.put("msg", "success");
        object.put("array", array);
        return object.toJSONString();
    }

    @RequestMapping(value = "getTargetCurrentDay")
    public String getTransactionByDay(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(year)
                || StringUtils.isBlank(month) || StringUtils.isBlank(day)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(year) || !StringUtils.isNumeric(month)
                || !StringUtils.isNumeric(day)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month);
        int dayInt = Integer.parseInt(day);
        Target newTarget = new Target();
        newTarget.setOpenId(id);
        newTarget.setStartTime(getTimesZeroByDay(yearInt, monthInt, dayInt));
        newTarget.setEndTime(getTimesEndByDay(yearInt, monthInt, dayInt));
        Target target = targetService.get(newTarget);
        if (target == null) {
            object.put("code", 500);
            object.put("msg", "无打卡");
            return object.toJSONString();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("finished", target.getIsFinish());
        ContractRecord record = contractRecordService.get(new ContractRecord(target.getId()));
        object.put("targetHash", record.getTxId());
        UnlockPower power = unlockPowerService.get(new UnlockPower(target.getId()));
        if (power != null) {
            object.put("unlockHash", power.getUnlockTxId());
        }

        if (target.getFinishWay() == 1) {
            object.put("way", "主动完成打卡");
        } else {
            object.put("way", "系统结算");
        }
        return object.toJSONString();
    }

    @RequestMapping(value = "getTotalPower")
    public String getPowerTotal(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        List<UnlockPower> list = unlockPowerService.findList(new UnlockPower(id));
        Long totalPower = 0L;
        for (UnlockPower unlockPower : list) {
            totalPower += unlockPower.getUnspentPower();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("total", formatNumberComma(totalPower));
        return object.toJSONString();
    }

    @RequestMapping(value = "getUserPower")
    public String getUserPower(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        List<UnlockPower> list = unlockPowerService.findList(new UnlockPower(id));
        Long totalPower = 0L;
        JSONArray array = new JSONArray();
        for (UnlockPower unlockPower : list) {
            JSONObject jsonObject = new JSONObject();
            totalPower += unlockPower.getUnspentPower();
            if (unlockPower.getUnspentPower() > 0) {
                jsonObject.put("powerId", unlockPower.getId());
                jsonObject.put("date", TimerUtils.formatDateByTimes(unlockPower.getCreateDay()));
                jsonObject.put("power", unlockPower.getUnspentPower());
                jsonObject.put("days", (TimerUtils.getTimesDayZero() - unlockPower.getCreateDay()) / DayTimes);
                array.add(jsonObject);
            }
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("total", totalPower);
        object.put("powerList", array);
        return object.toJSONString();
    }

    @RequestMapping(value = "getUserTodayPower")
    public String getUserTodayPower(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        UnlockPower unlockPower = new UnlockPower(id);
        unlockPower.setCreateDay(TimerUtils.getTimesDayZero());
        UnlockPower power = unlockPowerService.getUserTodayPower(unlockPower);
        if (power == null) {
            object.put("todayUnspentPower", 0);
        } else {
            object.put("todayUnspentPower", formatNumberComma(power.getUnspentPower()));
        }
        object.put("code", 200);
        object.put("msg", "success");
        return object.toJSONString();
    }

    private JSONArray getCalendarArray(String id, int year, int month) {
        JSONArray array = new JSONArray();
        Target newTarget = new Target();
        newTarget.setOpenId(id);
        newTarget.setStartTime(getTimesFirstDayMonth(year, month));
        newTarget.setEndTime(getTimesLastDayMonth(year, month, getLastDayMonth(year, month)));
        List<Target> list = targetService.findListByMonth(newTarget);
        for (Target target : list) {
            JSONObject object = new JSONObject();
            object.put("month", "current");
            object.put("color", "white");
            if ("true".equals(target.getIsFinish())) {
                object.put("background", "#728eff");
            } else {
                object.put("background", "#B2C8BB");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(target.getCreateTime());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            object.put("day", day);
            array.add(object);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int todayMonth = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        JSONObject object = new JSONObject();
        object.put("month", "current");
        object.put("color", "white");
        object.put("background", "#58cc69");
        if (month != todayMonth) {
            object.put("day", getLastDayMonth(year, month));
        } else {
            object.put("day", day);
        }
        array.add(object);
        return array;

    }

    public static String formatNumber(String number) {
        BigDecimal thousand = new BigDecimal("1000");
        BigDecimal tenThousand = new BigDecimal("10000");
        BigDecimal oneHundredMillion = new BigDecimal("100000000");

        StringBuilder builder = new StringBuilder();
        if (new BigDecimal(number).compareTo(oneHundredMillion) >= 0) {
            String num = new BigDecimal(number).divide(oneHundredMillion)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
            builder = builder.append(num).append(" 亿");
            return builder.toString();
        } else if (new BigDecimal(number).compareTo(tenThousand) >= 0) {
            String num = new BigDecimal(number).divide(tenThousand)
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
            builder = builder.append(num).append(" 万");
            return builder.toString();
        }
        return number.toString();
    }

    public static String formatNumberComma(float data) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(data);
    }
}
