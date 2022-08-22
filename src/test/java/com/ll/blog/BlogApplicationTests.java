package com.ll.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class BlogApplicationTests {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        //redisTemplate.opsForValue().set("test", 111, 30, TimeUnit.MINUTES);
        System.out.println(redisTemplate.keys("blog*"));
    }

}
