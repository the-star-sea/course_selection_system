package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Course;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.CourseSectionClass;
import cn.edu.sustech.cs307.dto.Student;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.service.*;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;

public class mycourse implements CourseService {


    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite coursePrerequisite) {

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
                    "insert into class values (?,?,?,?,?,?,?,?)"
            )){
            stmt.setInt(1, num);
            num++;
            stmt.setInt(2, instructorId);
            stmt.setInt(3, sectionId);
            stmt.setShort(4, classStart);
            stmt.setInt(5, classEnd);
            stmt.setString(5, dayOfWeek.toString());
            stmt.setString(5, weekList.toString());
            stmt.setString(5, location);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void removeCourse(String courseId) {

    }

    @Override
    public void removeCourseSection(int sectionId) {

    }

    @Override
    public void removeCourseSectionClass(int classId) {
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt=connection.prepareStatement(
                    "delete from class where id=?"
            )){
            stmt.setInt(1, classId);
            stmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
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
