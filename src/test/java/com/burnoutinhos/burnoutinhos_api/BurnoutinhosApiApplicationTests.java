package com.burnoutinhos.burnoutinhos_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BurnoutinhosApiApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("hello world");
    }
}
