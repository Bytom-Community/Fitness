package com.pangzi.btmfitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhangxuewen
 */
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableScheduling
public class BtmFitnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(BtmFitnessApplication.class, args);
    }
}
