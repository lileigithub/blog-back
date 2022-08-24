package com.ll.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class BlogApplicationTests {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        HyperLogLogOperations operations = stringRedisTemplate.opsForHyperLogLog();

        // add 方法对应 PFADD 命令
        operations.add("ip_20190301", "192.168.0.1", "192.168.0.2", "192.168.0.3");
        // size 方法对应 PFCOUNT 命令
        System.out.println(operations.size("ip_20190301"));     // 3
    }

}
