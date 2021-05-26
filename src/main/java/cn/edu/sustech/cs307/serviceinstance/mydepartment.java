package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.List;

public class mydepartment implements  DepartmentService {
    ResultSet resultSet;
    @Override
    public int addDepartment(String name) {

        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "INSERT INTO department VALUES (1,'sb')"
            )){
            //stmt.setInt(1, userId);
//          stmt.setInt(2, userId);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }


        return 0;
    }

    @Override
    public void removeDepartment(int departmentId) {

    }

    @Override
    public List<Department> getAllDepartments() {
        return null;
    }

    @Override
    public Department getDepartment(int departmentId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from department where id ="+departmentId+";");
        Department department=new Department();
        department.id=departmentId;
        department.name=resultSet.getString("name");
        return department;
    }
}
