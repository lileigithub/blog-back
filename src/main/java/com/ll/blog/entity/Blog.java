package com.ll.blog.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "t_blog")
@ToString
public class Blog {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String content;
    /**
     * 首图
     */
    private String firstPicture;
    /**
     * 标记
     */
    private String flag;
    /**
     * 浏览次数
     */
    @Column(columnDefinition = "integer default 0")
    private Integer views = 0;
    /**
     * 点赞数
     * //TODO 还没有多租户登录，所以先只计数，以后再加关联表
     */
    @Column(columnDefinition = "integer default 0")
    private Integer likes = 0;
    /**
     * 描述
     */
    private String description;
    /**
     * 赞赏开启
     */
    private boolean appreciation;
    /**
     * 版权开启
     */
    private boolean shareStatement;
    /**
     * 评论开启
     */
    private boolean commentabled;
    /**
     * 发布
     */
    private boolean published;
    /**
     * 推荐
     */
    private boolean recommend;
    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToOne
    private Type type;

    @ManyToMany(cascade = CascadeType.PERSIST) //级联新增
    private List<Tag> tags = new ArrayList<>();

    @Transient
    private String tagIds;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "blog")
    private List<Comment> comments = new ArrayList<>();
}
