package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.service.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class mydepartment implements  DepartmentService {
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
    public Department getDepartment(int departmentId) {
        return null;
    }
}
