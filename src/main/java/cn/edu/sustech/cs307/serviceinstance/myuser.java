package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.service.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class myuser implements UserService {
    @Override
    public void removeUser(int userId) {//--------------
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "delete from users where id=?;"
            )){
            stmt.setInt(1, userId);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUser(int userId) {//--------------
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "select * from users where id=?;"
            )){
            stmt.setInt(1, userId);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null ;
    }
}
