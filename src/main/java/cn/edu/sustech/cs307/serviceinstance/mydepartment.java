package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import javax.persistence.EntityNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mydepartment implements  DepartmentService {
    ResultSet resultSet;
    @Override
    public int addDepartment(String name) throws SQLException {//ok
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("insert into department(name) values ('"+name+"');");
        resultSet=statement.executeQuery("select id from department where name='"+name+"';");
        resultSet.next();
        return resultSet.getInt("id");
    }

    @Override
    public void removeDepartment(int departmentId) throws SQLException {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from department where id="+departmentId+";");
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from department where id=" + departmentId + ";");
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Department> getAllDepartments() throws SQLException {//ok
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();

        List<Department>departments=new ArrayList<>();
        resultSet=statement.executeQuery("select * from department;");
        if (resultSet.getRow()==0)throw new EntityNotFoundException();
        while(resultSet.next()){
            Department department=new mydepartment().getDepartment(resultSet.getInt("id"));
            departments.add(department);
        }
        return departments;
    }

    @Override
    public Department getDepartment(int departmentId) throws SQLException {//ok
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from department where id =" + departmentId + ";");
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            resultSet.next();
            Department department = new Department();
            department.id = departmentId;
            department.name = resultSet.getString("name");
            return department;
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }
}
