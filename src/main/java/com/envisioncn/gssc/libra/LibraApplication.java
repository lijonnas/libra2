package com.envisioncn.gssc.libra;

import com.envisioncn.gssc.libra.core.LibraManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableAutoConfiguration
public class LibraApplication {
    @Autowired
    LibraManager libraManager;

    public static void main(String[] args) {
        SpringApplication.run(LibraApplication.class, args);
    }

}
