package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.bytom.HexUtil;
import io.bytom.api.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhangxuewen
 * @date 2018.09.25
 */
@RestController
@RequestMapping(value = "base")
public class BaseController {

    @Autowired
    private BtmApiService btmApiService;

    @RequestMapping(value = "systemTime")
    public String systemTime() {
        JSONObject object = new JSONObject();
        object.put("code", 200);
        object.put("msg", new Date().toString());
        return object.toJSONString();
    }

    @RequestMapping(value = "transaction")
    public String getTransaction(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String txId = request.getParameter("txId");
        if (StringUtils.isBlank(txId)) {
            object.put("code", 500);
            object.put("msg", "请输入交易哈希");
            return object.toJSONString();
        }
        Transaction transaction = btmApiService.getTransaction(txId);
        if (transaction == null) {
            object.put("code", 500);
            object.put("msg", "交易不存在");
            return object.toJSONString();
        }
        int current = btmApiService.getBlockHeight();
        JSONObject data = new JSONObject();
        data.put("id", transaction.txId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.put("time", sdf.format(Long.valueOf(transaction.blockTime) * 1000L));
        data.put("blockId", transaction.blockHash);
        data.put("height", transaction.blockHeight + " ( " + (current - transaction.blockHeight) + " confirmations )");
        data.put("index", transaction.blockIndex);
        if (transaction.statusFail) {
            data.put("status", "失败");
        } else if (transaction.blockHeight == 0) {
            data.put("status", "未确认");
        } else {
            data.put("status", "成功");
        }
        data.put("transactionCount", transaction.blockTransactionsCount);
        List<Transaction.Input> inputs = transaction.inputs;
        JSONArray array = new JSONArray();
        Long inputBtm = 0L;
        Long outputBtm = 0L;
        for (Transaction.Input input : inputs) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", input.type);
            if (!StringUtils.isBlank(input.assetAlias)) {
                jsonObject.put("assetAlias", input.assetAlias);
            }
            if (!StringUtils.isBlank(input.accountAlias)) {
                jsonObject.put("accountAlias", input.accountAlias);
            }
            String amount = new BigDecimal(input.amount).divide(new BigDecimal(100000000L))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN).toString();
            jsonObject.put("amount", amount + " " + input.assetAlias);
            jsonObject.put("address", input.address);
            array.add(jsonObject);
            if ("BTM".equals(input.assetAlias)) {
                inputBtm += input.amount;
            }
        }
        JSONArray jsonArray = new JSONArray();
        JSONArray memoArray = new JSONArray();
        List<Transaction.Output> outputs = transaction.outputs;
        for (Transaction.Output output : outputs) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", output.type);
            String amount = new BigDecimal(output.amount).divide(new BigDecimal(100000000L))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN).toString();
            jsonObject.put("assetAlias", output.assetAlias);
            jsonObject.put("accountAlias", output.accountAlias);
            jsonObject.put("address", output.address);
            jsonObject.put("amount", amount + " " + output.assetAlias);
            jsonObject.put("controlProgram", output.controlProgram);
            jsonObject.put("id", output.id);
            jsonArray.add(jsonObject);
            if ("BTM".equals(output.assetAlias)) {
                outputBtm += output.amount;
            }
            if (("retire").equals(output.type)) {
                JSONObject memoObject = new JSONObject();
                memoObject.put("memo", HexUtil.decodingRetireProgram(output.controlProgram));
                String id = output.id.substring(0, 6) + "..." + output.id.substring(output.id.length() - 6, output.id.length());
                memoObject.put("outputId", id);
                memoArray.add(memoObject);
            }
        }
        Long gas = inputBtm - outputBtm;
        data.put("gas", new BigDecimal(gas).divide(new BigDecimal(100000000L))
                .setScale(8, BigDecimal.ROUND_HALF_DOWN).toString() + " BTM");
        data.put("inputs", array);
        data.put("outputs", jsonArray);
        object.put("code", 200);
        object.put("msg", "success");
        object.put("data", data);
        if (memoArray.size() != 0) {
            object.put("hasMemo", 1);
        } else {
            object.put("hasMemo", 0);
        }
        object.put("memoList", memoArray);
        return object.toJSONString();
    }

}
