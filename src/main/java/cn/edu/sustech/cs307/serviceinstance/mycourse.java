package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.CourseSectionClass;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.service.*;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class mycourse implements CourseService {

ResultSet resultSet;
public int addPre(Prerequisite coursePrerequisite) throws SQLException {
    Connection connection= SQLDataSource.getInstance().getSQLConnection();
    Statement statement = connection.createStatement();
    if(coursePrerequisite instanceof CoursePrerequisite){
resultSet=statement.executeQuery("insert into prerequisite(fid,kind) values('"+((CoursePrerequisite) coursePrerequisite).courseID+"',0)");
   resultSet.next();
   return resultSet.getInt("id");
    }
    else if (coursePrerequisite instanceof AndPrerequisite){
        resultSet=statement.executeQuery("");
    }
    else {}
}
    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite coursePrerequisite) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        if(coursePrerequisite.equals(null)){
            PreparedStatement stmt=connection.prepareStatement("insert into course(id,name,credit,class_hour,grading) values (?,?,?,?,?);");
    stmt.setString(1,courseId);
    stmt.setString(2,courseName);
    stmt.setInt(3,credit);
stmt.setInt(4,classHour);
stmt.setString(5,grading.toString());
stmt.execute();
}
else {int pre_id = addPre(coursePrerequisite);
            PreparedStatement stmt=connection.prepareStatement("insert into course(id,name,credit,class_hour,grading, prerequisite_id) values (?,?,?,?,?,?);");
            stmt.setString(1,courseId);
            stmt.setString(2,courseName);
            stmt.setInt(3,credit);
            stmt.setInt(4,classHour);
            stmt.setString(5,grading.toString());
            stmt.setInt(6,pre_id);
}
    }

    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {


        return 0;
    }
    private int num=0;
    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, List<Short> weekList, short classStart, short classEnd, String location) {
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();

            PreparedStatement stmt=connection.prepareStatement(
                    "insert into class(instructor_id,coursesection_id, class_begin, class_end,dayofweek ,weeklist,location) values (?,?,?,?,?,?,?,?);" +
                            "SELECT currval(pg_get_serial_sequence('class', 'id'));"
            )){
            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);
            stmt.setInt(3, classStart);
            stmt.setInt(4, classEnd);
            stmt.setString(5, dayOfWeek.toString());
            Array week = connection.createArrayOf("int", weekList.toArray());
            stmt.setArray(6, week);
            stmt.setString(7, location);
           resultSet= stmt.executeQuery();
           resultSet.next();
           return resultSet.getInt("id");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void removeCourse(String courseId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from course where id='"+courseId+"';");
    }

    @Override
    public void removeCourseSection(int sectionId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from coursesection where id="+sectionId+";");
    }

    @Override
    public void removeCourseSectionClass(int classId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from class where id="+classId+";");
    }

    @Override
    public List<Course> getAllCourses() {
        return null;
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        return null;
    }

    @Override
    public Course getCourseBySection(int sectionId) {
        return null;
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        return null;
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        return null;
    }

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        return null;
    }
}
