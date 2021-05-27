package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSearchEntry;
import cn.edu.sustech.cs307.dto.CourseTable;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.service.*;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class mystudent implements StudentService{
    ResultSet resultSet;
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement statement = connection.prepareStatement("insert into users(id,firstname,lastname,kind) values ("+userId+",'"+firstName+"','"+lastName+"',0);" +
                "insert into student(id,enrolled_date,major_id) values (" +userId+
                ",?,"+majorId+");");
        statement.setDate(1,enrolledDate);
        statement.execute();
    }

    @Override
    public List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid, @Nullable String searchName, @Nullable String searchInstructor, @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime, @Nullable List<String> searchClassLocations, CourseType searchCourseType, boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed, boolean ignoreMissingPrerequisites, int pageSize, int pageIndex) {
        return null;
    }

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId) {
        return null;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException, SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("delete from student_grade where student_id="+studentId+" and select_id= "+sectionId+";");
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {

    }

    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        return null;
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        return null;
    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        return false;
    }

    @Override
    public Major getStudentMajor(int studentId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select major_id from student where id =" + studentId + ";");
        resultSet.next();
        Major major=new mymajor().getMajor(resultSet.getInt("major_id"));
        return major;
    }
}
