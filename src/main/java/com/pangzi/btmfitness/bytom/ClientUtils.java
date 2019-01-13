package com.pangzi.btmfitness.bytom;

import io.bytom.exception.BytomException;
import io.bytom.http.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author zxw
 * @date 2018-09-26
 */
@Service
public class ClientUtils {

    private static String nodeUrl;
    private static String token;

    @Value("${btm.node.url}")
    private String NodeUrl;

    @Value("${btm.node.token}")
    private String Token;


    @PostConstruct
    public void init() {
        nodeUrl = NodeUrl;
        token = Token;
    }

    public static Client generateClient() throws BytomException {
        return new Client(nodeUrl, token);
    }
}
