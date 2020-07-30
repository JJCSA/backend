package com.jjcsa.service;

import com.jjcsa.model.User;

public interface UserService {
    /**
     * Returns user object for a given user ID.
     *
     * @params user ID.
     * @return a User.
     */
    public User getUser(long id);
}
