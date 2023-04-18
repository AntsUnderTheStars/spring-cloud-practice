package cn.hao.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProviderZkPaymentApplication8004 {

    public static void main(String[] args) {
        SpringApplication.run(ProviderZkPaymentApplication8004.class, args);
    }
}
