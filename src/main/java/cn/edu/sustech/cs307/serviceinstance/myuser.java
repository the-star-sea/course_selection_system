package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Instructor;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.List;

public class myuser implements UserService {
    ResultSet resultSet;
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
    public User getUser(int userId) throws SQLException {//--------------
      Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
         resultSet=statement.executeQuery("select * from users where id ="+userId+";");
         resultSet.next();
        int kind=resultSet.getInt("kind");
        String name=resultSet.getString("firstname")+" "+resultSet.getString("lastname");
        if(kind==0){resultSet=statement.executeQuery("select * from student where id ="+userId+";");
        resultSet.next();
        Student student= new Student();
        student.enrolledDate=resultSet.getDate("enrolled_date");
        student.id=userId;
        student.fullName=name;student.major=new mymajor().getMajor(resultSet.getInt("major_id"));
        return student;}
        Instructor instructor= new Instructor();
        instructor.fullName=name;
        instructor.id=userId;



        return instructor ;
    }
}
