package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mysemester implements SemesterService{
    @Override
    public int addSemester(String name, Date begin, Date end) {
        return 0;
    }

    @Override
    public void removeSemester(int semesterId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from semester where id="+semesterId+";");
    }

    @Override
    public List<Semester> getAllSemesters() throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<Semester>semesters=new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("select * from department;");
        while(resultSet.next()){

            semesters.add(getSemester(resultSet.getInt("id")));
        }
        return semesters;
    }

    @Override
    public Semester getSemester(int semesterId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from users where id =" + semesterId + ";");
        resultSet.next();
        Semester semester=new Semester();
        semester.id=resultSet.getInt("id");
        semester.name=resultSet.getString("name");
        return semester;
    }
}
