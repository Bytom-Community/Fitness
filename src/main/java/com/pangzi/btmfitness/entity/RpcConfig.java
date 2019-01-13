package com.pangzi.btmfitness.entity;

/**
 * @author by zxw on 2017/12/8.
 */
public class RpcConfig {

    private String rpcUsername;
    private String rpcPassword;
    private String rpcUrl;

    public RpcConfig(String rpcUsername, String rpcPassword, String rpcUrl) {
        this.rpcUsername = rpcUsername;
        this.rpcPassword = rpcPassword;
        this.rpcUrl = rpcUrl;
    }

    public RpcConfig() {

    }

    public String getRpcUsername() {
        return rpcUsername;
    }

    public void setRpcUsername(String rpcUsername) {
        this.rpcUsername = rpcUsername;
    }

    public String getRpcPassword() {
        return rpcPassword;
    }

    public void setRpcPassword(String rpcPassword) {
        this.rpcPassword = rpcPassword;
    }

    public String getRpcUrl() {
        return rpcUrl;
    }

    public void setRpcUrl(String rpcUrl) {
        this.rpcUrl = rpcUrl;
    }

    @Override
    public String toString() {
        return "RpcConfig{" +
                "rpcUsername='" + rpcUsername + '\'' +
                ", rpcUrl='" + rpcUrl + '\'' +
                '}';
    }
}
