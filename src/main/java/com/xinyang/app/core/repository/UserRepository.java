package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.User;

public interface UserRepository  extends XyRepository<User>{

    User findUserByUsername(String username);

}
