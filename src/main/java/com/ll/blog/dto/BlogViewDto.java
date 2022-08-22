package com.ll.blog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * description
 * @author lilei
 * @date 2022/8/22 18:54
 */
@Data
public class BlogViewDto implements Serializable {
    public BlogViewDto(){

    }
    public BlogViewDto(Integer nowView, Integer lastTimeView){
        this.nowView = nowView;
        this.lastTimeView = lastTimeView;
    }
    private Integer nowView;
    private Integer lastTimeView;
}
