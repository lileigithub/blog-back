package com.ll.blog.scheduled;

import com.ll.blog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * description
 * @author lilei
 * @date 2022/8/22 17:54
 */
@Component
public class BlogTasks {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    BlogService blogService;

    @Scheduled(cron = "0/60 * * * * ? ")
    public void saveBlogViews(){
        Set keys = redisTemplate.keys("blogViews_*");
        if(CollectionUtils.isEmpty(keys)) return;
        for (Object object : keys) {
            String key = (String)object;
            String id = key.split("_")[1];
            Integer views = (Integer)redisTemplate.opsForValue().get(key);
            blogService.saveViewsById(Long.parseLong(id), views);
        }
    }
}
