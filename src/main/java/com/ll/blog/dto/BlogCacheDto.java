package com.ll.blog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * description
 * @author lilei
 * @date 2022/8/22 18:54
 */
@Data
public class BlogCacheDto implements Serializable {
    public BlogCacheDto(){

    }
    public BlogCacheDto(Integer views, Boolean changed){
        this.views = views;
        this.changed = changed;
    }
    private Boolean changed;
    private Integer views = 0;
    private Integer likes = 0;

}
