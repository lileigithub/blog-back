package com.ll.blog.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.blog.dto.BlogCacheDto;
import com.ll.blog.entity.Blog;
import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import com.ll.blog.service.BlogService;
import com.ll.blog.service.TagService;
import com.ll.blog.service.TypeService;
import com.ll.blog.util.Constants;
import com.ll.blog.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    TagService tagService;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping({"/", "index"})
    public String index(@PageableDefault(sort = "updateTime", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page page = blogService.listPublished(pageable);
        List<Type> types = typeService.findTopByBlogSize(6);
        List<Tag> tags = tagService.findTopByBlogSize(6);
        List<Blog> recommendBlogs = blogService.listRecommentBlogTop(6);
        model.addAttribute("page", page);
        model.addAttribute("types", types);
        model.addAttribute("tags", tags);
        model.addAttribute("recommendBlogs", recommendBlogs);
        return "index";
    }

    @PostMapping("search")
    public String search(@RequestParam String query, @PageableDefault(sort = "updateTime", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page page = blogService.searchLikeTitleContent(query, pageable);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("blog/{id}")
    public String blog(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        if (id == null) return "redirect:/";
        Blog blog = blogService.getAndConvert(id);
        if (blog == null) return "redirect:/";

        ObjectMapper objectMapper = new ObjectMapper();
        String ipAddress = IpUtils.getIPAddress(request);
        String key = Constants.BLOG_CACHE_KEY + id;
        Object object = redisTemplate.opsForValue().get(key);
        BlogCacheDto blogCacheDto;
        if (object == null) {
            blogCacheDto = new BlogCacheDto();
            blogCacheDto.setViews(blog.getViews() == null ? 0 : blog.getViews());
            blogCacheDto.setLikes(blog.getLikes() == null ? 0 : blog.getLikes());
            blogCacheDto.setChanged(false);
        } else {
            blogCacheDto = objectMapper.convertValue(object, new TypeReference<BlogCacheDto>() {
            });
        }
        if (redisTemplate.opsForValue().get("blogWait_" + ipAddress) == null) {//这个ip查看文章,有计数cd
            redisTemplate.opsForValue().set("blogWait_" + ipAddress, 1, 90, TimeUnit.SECONDS);
            blogCacheDto.setViews(blogCacheDto.getViews() + 1);
            blogCacheDto.setChanged(true);
        }

        blog.setLikes(blogCacheDto.getLikes());
        redisTemplate.opsForValue().set(key, blogCacheDto);
        Boolean iMember = redisTemplate.opsForSet().isMember(Constants.USER_BLOG_LIKES_SET + ipAddress, id);
        model.addAttribute("blog", blog);
        model.addAttribute("isLiked", iMember);
        return "blog";
    }

    /**
     * like的存在性校验和计数
     * 存在性校验需要支持删除，但是布隆过滤器不可以删除，增强型可以，布谷鸟过滤器也可以。
     * 先用set实现简单的存在性校验，应对小型项目足够了。给每个用户维护一个Set,未登录的一个ip算一个用户。
     * 不直接更新数据库，先放redis,然后定时更新库。
     * TODO bug:存在性检查和计数可能会不一致，原因有：网络、服务器故障、mysql和redis不能保持同一事务，出错后不能一起回退等。
     * TODO 维护喜欢的博客与用户关联的MySQL储存。
     * @param id
     * @param request
     * @return
     */
    @GetMapping("blog/like/{id}/{flag}")
    public String blogLike(@PathVariable Long id, @PathVariable Integer flag, HttpServletRequest request, Model model) {
        Blog blog = new Blog();
        String ipAddress = IpUtils.getIPAddress(request);
        if (flag != null) {
            blog = blogService.saveLikesCache(id, flag, ipAddress);
        }
        model.addAttribute("blog", blog);
        Boolean iMember = redisTemplate.opsForSet().isMember(Constants.USER_BLOG_LIKES_SET + ipAddress, id);
        model.addAttribute("isLiked", iMember);
        return "blog::likeDiv";
    }


}
