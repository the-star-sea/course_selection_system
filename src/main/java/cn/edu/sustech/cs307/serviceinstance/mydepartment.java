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
                    "insert into department values (1,?);"
            )){
            stmt.setString(1, name);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }


        return 0;
    }

    @Override
    public void removeDepartment(int departmentId) {
        try(
            Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "delete from department where id =?;"
            )){
            stmt.setInt(1,departmentId);
            //stmt.setInt(2, departmentId);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
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
