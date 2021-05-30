package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.grade.PassOrFailGrade;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
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
    public EnrollResult enrollCourse(int studentId, int sectionId) throws SQLException {//todo
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from student_grade where student_id="+studentId+" and section_id="+sectionId+";");
        resultSet.next();
        return null;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException, SQLException {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            statement.execute("delete from student_grade where student_id=" + studentId + " and selection_id= " + sectionId + ";");
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        if(grade==null){
            statement.execute("insert into student_grade(student_id,section_id) values (" +studentId+","+sectionId+
                    ");");
        }
        if(grade instanceof HundredMarkGrade){
            statement.execute("insert into student_grade(student_id,section_id,kind) values (" +studentId+","+sectionId+","+
                    "0);");
            resultSet=statement.executeQuery("select max(id)as id from student_grade;");
            resultSet.next();
            statement.execute("insert into student_grade_hundred (student_grade_id,grade) values("+resultSet.getInt("id")+","+((HundredMarkGrade) grade).mark+")");

        }
        if(grade instanceof PassOrFailGrade){
            statement.execute("insert into student_grade(student_id,section_id,kind) values (" +studentId+","+sectionId+","+
                    "1);");
            resultSet=statement.executeQuery("select max(id)as id from student_grade;");
            resultSet.next();
            statement.execute("insert into student_grade_hundred (student_grade_id,grade) values("+resultSet.getInt("id")+","+((PassOrFailGrade) grade).name()+")");
        }
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
    public CourseTable getCourseTable(int studentId, Date date) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement preparedStatement= connection.prepareStatement("select ?-semester_begin from semester where ? between semester_begin and semester_end; ");

    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from course where id="+courseId+";");
        resultSet.next();
        int pre_id=resultSet.getInt("prerequisite_id");
        return testpre(studentId,pre_id);
    }

    private boolean testpre(int studentId, int pre_id) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from prerequisite where id="+pre_id+";");
        resultSet.next();
        int[]pres=(int[])resultSet.getArray("content").getArray();
        int kind=resultSet.getInt("kind");
        if(kind==0){
            resultSet=statement.executeQuery("select * from course where pre_base_id="+pre_id+";");
            resultSet.next();
            return passedCourse(studentId,resultSet.getString("id"));
        }
        else if(kind==1){
            boolean ans=true;
            for(int i=0;i<pres.length;i++){
                ans=ans&testpre(studentId,pres[i]);
            }
            return ans;
        }
        else if(kind==2){
            boolean ans=true;
            for(int i=0;i<pres.length;i++){
                ans=ans|testpre(studentId,pres[i]);
            }
            return ans;
        }
return false;
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
return false;
    }
    @Override
    public Major getStudentMajor(int studentId) throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select major_id from student where id =" + studentId + ";");
            resultSet.next();
            Major major=new mymajor().getMajor(resultSet.getInt("major_id"));
            return major;
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }

    }
}
