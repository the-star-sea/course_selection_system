package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.grade.PassOrFailGrade;
import cn.edu.sustech.cs307.service.*;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class mystudent implements StudentService{
    ResultSet resultSet;//to do 疑似要每次重新定义
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
        statement.execute("delete from student_grade where student_id="+studentId+" and selection_id= "+sectionId+";");
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
if(grade instanceof HundredMarkGrade){
    statement.execute("update student_grade set kind=0 where student_id=" +studentId+" section_id="+sectionId+ ";");
    resultSet=statement.executeQuery("select id from student_grade where student_id=" +studentId+" section_id="+sectionId+ ";");
    resultSet.next();
    int id=resultSet.getInt("id");
    statement.execute("update student_grade_hundred set grade="+((HundredMarkGrade) grade).mark+" where student_grade_id="+id+";");
}
else if(grade instanceof PassOrFailGrade){
    statement.execute("update student_grade set kind=1 where student_id=" +studentId+" section_id="+sectionId+ ";");
    resultSet=statement.executeQuery("select id from student_grade where student_id=" +studentId+" section_id="+sectionId+ ";");
    resultSet.next();
    int id=resultSet.getInt("id");
    statement.execute("update student_grade_hundred set grade="+((PassOrFailGrade) grade).name()+" where student_grade_id="+id+";");
}
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
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select pre_base_id from course where id="+courseId+";");
        resultSet.next();
        int pre_base=resultSet.getInt("pre_base_id");
        return testpre(pre_base);
    }

    private boolean testpre(int pre_id) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from prerequisite where id="+pre_id+";");
        resultSet.next();
        int[]pres=(int[])resultSet.getArray("content").getArray();
        int kind=resultSet.getInt("kind");
        if(kind==0){
            resultSet=statement.executeQuery("");
            return passedCourse();
        }

    }

    public boolean passedCourse(int studentId, String courseId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select kind,student_grade.id from student_grade join coursesection c on c.id = student_grade.section_id where course_id=" +courseId+
                " and student_id=" +studentId+
                ";");
        resultSet.next();
        int sgi=resultSet.getInt("student_grade.id");
        int kind=resultSet.getInt("kind");
        if(kind==0){
           resultSet=statement.executeQuery("select grade from student_grade_hundred where student_grade_id="+sgi+";");
           resultSet.next();
           if(resultSet.getInt("grade")>=60)return true;
           return false;

        }
if(kind==1) {
            resultSet=statement.executeQuery("select grade from student_grade_pf where student_grade_id="+sgi+";");
            resultSet.next();
            if(resultSet.getString("grade").equals("PASS"))return true;
            return false;
        }
throw new Exception();
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
