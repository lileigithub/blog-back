package com.ll.blog.scheduled;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.blog.dto.BlogCacheDto;
import com.ll.blog.service.BlogService;
import com.ll.blog.util.Constants;
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

    /**
     * 现有需定时更新字段：阅读量，点赞量，之后可能会有收藏量，分享量。
     * @throws Exception
     */
    @Scheduled(cron = "0/30 * * * * ? ")
    public void saveBlogCache() throws Exception{
        Set keys = redisTemplate.keys(Constants.BLOG_CACHE_KEY + "*");
        if(CollectionUtils.isEmpty(keys)) return;
        for (Object object : keys) {
            String key = (String)object;
            String id = key.split("_")[1];
            Object jsonString = redisTemplate.opsForValue().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            BlogCacheDto blogViewDto = objectMapper.convertValue(jsonString, new TypeReference<BlogCacheDto>(){});
            Integer nowView = blogViewDto.getViews();
            if(!blogViewDto.getChanged()) continue;
            blogService.saveViewsById(Long.parseLong(id), nowView);
            blogViewDto.setChanged(false);
            redisTemplate.opsForValue().set(Constants.BLOG_CACHE_KEY + id, blogViewDto);
        }
    }
}
