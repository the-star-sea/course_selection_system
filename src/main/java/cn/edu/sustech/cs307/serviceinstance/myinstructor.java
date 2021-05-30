package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class myinstructor implements InstructorService{
    @Override
    public void addInstructor(int userId, String firstName, String lastName) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();

        statement.execute("insert into users(id,firstname,lastname,kind) values ("+userId+",'"+firstName+"','"+lastName+"',1);");
    }

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();

        List<CourseSection>courseSections=new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("select * from (select  * from class join users u on u.id = class.instructor_id where u.id="+instructorId+" )aa join coursesection on aa.coursesection_id=coursesection.id where kind=1 and semester_id="+semesterId+";");
        if (resultSet.getRow()==0)throw new EntityNotFoundException();
        while(resultSet.next()){
            CourseSection courseSection= new CourseSection();
            courseSection.id=resultSet.getInt("id");
            courseSection.name=resultSet.getString("name");
            courseSection.totalCapacity=resultSet.getInt("totcapcity");
            courseSection.leftCapacity=resultSet.getInt("leftcapcity");
            courseSections.add(courseSection);
        }
        return courseSections;
    }
}
