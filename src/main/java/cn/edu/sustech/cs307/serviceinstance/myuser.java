package cn.edu.sustech.cs307.serviceinstance;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.UserService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class myuser implements UserService {
    ResultSet resultSet;
    Connection connection;
    @Override
    public synchronized void removeUser(int userId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from users where id="+userId+";");
            resultSet.next();
            if (resultSet.getRow()==0){throw new EntityNotFoundException();}
            else{
                statement.execute("delete from users where id=" + userId + ";");
            }
        }catch (SQLException sqlException){

        }
    }
    @Override
    public synchronized List<User> getAllUsers()  {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            List<User>users=new ArrayList<>();
            resultSet =statement.executeQuery("select * from users;");
            while(resultSet.next()){
                if (resultSet.getRow()==0){
                    throw new EntityNotFoundException();
                }else {
                    int id= resultSet.getInt("id");
                    int kind = resultSet.getInt("kind");
                    if(kind==0){
                        User student=getUser(id);
                        users.add(student);
                    }else{
                        User instructor=getUser(id);
                        users.add(instructor);
                    }
                }
            }
            return users;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public synchronized User getUser(int userId) {
            try {
                if(connection==null){
                    connection= SQLDataSource.getInstance().getSQLConnection();}
                Statement statement = connection.createStatement();
                resultSet =statement.executeQuery("select * from users where id ="+userId+";");
                resultSet.next();
                if (resultSet.getRow()==0){throw new EntityNotFoundException();}
                else{
                    int kind= resultSet.getInt("kind");
                    String name= resultSet.getString("name");
                    if(kind==0){
                        resultSet =statement.executeQuery("select * from student where id ="+userId+";");
                        resultSet.next();
                        Student student= new Student();
                        student.enrolledDate= resultSet.getDate("enrolled_date");
                        student.id=userId;
                        student.fullName=name;
                        student.major=getMajor(resultSet.getInt("major_id"));
                        return student;}
                    Instructor instructor= new Instructor();
                    instructor.fullName=name;
                    instructor.id=userId;
                    return instructor ;
                }

            }catch (SQLException sqlException){
                sqlException.printStackTrace();
                throw new EntityNotFoundException();
            }
    }
    public synchronized Major getMajor(int majorId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet1 =statement.executeQuery("select * from major where id ="+majorId+";");
            resultSet1.next();
            if (resultSet1.getRow()==0){throw new EntityNotFoundException();}else
            {
                Major major=new Major();
                major.id=majorId;
                major.name= resultSet1.getString("name");
                major.department=getDepartment(resultSet1.getInt("department_id"));
                return major;
            }

        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }
    public synchronized Department getDepartment(int departmentId) {//ok
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet2 = statement.executeQuery("select * from department where id =" + departmentId + ";");
            resultSet2.next();
            if (resultSet2.getRow()==0){throw new EntityNotFoundException();}
            else {
                Department department = new Department();
                department.id = departmentId;
                department.name = resultSet2.getString("name");
                return department;
            }

        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

}

