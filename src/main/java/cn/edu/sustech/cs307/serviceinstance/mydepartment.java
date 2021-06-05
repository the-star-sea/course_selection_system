package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.*;

import cn.edu.sustech.cs307.exception.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mydepartment implements  DepartmentService {
    ResultSet resultSet;
    Connection connection;
    @Override
    public int addDepartment(String name)  {
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from department where name='"+name+"';");
            if(resultSet.getRow()!=0)throw new IntegrityViolationException();
            statement.execute("insert into department(name) values ('"+name+"');");
            resultSet=statement.executeQuery("select id from department where name='"+name+"';");
            resultSet.next();
            return resultSet.getInt("id");
        }catch (SQLException sqlException) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized void removeDepartment(int departmentId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from department where id="+departmentId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from department where id=" + departmentId + ";");
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized List<Department> getAllDepartments()  {//ok
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();

            List<Department>departments=new ArrayList<>();
            resultSet=statement.executeQuery("select * from department;");

            while(resultSet.next()){
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                Department department=new mydepartment().getDepartment(resultSet.getInt("id"));
                departments.add(department);
            }
            return departments;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized Department getDepartment(int departmentId) {//ok
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from department where id =" + departmentId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Department department = new Department();
            department.id = departmentId;
            department.name = resultSet.getString("name");
            return department;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }
}
