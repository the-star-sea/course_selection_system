package cn.edu.sustech.cs307.serviceinstance;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Instructor;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
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
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            statement.execute("delete from users where id=" + userId + ";");
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }
    @Override
    public List<User> getAllUsers() throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            List<User>users=new ArrayList<>();
            resultSet=statement.executeQuery("select * from users;");
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            while(resultSet.next()){
                int id=resultSet.getInt("id");
                int kind =resultSet.getInt("kind");
                if(kind==0){
                    User student=new myuser().getUser(id);
                    users.add(student);
                }else{
                    User instructor=new myuser().getUser(id);
                    users.add(instructor);
                }
            }
            return users;
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public User getUser(int userId) throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from users where id ="+userId+";");
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            resultSet.next();
            int kind=resultSet.getInt("kind");
            String firstname=resultSet.getString("firstname");
            String lastname=resultSet.getString("lastname");
            String name=firstname+lastname;
            if(name.matches("[a-zA-Z]+"))name=firstname+" "+lastname;
            if(kind==0){
                resultSet=statement.executeQuery("select * from student where id ="+userId+";");
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
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }

    }
}
