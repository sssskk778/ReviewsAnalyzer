package com.aska.core.service;

import com.aska.core.model.User;

public interface IUserService {

    void ensureExists(String email, String name);

    User getByEmail(String email);

}
