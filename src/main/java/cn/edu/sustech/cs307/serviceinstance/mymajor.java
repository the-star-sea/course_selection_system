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
    Connection connection;
    @Override
    public int addMajor(String name, int departmentId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            PreparedStatement statement=connection.prepareStatement("insert into major(name,department_id) values ('"+name+"',"+departmentId+");",Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int tmp=resultSet.getInt(1);
            connection.commit();
            return tmp;
        }catch (SQLException sqlException) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void removeMajor(int majorId) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from major where id="+majorId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from major where id="+majorId+";");
            connection.commit();
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Major> getAllMajors()  {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();

            List<Major>majors=new ArrayList<>();
            ResultSet resultSet=statement.executeQuery("select * from major;");

            while(resultSet.next()){
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                Major major=getMajor(resultSet.getInt("id"));
                majors.add(major);
            }
            connection.commit();
            return majors;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }

    }

    @Override
    public Major getMajor(int majorId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from major where id ="+majorId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Major major=new Major();
            major.id=majorId;
            major.name=resultSet.getString("name");
            major.department=getDepartment(resultSet.getInt("department_id"));
            connection.commit();
            return major;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    public Department getDepartment(int departmentId) {//ok
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet1 = statement.executeQuery("select * from department where id =" + departmentId + ";");
            resultSet1.next();
            if (resultSet1.getRow()==0)throw new EntityNotFoundException();
            Department department = new Department();
            department.id = departmentId;
            department.name = resultSet1.getString("name");
            return department;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId){//todo
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            statement.execute("insert into major_course(course_id,major_id) values ('"+courseId+"',"+majorId+");");
            PreparedStatement statement1=connection.prepareStatement("update course set coursetype='MAJOR_COMPULSORY' where id=?;");
            statement1.setString(1,courseId);
            statement1.execute();
            connection.commit();
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }
    @Override
    public void addMajorElectiveCourse(int majorId, String courseId){//todo
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();
                connection.setAutoCommit(false);
            }
            Statement statement = connection.createStatement();
            statement.execute("insert into major_course(course_id,major_id) values ('"+courseId+"',"+majorId+");");
            PreparedStatement statement1=connection.prepareStatement("update course set coursetype='MAJOR_ELECTIVE' where id=?;");
            statement1.setString(1,courseId);
            statement1.execute();
            connection.commit();
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }


}
