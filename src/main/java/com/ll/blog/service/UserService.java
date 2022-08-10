package com.ll.blog.service;

import com.ll.blog.entity.User;

public interface UserService {
    User checkUser(String username, String password);
}
