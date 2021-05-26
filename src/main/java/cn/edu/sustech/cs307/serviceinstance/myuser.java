package cn.edu.sustech.cs307.serviceinstance;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Instructor;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.service.UserService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class myuser implements UserService {
    ResultSet resultSet;
    @Override
    public void removeUser(int userId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from users where id="+userId+";");
    }


    @Override
    public List<User> getAllUsers() throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<User>users=new ArrayList<>();
        resultSet=statement.executeQuery("select * from users;");
        while(resultSet.next()){
            int id=resultSet.getInt("id");
            users.add(getUser(id));
        }
        return users;
    }

    @Override
    public User getUser(int userId) throws SQLException {
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
