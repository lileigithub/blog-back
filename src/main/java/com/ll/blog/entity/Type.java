package com.ll.blog.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "t_type")
public class Type {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "名称不能为空")
    private String name;

    @OneToMany(mappedBy = "type")
    @ToString.Exclude
    private List<Blog> blogs = new ArrayList<>();
}
