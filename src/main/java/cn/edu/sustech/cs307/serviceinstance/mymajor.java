package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import cn.edu.sustech.cs307.exception.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mymajor implements MajorService{
    ResultSet resultSet;
    @Override
    public synchronized int addMajor(String name, int departmentId){
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement statement=connection.prepareStatement("insert into major(name,department_id) values ('"+name+"',"+departmentId+");");
            statement.execute();
            Statement statement1 = connection.createStatement();
            resultSet = statement1.executeQuery("select id from major where name='"+name+"';");
            resultSet.next();
            return resultSet.getInt("id");
        }catch (SQLException sqlException) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized void removeMajor(int majorId) {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from major where id="+majorId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from major where id="+majorId+";");
        }catch (SQLException exception){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized List<Major> getAllMajors()  {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();

            List<Major>majors=new ArrayList<>();
            resultSet=statement.executeQuery("select * from major;");

            while(resultSet.next()){
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                Major major=new mymajor().getMajor(resultSet.getInt("id"));
                majors.add(major);
            }
            return majors;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    @Override
    public synchronized Major getMajor(int majorId){
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from major where id ="+majorId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Major major=new Major();
            major.id=majorId;
            major.name=resultSet.getString("name");
            major.department=new mydepartment().getDepartment(resultSet.getInt("department_id"));
            return major;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    @Override
    public synchronized void addMajorCompulsoryCourse(int majorId, String courseId){//todo
        try{
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            statement.execute("insert into major_course(course_id,major_id) values ('"+courseId+"',"+majorId+");");
            PreparedStatement statement1=connection.prepareStatement("update course set coursetype='MAJOR_COMPULSORY' where id=?;");
            statement1.setString(1,courseId);
            statement1.execute();
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }
    @Override
    public synchronized void addMajorElectiveCourse(int majorId, String courseId){//todo
        try{
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            statement.execute("insert into major_course(course_id,major_id) values ('"+courseId+"',"+majorId+");");
            PreparedStatement statement1=connection.prepareStatement("update course set coursetype='MAJOR_ELECTIVE' where id=?;");
            statement1.setString(1,courseId);
            statement1.execute();
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }
}
