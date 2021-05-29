package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mysemester implements SemesterService{
    ResultSet resultSet;
    @Override
    public int addSemester(String name, Date begin, Date end) throws Exception {
        if(begin.after(end))throw new Exception();
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement statement=connection.prepareStatement("insert into semester(name,semester_begin ,semester_end )" +
                " values ('"+name+"',?,?);");
        statement.setDate(1,begin);
        statement.setDate(2,end);
        statement.execute();
        Statement statement1 = connection.createStatement();
        resultSet = statement1.executeQuery("select id from semester where name='"+name+"';");
        resultSet.next();
        return resultSet.getInt("id");
    }

    @Override
    public void removeSemester(int semesterId) {
        try{
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from semester where id="+semesterId+";");}
        catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Semester> getAllSemesters() throws SQLException {

        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<Semester>semesters=new ArrayList<>();
       resultSet = statement.executeQuery("select * from department;");
        while(resultSet.next()){
            Semester semester=new mysemester().getSemester(resultSet.getInt("id"));
            semesters.add(semester);
        }
        return semesters;

    }

    @Override
    public Semester getSemester(int semesterId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
       resultSet = statement.executeQuery("select * from users where id =" + semesterId + ";");
        resultSet.next();
        Semester semester=new Semester();
        semester.id=resultSet.getInt("id");
        semester.name=resultSet.getString("name");
        semester.begin=resultSet.getDate("semester_begin");
        semester.end=resultSet.getDate("semester_end");
        return semester;
    }
}
