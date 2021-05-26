package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class mymajor implements MajorService{
    ResultSet resultSet;
    @Override
    public int addMajor(String name, int departmentId) {
        return 0;
    }

    @Override
    public void removeMajor(int majorId) {

    }

    @Override
    public List<Major> getAllMajors() {
        return null;
    }

    @Override
    public Major getMajor(int majorId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from major where id ="+majorId+";");
        Major major=new Major();
        major.id=majorId;
        major.name=resultSet.getString("name");
        major.department=new mydepartment().getDepartment(resultSet.getInt("department_id"));
        return  major;
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {

    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {

    }
}
