package com.pangzi.btmfitness.bytom;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangzi.btmfitness.utils.HttpClientUtils;
import io.bytom.api.*;
import io.bytom.exception.BytomException;
import io.bytom.http.Client;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.bytom.api.Transaction.estimateGas;

/**
 * @author zhangxuewen
 * @date 2018.09.26
 */
@Service
public class BtmApiService {

    @Value("${btm.node.token}")
    private String token;
    @Value("${btm.node.url}")
    private String nodeUrl;
    @Value("${btm.spend.account}")
    private String spendAccount;
    @Value("${btm.main.fitness}")
    private String spendAccountPassword;

    private Client client;

    Logger logger = LoggerFactory.getLogger(getClass());

    private final String SUCCESS = "success";
    private final String BTM = "BTM";
    private final String FITNESS = "FITNESS";
    private final String CITOKEN = "CITOKEN";
    private final String ADTOKEN = "ADTOKEN";
    private final String CODE = "code";
    private final String FAIL = "fail";
    private final String contract = "contract keep(target: Integer) locks value { clause reveal(current: Integer) { verify current >= target unlock value }}";
    private final String COMPILE = "compile";
    private final String BALANCES = "list-balances";
    private final String CHECKPASSWORD = "check-key-password";
    private final String VALIDATEADDRESS = "validate-address";
    private final String TRANSACTIONLIST = "list-transactions";
    private final String UNCONFIRMEDLIST = "list-unconfirmed-transactions";
    private final String CHARITYCENTER = "charitycenter";
    private final String adContract = "contract keep(target: Integer,hash: Hash) locks value { clause reveal(current: Integer,string: String) { verify current >= target verify sha256(string) == hash unlock value }}";
    private final String ADCENTER = "adcenter";
    private final String DISTRIBUTEDCENTER = "distributedcenter";
    private final String DISTRIBUTEDPROGRAM = "0014f62a6b358a0ea34053604306f6ad9b9ad88ef7c4";

    /**
     * 获取块高
     *
     * @return
     */
    public int getBlockHeight() {
        try {
            client = ClientUtils.generateClient();
            int blockCount = Block.getBlockCount(client);
            return blockCount;
        } catch (BytomException e) {
            logger.error("[BtmApiService-getBlockHeight] get current block height error:{}", e.getError());
            return 0;
        }
    }

    /**
     * 获取 BTM FITNESS 的资产数量
     * <p>
     * 速度比getBalanceByAlias快
     *
     * @param name
     * @return
     */
    public JSONObject getBalanceByAccount(String name) {
        JSONObject object = new JSONObject();
        object.put("BTM", 0);
        object.put("FITNESS", 0);
        try {
            client = ClientUtils.generateClient();
            Balance.Items items = new Balance.QueryBuilder().list(client);
            if (SUCCESS.equals(items.status)) {
                for (int i = 0; i < items.data.size(); i++) {
                    Balance balance = items.data.get(i);
                    BigDecimal amount = new BigDecimal(balance.amount);
                    if (name.equals(balance.accountAlias) && BTM.equals(balance.assetAlias)) {
                        object.put("BTM", amount.divide(new BigDecimal(100000000)).setScale(8)
                                .stripTrailingZeros().toPlainString());
                    } else if (name.equals(balance.accountAlias) && FITNESS.equals(balance.assetAlias)) {
                        object.put("FITNESS", amount.divide(new BigDecimal(100000000)).setScale(8)
                                .stripTrailingZeros().toPlainString());
                    }
                }
            }
            return object;
        } catch (BytomException e) {
            logger.error("[BtmApiService-getAssetByAccount] get asset error:{}", e.getError());
        }
        return null;
    }

    /**
     * 利用list-balances的筛选条件获取指定用户资产
     *
     * @param name
     * @return
     */
    public String getBalanceByAliasAndSymbol(String name, String symbol) {
        JSONObject object = new JSONObject();
        object.put(symbol, 0);
        String url = nodeUrl + BALANCES;
        JSONObject data = new JSONObject();
        data.put("account_alias", name);
        try {
            String body = HttpClientUtils.post(url, data.toJSONString(), getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            JSONObject jsonObject = JSON.parseObject(body);
            if (!checkRpcResult(body, BALANCES)) {
                return null;
            }
            logger.info("[BtmApiService-getBalanceByAliasAndSymbol] get balances return body: {}", body);
            JSONArray array = jsonObject.getJSONArray("data");
            JSONArray jsonArray = new JSONArray();
            for (Object o : array) {
                JSONObject object1 = JSON.parseObject(o.toString());
                if (StringUtils.isBlank(symbol)) {
                    object = new JSONObject();
                    BigDecimal fitness = object1.getBigDecimal("amount");
                    object.put("symbol", object1.getString("asset_alias"));
                    object.put("amount", fitness.divide(new BigDecimal(100000000)).setScale(8)
                            .stripTrailingZeros().toPlainString());
                    if ("BTM".equals(object1.getString("asset_alias"))) {
                        jsonArray.add(0, object);
                    } else {
                        jsonArray.add(object);
                    }
                } else if (symbol.equals(object1.getString("asset_alias"))) {
                    BigDecimal fitness = object1.getBigDecimal("amount");
                    object.put(symbol, fitness.divide(new BigDecimal(100000000)).setScale(8)
                            .stripTrailingZeros().toPlainString());
                }
            }
            if (StringUtils.isBlank(symbol)) {
                return jsonArray.toJSONString();
            }
            return object.toJSONString();
        } catch (Exception e) {
            logger.error("[BtmApiService-getBalanceByAliasAndSymbol] get balances error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据哈希查交易
     *
     * @param tx
     * @return
     */
    public Transaction getTransaction(String tx) {
        try {
            client = ClientUtils.generateClient();
            Transaction transaction = new Transaction.QueryBuilder().setTxId(tx).get(client);
            return transaction;
        } catch (BytomException e) {
            logger.error("[BtmApiService-getTransaction] get transaction error:{}", e.getError());
        }
        return null;
    }

    /**
     * 获取当前未确认的交易
     *
     * @return
     */
    public JSONObject getUnconfirmedTransactions() {
        JSONObject object = new JSONObject();
        String url = nodeUrl + UNCONFIRMEDLIST;
        ;
        try {
            String body = HttpClientUtils.post(url, null, getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            logger.info("[BtmApiService-getTransactionListByAccountId] get transaction-list return body: {}", body);
            if (!checkRpcResult(body, BALANCES)) {
                return null;
            }
            return JSON.parseObject(body).getJSONObject("data");
        } catch (Exception e) {
            logger.error("[BtmApiService-getUnconfirmedTransactions] get unconfirmed-transactions error:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 查询指定用户的交易列表，from为起始条数，count为查询总数
     *
     * @param accountId
     * @param from
     * @param count
     * @return
     */
    public JSONArray getTransactionListByAccountId(String accountId, Integer from, Integer count) {
        String url = nodeUrl + TRANSACTIONLIST;
        JSONObject data = new JSONObject();
        data.put("unconfirmed", true);
        data.put("detail", true);
        if (!StringUtils.isBlank(accountId)) {
            data.put("account_id", accountId);
        }
        if (count != 0) {
            data.put("count", count);
        }
        if (from != -1) {
            data.put("from", from);
        }
        try {
            String body = HttpClientUtils.post(url, data.toJSONString(), getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            if (!checkRpcResult(body, BALANCES)) {
                return null;
            }
            logger.info("[BtmApiService-getTransactionListByAccountId] get transaction-list return body: {}", body);
            JSONObject object = JSON.parseObject(body);
            JSONArray array = new JSONArray();
            array = object.getJSONArray("data");
            return array;
        } catch (Exception e) {
            logger.error("[BtmApiService-getTransactionListByAccountId] get transaction-list error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 预估手续费
     *
     * @param account
     * @param asset
     * @param address
     * @param amount
     * @return
     */
    public int estimateTransactionGas(String account, String asset, String address, Long amount) {
        try {
            client = ClientUtils.generateClient();
            Transaction.Template build = new Transaction.Builder().addAction(
                    new Transaction.Action.SpendFromAccount().setAssetAlias(asset).setAccountAlias(account).setAmount(amount)
            ).addAction(
                    new Transaction.Action.ControlWithAddress().setAmount(amount).setAssetAlias(asset).setAddress(address)
            ).build(client);
            Transaction.TransactionGas gas = estimateGas(client, build);
            return gas.totalNeu;
        } catch (BytomException e) {
            logger.error("[BtmApiService-estimateTransactionGas] estimate transaction gas error:{}", e.getError());
        }
        return -1;
    }


    /**
     * 普通的转账交易
     *
     * @param account
     * @param asset
     * @param address
     * @param amount
     * @param password
     * @return
     */
    public String makeTransaction(String account, String asset, String address, Long amount, Long gas, String password) {
        try {
            client = ClientUtils.generateClient();
            Transaction.Template controlAddress = new Transaction.Builder()
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAccountAlias(account)
                                    .setAssetAlias("BTM")
                                    .setAmount(gas)
                    )
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAccountAlias(account)
                                    .setAssetAlias(asset)
                                    .setAmount(amount)
                    )
                    .addAction(
                            new Transaction.Action.ControlWithAddress()
                                    .setAddress(address)
                                    .setAssetAlias(asset)
                                    .setAmount(amount)
                    ).build(client);
            Transaction.Template singer = new Transaction.SignerBuilder().sign(client,
                    controlAddress, password);
            Transaction.SubmitResponse txs = Transaction.submit(client, singer);
            return txs.tx_id;
        } catch (BytomException e) {
            logger.error("[BtmApiService-makeTransaction] make transaction error:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 编译链上打卡合约
     *
     * @param target
     * @return
     */
    public String compileSmartContract(Integer target) {
        String url = nodeUrl + COMPILE;
        JSONObject data = new JSONObject();
        data.put("contract", contract);
        JSONArray args = new JSONArray();
        JSONObject param = new JSONObject();
        param.put("integer", target);
        args.add(param);
        data.put("args", args);
        try {
            String body = HttpClientUtils.post(url, data.toJSONString(), getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            JSONObject object = JSON.parseObject(body);
            if (!checkRpcResult(body, COMPILE)) {
                return null;
            }
            logger.info("[BtmApiService-compileSmartContract] compile return body: {}", body);
            return object.getJSONObject("data").getString("program");
        } catch (Exception e) {
            logger.error("[BtmApiService-compileSmartContract] compile error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 发布链上打卡合约
     *
     * @param amount
     * @param program
     * @return
     */
    public String pushSmartContract(Long amount, String program) {
        try {
            client = ClientUtils.generateClient();
            Transaction.Template build = new Transaction.Builder()
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAmount(1500000)
                                    .setAccountAlias(spendAccount)
                                    .setAssetAlias("BTM")
                    )
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAssetAlias("FITNESS")
                                    .setAmount(amount)
                                    .setAccountAlias(spendAccount)
                    )
                    .addAction(
                            new Transaction.Action()
                                    .setParameter("type", "control_program")
                                    .setParameter("amount", amount)
                                    .setParameter("asset_alias", "FITNESS")
                                    .setParameter("control_program", program)
                    ).build(client);
            Transaction.Template singer = new Transaction.SignerBuilder().sign(client,
                    build, spendAccountPassword);
            Transaction.SubmitResponse txs = Transaction.submit(client, singer);
            return txs.tx_id;
        } catch (BytomException e) {
            logger.error("[BtmApiService-pushSmartContract] push contract transaction error:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 发布广告打卡合约
     *
     * @param amount
     * @param program
     * @return
     */
    public String pushAdContract(Long amount, String program) {
        try {
            client = ClientUtils.generateClient();
            Transaction.Template build = new Transaction.Builder()
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAmount(1300000)
                                    .setAccountAlias(ADCENTER)
                                    .setAssetAlias("BTM")
                    )
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAssetAlias(ADTOKEN)
                                    .setAmount(amount)
                                    .setAccountAlias(ADCENTER)
                    )
                    .addAction(
                            new Transaction.Action()
                                    .setParameter("type", "control_program")
                                    .setParameter("amount", amount)
                                    .setParameter("asset_alias", ADTOKEN)
                                    .setParameter("control_program", program)
                    ).build(client);
            Transaction.Template singer = new Transaction.SignerBuilder().sign(client,
                    build, spendAccountPassword);
            Transaction.SubmitResponse txs = Transaction.submit(client, singer);
            return txs.tx_id;
        } catch (BytomException e) {
            logger.error("[BtmApiService-pushAdContract] push contract transaction error:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 解锁合约
     *
     * @param amount
     * @param accountProgram
     * @param unspentId
     * @param value
     * @return
     */
    public String unLockSmartContract(Long amount, String accountProgram, String unspentId, String value) {
        JSONArray arguments = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("type", "data");
        JSONObject raw = new JSONObject();
        raw.put("value", value);
        object.put("raw_data", raw);
        arguments.add(object);
        try {
            client = ClientUtils.generateClient();
            Transaction.Template build = new Transaction.Builder()
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAmount(1500000)
                                    .setAccountAlias(spendAccount)
                                    .setAssetAlias("BTM")
                    )
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setParameter("type", "spend_account_unspent_output")
                                    .setParameter("output_id", unspentId)
                                    .setParameter("arguments", arguments)
                    )
                    .addAction(
                            new Transaction.Action()
                                    .setParameter("type", "control_program")
                                    .setParameter("amount", amount)
                                    .setParameter("asset_alias", "FITNESS")
                                    .setParameter("control_program", accountProgram)
                    ).build(client);
            Transaction.Template singer = new Transaction.SignerBuilder().sign(client,
                    build, spendAccountPassword);
            Transaction.SubmitResponse txs = Transaction.submit(client, singer);
            return txs.tx_id;
        } catch (BytomException e) {
            logger.error("[BtmApiService-unLockSmartContract] unlock contract transaction error:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取发布的合约id
     *
     * @param txId
     * @param program
     * @return
     */
    public String getContractId(String txId, String program) {
        try {
            client = ClientUtils.generateClient();
            Transaction transaction = new Transaction.QueryBuilder().setTxId(txId).get(client);
            List<Transaction.Output> list = transaction.outputs;
            for (Transaction.Output output : list) {
                if (output.controlProgram.equals(program)) {
                    return output.id;
                }
            }
        } catch (BytomException e) {
            logger.error("[BtmApiService-getContractId] get contract id error:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 创建账户
     *
     * @param alias
     * @param xpubs
     * @return
     */
    public Account createAccount(String alias, List<String> xpubs) {
        try {
            client = ClientUtils.generateClient();
            Account account = new Account.Builder().setAlias(alias).setQuorum(1).setXpubs(xpubs).create(client);
            return account;
        } catch (BytomException e) {
            logger.error("[BtmApiService-createAccount] alias:{} create account error:{}", alias, e.getMessage());
        }
        return null;
    }

    /**
     * 创建密钥对
     *
     * @param alias
     * @param password
     * @return
     */
    public Key createKey(String alias, String password) {
        try {
            client = ClientUtils.generateClient();
            Key key = Key.create(client, alias, password);
            return key;
        } catch (BytomException e) {
            logger.error("[BtmApiService-createKey] alias:{} create key error:{}", alias, e.getMessage());
        }
        return null;
    }

    /**
     * 创建地址
     *
     * @param alias
     * @return
     */
    public Receiver createAddress(String alias) {
        try {
            client = ClientUtils.generateClient();
            Receiver receiver = new Account.ReceiverBuilder().setAccountAlias(alias).create(client);
            return receiver;
        } catch (BytomException e) {
            logger.error("[BtmApiService-createAddress] alias:{} create address error:{}", alias, e.getMessage());
        }
        return null;
    }

    /**
     * 校验地址是否正确
     *
     * @param address
     * @return
     */
    public Boolean validateAddress(String address) {
//        try {
//            client = ClientUtils.generateClient();
//            Address validate = new Account.AddressBuilder().validate(client, address);
//            return validate.vaild;
//        } catch (BytomException e) {
//            logger.error("[BtmApiService-validateAddress] validate address error:{}", e.getMessage());
//        }
//
//        return null;
        String url = nodeUrl + VALIDATEADDRESS;
        JSONObject data = new JSONObject();
        data.put("address", address);
        try {
            String body = HttpClientUtils.post(url, data.toJSONString(), getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            JSONObject jsonObject = JSON.parseObject(body);
            if (!checkRpcResult(body, BALANCES)) {
                return null;
            }
            logger.info("[BtmApiService-validateAddress] check address return body: {}", body);
            return jsonObject.getJSONObject("data").getBoolean("valid");
        } catch (Exception e) {
            logger.error("[BtmApiService-validateAddress] check address error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 校验密钥密码是否正确
     *
     * @param pub
     * @param pwd
     * @return
     */
    public Boolean validatePassword(String pub, String pwd) {
        String url = nodeUrl + CHECKPASSWORD;
        JSONObject data = new JSONObject();
        data.put("xpub", pub);
        data.put("password", pwd);
        try {
            String body = HttpClientUtils.post(url, data.toJSONString(), getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            JSONObject jsonObject = JSON.parseObject(body);
            if (!checkRpcResult(body, BALANCES)) {
                return null;
            }
            logger.info("[BtmApiService-validatePassword] check password return body: {}", body);
            return jsonObject.getJSONObject("data").getBoolean("check_result");
        } catch (Exception e) {
            logger.error("[BtmApiService-validatePassword] check password error: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, String> getHeader() {
        String auth = token;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        Map<String, String> header = new LinkedHashMap<String, String>();
        header.put("Authorization", authHeader);
        return header;
    }

    private Boolean checkRpcResult(String body, String method) {
        if (StringUtils.isBlank(body)) {
            logger.error("{} error, rpc request error: {}", method, null);
            return false;
        }
        JSONObject result = JSON.parseObject(body);
        String error = result.getString("status");
        if (FAIL.equals(error)) {
            logger.error("{} error, rpc request error: {}", method, result.getString("msg"));
            return false;
        }
        return true;
    }

    /**
     * 爱心捐赠销毁以写入捐赠证明到区块上
     *
     * @param amount
     * @param memo
     * @return
     */
    public String charityRetire(Long amount, String memo) {
        try {
            client = ClientUtils.generateClient();
            Transaction.Template build = new Transaction.Builder()
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAmount(1300000)
                                    .setAccountAlias(spendAccount)
                                    .setAssetAlias("BTM")
                    )
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAssetAlias(CITOKEN)
                                    .setAmount(amount)
                                    .setAccountAlias(CHARITYCENTER)
                    )
                    .addAction(
                            new Transaction.Action()
                                    .setParameter("type", "retire")
                                    .setParameter("amount", amount)
                                    .setParameter("asset_alias", CITOKEN)
                                    .setParameter("arbitrary", memo)
                    ).build(client);
            Transaction.Template singer = new Transaction.SignerBuilder().sign(client,
                    build, spendAccountPassword);
            Transaction.SubmitResponse txs = Transaction.submit(client, singer);
            return txs.tx_id;
        } catch (BytomException e) {
            logger.error("[BtmApiService-pushSmartContract] push contract transaction error:{}", e.getMessage());
        }
        return null;
    }


    /**
     * 编译广告合约
     *
     * @param keyHash
     * @param target
     * @return
     */
    public String compileAdSmartContract(String keyHash, Integer target) {
        String url = nodeUrl + COMPILE;
        JSONObject data = new JSONObject();
        data.put("contract", adContract);
        JSONArray args = new JSONArray();
        JSONObject param = new JSONObject();
        param.put("integer", target);
        args.add(param);
        param = new JSONObject();
        param.put("string", keyHash);
        args.add(param);
        data.put("args", args);
        try {
            String body = HttpClientUtils.post(url, data.toJSONString(), getHeader(), "application/json",
                    null, null, null);
            body = body.replaceAll("\n", "");
            logger.info("[BtmApiService-compileAdSmartContract] compile return body: {}", body);
            JSONObject object = JSON.parseObject(body);
            if (!checkRpcResult(body, COMPILE)) {
                return null;
            }
            return object.getJSONObject("data").getString("program");
        } catch (Exception e) {
            logger.error("[BtmApiService-compileAdSmartContract] compile error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解锁广告合约
     *
     * @param amount
     * @param unspentId
     * @param value
     * @param keyHex
     * @return
     */
    public String unlockAdContract(Long amount, String unspentId, String value, String keyHex) {
        JSONArray arguments = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("type", "data");
        JSONObject raw = new JSONObject();
        raw.put("value", value);
        object.put("raw_data", raw);
        arguments.add(object);
        object = new JSONObject();
        object.put("type", "data");
        raw = new JSONObject();
        raw.put("value", keyHex);
        object.put("raw_data", raw);
        arguments.add(object);
        try {
            client = ClientUtils.generateClient();
            Transaction.Template build = new Transaction.Builder()
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setAmount(1500000)
                                    .setAccountAlias(DISTRIBUTEDCENTER)
                                    .setAssetAlias("BTM")
                    )
                    .addAction(
                            new Transaction.Action.SpendFromAccount()
                                    .setParameter("type", "spend_account_unspent_output")
                                    .setParameter("output_id", unspentId)
                                    .setParameter("arguments", arguments)
                    )
                    .addAction(
                            new Transaction.Action()
                                    .setParameter("type", "control_program")
                                    .setParameter("amount", amount)
                                    .setParameter("asset_alias", ADTOKEN)
                                    .setParameter("control_program", DISTRIBUTEDPROGRAM)
                    ).build(client);
            Transaction.Template singer = new Transaction.SignerBuilder().sign(client,
                    build, spendAccountPassword);
            Transaction.SubmitResponse txs = Transaction.submit(client, singer);
            return txs.tx_id;
        } catch (BytomException e) {
            logger.error("[BtmApiService-unlockAdContract] unlock contract transaction error:{}", e.getMessage());
        }
        return null;
    }
}
