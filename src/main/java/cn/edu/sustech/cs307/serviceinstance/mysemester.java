package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mysemester implements SemesterService{
    Connection connection;
    @Override
    public int addSemester(String name, Date begin, Date end)  {
        if(begin.after(end))throw new IntegrityViolationException();
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            PreparedStatement statement = connection.prepareStatement("insert into semester(name,semester_begin ,semester_end )" +
                    " values ('" + name + "',?,?);",Statement.RETURN_GENERATED_KEYS);
            statement.setDate(1, begin);
            statement.setDate(2, end);
            statement.executeUpdate();
            ResultSet resultSet=statement.getGeneratedKeys();
            resultSet.next();
            int tmp=resultSet.getInt(1);
            connection.commit();
            return tmp;
        }catch (SQLException exception){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void removeSemester(int semesterId){
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from semester where id="+semesterId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from semester where id="+semesterId+";");
            connection.commit();
        }
        catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Semester> getAllSemesters()  {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            List<Semester> semesters = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery("select * from semester;");
            while (resultSet.next()) {
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                Semester semester = getSemester(resultSet.getInt("id"));
                semesters.add(semester);
            }
            connection.commit();
            return semesters;
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public Semester getSemester(int semesterId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from semester where id =" + semesterId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Semester semester=new Semester();
            semester.id=resultSet.getInt("id");
            semester.name=resultSet.getString("name");
            semester.begin=resultSet.getDate("semester_begin");
            semester.end=resultSet.getDate("semester_end");
            connection.commit();
            return semester;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }

    }
}
