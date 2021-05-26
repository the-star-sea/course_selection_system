package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.service.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class myuser implements UserService {
    @Override
    public void removeUser(int userId) {

    }


    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUser(int userId) {

        return null;
    }
}
