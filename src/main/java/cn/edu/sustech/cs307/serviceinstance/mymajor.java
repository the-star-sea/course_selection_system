package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mymajor implements MajorService{
    ResultSet resultSet;
    @Override
    public int addMajor(String name, int departmentId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement statement=connection.prepareStatement("insert into major(name,department_id) values ('"+name+"',"+departmentId+");");
        statement.execute();
        Statement statement1 = connection.createStatement();
        resultSet = statement1.executeQuery("select id from major where name='"+name+"';");
        resultSet.next();
        return resultSet.getInt("id");
    }

    @Override
    public void removeMajor(int majorId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from major where id="+majorId+";");
    }

    @Override
    public List<Major> getAllMajors() throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<Major>majors=new ArrayList<>();
        resultSet=statement.executeQuery("select * from major;");
        while(resultSet.next()){
            Major major=new mymajor().getMajor(resultSet.getInt("id"));
            majors.add(major);
        }
        return majors;
    }

    @Override
    public Major getMajor(int majorId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from major where id ="+majorId+";");
        resultSet.next();
        Major major=new Major();
        major.id=majorId;
        major.name=resultSet.getString("name");
        major.department=new mydepartment().getDepartment(resultSet.getInt("department_id"));
        return major;
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) throws SQLException {//todo
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement statement=connection.prepareStatement(
                "insert into major_course(courseId,majorId) values ('"+courseId+"',"+majorId+");"+
                "update course set coursetype='MAJOR_COMPULSORY' where courseId=? and majorId=?");
        statement.setString(1,courseId);
        statement.setInt(2,majorId);
        statement.execute();
    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) throws SQLException {//todo
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement statement=connection.prepareStatement(
                "insert into major_course(courseId,majorId) values ('"+courseId+"',"+majorId+");"+
                        "update course set coursetype='MAJOR_ELECTIVE' where courseId=? and majorId=?");
        statement.setString(1,courseId);
        statement.setInt(2,majorId);
        statement.execute();
    }
}
