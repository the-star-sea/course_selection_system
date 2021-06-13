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
    Connection connection;
    @Override
    public int addDepartment(String name)  {
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into department(name) values ('"+name+"');",Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet=statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        }catch (SQLException sqlException) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void removeDepartment(int departmentId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from department where id="+departmentId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from department where id=" + departmentId + ";");
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Department> getAllDepartments()  {//ok
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();

            List<Department>departments=new ArrayList<>();
            ResultSet resultSet=statement.executeQuery("select * from department;");

            while(resultSet.next()){
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                Department department=getDepartment(resultSet.getInt("id"));
                departments.add(department);
            }
            return departments;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public Department getDepartment(int departmentId) {//ok
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from department where id =" + departmentId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Department department = new Department();
            department.id = departmentId;
            department.name = resultSet.getString("name");
            return department;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }
}
