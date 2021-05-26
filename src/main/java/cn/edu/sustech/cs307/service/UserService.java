package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.User;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UserService {
    void removeUser(int userId) throws SQLException;

    List<User> getAllUsers() throws SQLException;

    User getUser(int userId) throws SQLException;
}
