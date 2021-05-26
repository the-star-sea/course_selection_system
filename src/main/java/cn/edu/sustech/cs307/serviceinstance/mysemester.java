package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.service.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class mysemester implements SemesterService{
    @Override
    public int addSemester(String name, Date begin, Date end)
    {
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "insert into semester values (?,?,?);"
            )){
            stmt.setString(1, name);
            stmt.setDate(2, begin);
            stmt.setDate(3, end);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void removeSemester(int semesterId) {

    }

    @Override
    public List<Semester> getAllSemesters() {
        return null;
    }

    @Override
    public Semester getSemester(int semesterId) {
        return null;
    }
}
