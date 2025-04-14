package com.hmdp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class HmDianPingApplicationTests {
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    void contextLoads(){
        redisTemplate.opsForValue().set("helloComment","world");
    }
}
