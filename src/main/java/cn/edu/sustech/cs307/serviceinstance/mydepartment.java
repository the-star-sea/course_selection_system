package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mydepartment implements  DepartmentService {
    ResultSet resultSet;
    @Override
    public int addDepartment(String name) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("insert into department(name) values ('"+name+"');");
        resultSet=statement.executeQuery("select id from department where name='"+name+"';");
        resultSet.next();
        return resultSet.getInt("id");
    }

    @Override
    public void removeDepartment(int departmentId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from department where id="+departmentId+";");
    }

    @Override
    public List<Department> getAllDepartments() throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();

        List<Department>departments=new ArrayList<>();
        resultSet=statement.executeQuery("select * from department;");
        while(resultSet.next()){
            departments.add(getDepartment(resultSet.getInt("id")));
        }
        return departments;
    }

    @Override
    public Department getDepartment(int departmentId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();

        resultSet=statement.executeQuery("select * from department where id ="+departmentId+";");
        resultSet.next();
        Department department=new Department();
        department.id=departmentId;
        department.name=resultSet.getString("name");
        return department;
    }
}
