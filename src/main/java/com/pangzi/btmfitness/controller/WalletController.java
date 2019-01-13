package com.pangzi.btmfitness.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.bytom.BtmApiService;
import com.pangzi.btmfitness.entity.BtmAccount;
import com.pangzi.btmfitness.service.BtmAccountService;
import io.bytom.api.Account;
import io.bytom.api.Key;
import io.bytom.api.Receiver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangxuewen
 * @date 2018.10.20
 */
@RequestMapping(value = "v1/mini/wallet")
@RestController
public class WalletController {

    private final String BTM = "BTM";
    private final Long NEU = 100000000L;
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private BtmApiService btmApiService;
    @Autowired
    private BtmAccountService accountService;

    @RequestMapping(value = "check", method = RequestMethod.POST)
    public String checkWallet(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失.");
            return object.toJSONString();
        }
        BtmAccount account = accountService.get(new BtmAccount(id));
        object.put("code", 200);
        object.put("msg", "success.");
        if (account != null) {
            object.put("created", true);
        } else {
            object.put("created", false);
        }
        return object.toJSONString();
    }

    @RequestMapping(value = "balance", method = RequestMethod.POST)
    public String getBalance(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失.");
            return object.toJSONString();
        }
        BtmAccount account = accountService.get(new BtmAccount(id));
        if (account == null) {
            object.put("code", 500);
            object.put("msg", "钱包不存在.");
            return object.toJSONString();
        }
        JSONObject jsonObject = btmApiService.getBalanceByAccount(account.getAccountAlias());
        object.put("code", 200);
        object.put("msg", "success.");
        object.put("BTM", new BigDecimal(jsonObject.getString("BTM"))
                .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        object.put("FITNESS", new BigDecimal(jsonObject.getString("FITNESS"))
                .setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        return object.toJSONString();
    }

    @RequestMapping(value = "info")
    public String getWalletInfo(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            object.put("code", 500);
            object.put("msg", "参数缺失.");
            return object.toJSONString();
        }
        BtmAccount account = accountService.get(new BtmAccount(id));
        if (account == null) {
            object.put("code", 500);
            object.put("msg", "钱包不存在.");
            return object.toJSONString();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("alias", account.getAccountAlias());
        object.put("address", account.getReceiverAddress());
        object.put("controlProgram", account.getControlProgram());
        object.put("accountId", account.getAccountId());
        String json = btmApiService.getBalanceByAliasAndSymbol(account.getAccountAlias(), null);
        JSONArray array = JSON.parseArray(json);
        object.put("assetList", array);
        String asset = btmApiService.getBalanceByAliasAndSymbol(account.getAccountAlias(), "BTM");
        JSONObject result = JSON.parseObject(asset);
        object.put("asset", new BigDecimal(result.getString("BTM")).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
        return object.toJSONString();
    }

    @RequestMapping(value = "create")
    public String createWallet(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String alias = request.getParameter("alias");
        String password = request.getParameter("password");
        String id = request.getParameter("id");
        if (StringUtils.isBlank(alias) || StringUtils.isBlank(password)) {
            object.put("code", 500);
            object.put("msg", "参数缺失.");
            return object.toJSONString();
        }
        BtmAccount btmAccount = new BtmAccount();
        btmAccount.setOpenId(id);
        BtmAccount result = accountService.get(btmAccount);
        if (result != null) {
            object.put("code", 500);
            object.put("msg", "您已创建过钱包");
            return object.toJSONString();
        }
        BtmAccount isExist = accountService.getByAccountAlias(alias);
        if (isExist != null) {
            object.put("code", 500);
            object.put("msg", "钱包名称已被使用");
            return object.toJSONString();
        }
        Key key = btmApiService.createKey(id, password);
        if (key == null) {
            object.put("code", 500);
            object.put("msg", "创建密钥请求区块错误");
            return object.toJSONString();
        }
        List<String> list = new LinkedList<>();
        list.add(key.xpub);
        Account account = btmApiService.createAccount(alias, list);
        if (account == null) {
            object.put("code", 500);
            object.put("msg", "创建帐号请求区块错误");
            return object.toJSONString();
        }
        Receiver receiver = btmApiService.createAddress(account.alias);
        if (receiver == null) {
            object.put("code", 500);
            object.put("msg", "创建地址请求区块错误");
            return object.toJSONString();
        }
        btmAccount.setAccountAlias(account.alias);
        btmAccount.setControlProgram(receiver.controlProgram);
        btmAccount.setKeyAlias(id);
        btmAccount.setReceiverAddress(receiver.address);
        btmAccount.setKeyPublic(key.xpub);
        btmAccount.setKeySecret(password);
        btmAccount.setAccountId(account.id);
        accountService.save(btmAccount);
        object.put("code", 200);
        object.put("msg", "success");
        object.put("alias", btmAccount.getAccountAlias());
        return object.toJSONString();
    }

    @RequestMapping(value = "estimateGas")
    public String estimateTransactionFee(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String symbol = request.getParameter("symbol");
        String address = request.getParameter("address");
        String amount = request.getParameter("amount");
        BtmAccount account = accountService.get(new BtmAccount(id));
        if (account == null) {
            object.put("code", 500);
            object.put("msg", "获取账户失败");
            return object.toJSONString();
        }
        String reg = "\\d+(\\.\\d+)?";
        if (!amount.matches(reg)) {
            object.put("code", 500);
            object.put("msg", "请输入正确的数量");
            return object.toJSONString();
        }
        Long amounts = new BigDecimal(amount).multiply(new BigDecimal(NEU)).longValue();
        if (amounts <= 0) {
            object.put("code", 500);
            object.put("msg", "转账数量必须大于0");
            return object.toJSONString();
        }
        if (!btmApiService.validateAddress(address)) {
            object.put("code", 500);
            object.put("msg", "地址格式错误");
            return object.toJSONString();
        }
        int fee = btmApiService.estimateTransactionGas(account.getAccountAlias(), symbol, address, amounts);
        if (fee == -1) {
            object.put("code", 400);
            object.put("msg", "预估手续费失败,系统会默认 0.01BTM");
            return object.toJSONString();
        }
        String gas = new BigDecimal(fee).divide(new BigDecimal(NEU)).setScale(8).toString();
        object.put("code", 200);
        object.put("msg", "success");
        object.put("fee", gas);
        return object.toJSONString();
    }

    @RequestMapping(value = "transfer")
    public String transfer(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String symbol = request.getParameter("symbol");
        String address = request.getParameter("address");
        String fee = request.getParameter("fee");
        String amount = request.getParameter("amount");
        String password = request.getParameter("password");
        if (StringUtils.isBlank(symbol) || StringUtils.isBlank(address)
                || StringUtils.isBlank(fee) || StringUtils.isBlank(amount)) {
            object.put("code", 500);
            object.put("msg", "参数缺少");
            return object.toJSONString();
        }
        String reg = "\\d+(\\.\\d+)?";
        if (!amount.matches(reg)) {
            object.put("code", 500);
            object.put("msg", "请输入正确的数量");
            return object.toJSONString();
        }
        Long amounts = new BigDecimal(amount).multiply(new BigDecimal(NEU)).longValue();
        if (amounts <= 0) {
            object.put("code", 500);
            object.put("msg", "转账数量必须大于0");
            return object.toJSONString();
        }
        if (!amount.matches(reg)) {
            fee = "0.01";
        }
        Long fees = new BigDecimal(fee).multiply(new BigDecimal(NEU)).longValue();
        if (!btmApiService.validateAddress(address)) {
            object.put("code", 500);
            object.put("msg", "地址格式错误");
            return object.toJSONString();
        }
        BtmAccount account = accountService.get(new BtmAccount(id));
        if (account == null) {
            object.put("code", 500);
            object.put("msg", "请先创建钱包");
            return object.toJSONString();
        }
        String result = btmApiService.getBalanceByAliasAndSymbol(account.getAccountAlias(), symbol);
        JSONObject jsonObject = JSON.parseObject(result);
        if (new BigDecimal(jsonObject.getString(symbol))
                .compareTo(new BigDecimal(amount)) < 0) {
            object.put("code", 500);
            object.put("msg", "资产不足");
            return object.toJSONString();
        }
        result = btmApiService.getBalanceByAliasAndSymbol(account.getAccountAlias(), BTM);
        jsonObject = JSON.parseObject(result);
        if (new BigDecimal(jsonObject.getString(BTM)).compareTo(new BigDecimal(fee)) < 0) {
            object.put("code", 500);
            object.put("msg", "BTM资产少于手续费");
            return object.toJSONString();
        }
        if (!btmApiService.validatePassword(account.getKeyPublic(), password)) {
            object.put("code", 500);
            object.put("msg", "密码错误");
            return object.toJSONString();
        }

        String txId = btmApiService.makeTransaction(account.getAccountAlias(), symbol, address, amounts, fees, password);
        if (txId == null) {
            object.put("code", 500);
            object.put("msg", "Available UTXOs of account have been reserved");
            return object.toJSONString();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("txId", txId);
        return object.toJSONString();
    }

    @RequestMapping(value = "getTransactionList")
    public String transactionList(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String id = request.getParameter("id");
        String from = request.getParameter("from");
        String count = request.getParameter("count");
        if (StringUtils.isBlank(id) || StringUtils.isBlank(from) || StringUtils.isBlank(count)) {
            object.put("code", 500);
            object.put("msg", "参数缺失");
            return object.toJSONString();
        }
        if (!StringUtils.isNumeric(from) || !StringUtils.isNumeric(count)) {
            object.put("code", 500);
            object.put("msg", "参数格式错误");
            return object.toJSONString();
        }
        BtmAccount account = accountService.get(new BtmAccount(id));
        if (account == null) {
            object.put("code", 500);
            object.put("msg", "您没有账户");
            return object.toJSONString();
        }
        JSONArray array = btmApiService.getTransactionListByAccountId(account.getAccountId(),
                Integer.valueOf(from), Integer.valueOf(count));
        if (array.size() == 0 && Integer.parseInt(from) == 0) {
            object.put("code", 300);
            object.put("msg", "data is null");
            return object.toJSONString();
        } else if (array.size() == 0 && Integer.parseInt(from) != 0) {
            object.put("code", 400);
            object.put("msg", "no more data");
            return object.toJSONString();
        }
        object.put("code", 200);
        object.put("msg", "success");
        object.put("transactionList", parseTransactionList(array));
        return object.toJSONString();
    }

    private JSONArray parseTransactionList(JSONArray list) {
        JSONArray array = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = new JSONObject();
            JSONObject transaction = list.getJSONObject(i);
            String id = transaction.getString("tx_id");
            object.put("tx_id", id);
            id = id.substring(0, 18) + "...";
            object.put("txId", id);
            if (transaction.getInteger("block_height") == 0) {
                object.put("status", "未确认");
            } else if (transaction.getBoolean("status_fail")) {
                object.put("status", "失败");
            } else {
                object.put("status", "成功");
            }

            Long time = transaction.getLong("block_time") * 1000L;
            object.put("time", sdf.format(time));
            JSONArray inputs = transaction.getJSONArray("inputs");
            JSONArray outputs = transaction.getJSONArray("outputs");
            for (int input = 0; input < inputs.size(); input++) {
                JSONObject objectInput = inputs.getJSONObject(input);
                if (("spend").equals(objectInput.getString("type"))) {
                    if (StringUtils.isBlank(object.getString("from"))) {
                        if (objectInput.containsKey("account_alias")) {
                            object.put("from", objectInput.getString("account_alias"));
                        } else {
                            String address = objectInput.getString("address");
                            object.put("from", address.substring(0, 8) + "...");
                        }
                    }
                }
            }
            StringBuilder outputAlias = new StringBuilder();
            for (int output = 0; output < outputs.size(); output++) {
                JSONObject objectOutput = outputs.getJSONObject(output);
                if (("control").equals(objectOutput.getString("type"))) {
                    if (objectOutput.containsKey("account_alias")) {
                        if (!object.getString("from").equals(objectOutput.getString("account_alias"))) {
                            outputAlias = outputAlias.append(objectOutput.getString("account_alias")).append(" / ");
                        }
                    }
                }
            }
            if (StringUtils.isBlank(outputAlias)) {
                outputAlias = outputAlias.append(object.getString("from")).append(" / ");
            }
            String str = outputAlias.toString().substring(0, outputAlias.length() - 3);
            object.put("to", str);
            array.add(object);
        }
        return array;
    }
}
