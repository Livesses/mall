package com.color.mall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ColorMallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColorMallThirdPartyApplication.class, args);
    }

}
