package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.service.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class mymajor implements MajorService{
    private int num=0;
    @Override
    public int addMajor(String name, int departmentId)
    {
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "insert into major values (?,?,?);"
            )){
            stmt.setInt(1, num);
            num++;
            stmt.setString(2, name);
            stmt.setInt(3, departmentId);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
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
    public Major getMajor(int majorId) {
        return null;
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "insert major_course values (?,?,0);"
            )){
            stmt.setInt(1,majorId);
            stmt.setString(2, courseId);

            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "insert major_course values (?,?,1);"
            )){
            stmt.setInt(1,majorId);
            stmt.setString(2, courseId);

            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
