package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.*;

import javax.annotation.Nullable;
import cn.edu.sustech.cs307.exception.*;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class mycourse implements CourseService {

ResultSet resultSet;
public int addPre(Prerequisite coursePrerequisite) throws Exception {
    Connection connection= SQLDataSource.getInstance().getSQLConnection();
    Statement statement = connection.createStatement();
    if(coursePrerequisite instanceof CoursePrerequisite){
        String courseid=((CoursePrerequisite) coursePrerequisite).courseID;
        resultSet=statement.executeQuery("select * from course where id='"+courseid+"';");
        resultSet.next();
        return resultSet.getInt("pre_base_id");
    }
    else if (coursePrerequisite instanceof AndPrerequisite){
        Object[]pre=new Object[((AndPrerequisite) coursePrerequisite).terms.size()];
        for(int i=0;i<((AndPrerequisite) coursePrerequisite).terms.size();i++){
           pre[i]=addPre(((AndPrerequisite) coursePrerequisite).terms.get(i));
       }Array pres=connection.createArrayOf("int",pre);
        PreparedStatement stmt=connection.prepareStatement("insert into prerequisite (content,kind)values(?,1);");
        stmt.setArray(1,pres);
        stmt.execute();
        Statement statement1=connection.createStatement();
        resultSet=statement1.executeQuery("select max(id) as id from prerequisite;");
        resultSet.next();
        return resultSet.getInt("id");
    }
    else if(coursePrerequisite instanceof OrPrerequisite) {Object[]pre=new Object[((OrPrerequisite) coursePrerequisite).terms.size()];
        for(int i=0;i<((OrPrerequisite) coursePrerequisite).terms.size();i++){
            pre[i]=addPre(((OrPrerequisite) coursePrerequisite).terms.get(i));
        }Array pres=connection.createArrayOf("int",pre);
        PreparedStatement stmt=connection.prepareStatement("insert into prerequisite (content,kind)values(?,2)");
        stmt.setArray(1,pres);
        stmt.execute();
        Statement statement1=connection.createStatement();
        resultSet=statement1.executeQuery("select max(id) as id from prerequisite;");
        resultSet.next();
        return resultSet.getInt("id");}
    throw new Exception();
}
    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite coursePrerequisite) throws Exception {
        if (credit<0||classHour<0){
            throw new IntegrityViolationException();
        }
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement=connection.createStatement();
        statement.execute("insert into prerequisite (kind)values(0) ;");
        resultSet=statement.executeQuery("select max(id) as id from prerequisite;");
        resultSet.next();
        int prebas=resultSet.getInt("id");
        if(coursePrerequisite==null){
            PreparedStatement stmt=connection.prepareStatement("insert into course(id,name,credit,class_hour,grading,pre_base_id,coursetype) values (?,?,?,?,?,?,'PUBLIC');");
            stmt.setString(1,courseId);
            stmt.setString(2,courseName);
            stmt.setInt(3,credit);
            stmt.setInt(4,classHour);
            stmt.setString(5,grading.toString());
            stmt.setInt(6,prebas);
            stmt.execute();
        }
        else {int pre_id = addPre(coursePrerequisite);
            PreparedStatement stmt=connection.prepareStatement("insert into course(id,name,credit,class_hour,grading, prerequisite_id,pre_base_id,coursetype) values (?,?,?,?,?,?,?,'PUBLIC');");
            stmt.setString(1,courseId);
            stmt.setString(2,courseName);
            stmt.setInt(3,credit);
            stmt.setInt(4,classHour);
            stmt.setString(5,grading.toString());
            stmt.setInt(6,pre_id);
            stmt.setInt(7,prebas);
            stmt.execute();
        }
    }

    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) throws SQLException{
        if (totalCapacity<0){
            throw new IntegrityViolationException();
        }
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        statement.execute("insert into coursesection(semester_id,name,course_id,totcapcity,leftcapcity) values ("+semesterId+",'"+sectionName+"','"+courseId+"',"+totalCapacity+","+totalCapacity+");");
        ResultSet resultSet = statement.executeQuery("select id from coursesection where course_id='" + courseId + "'and semester_id=" + semesterId + " and name='"+sectionName+"';");
        resultSet.next();
        return resultSet.getInt("id");
    }
    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, List<Short> weekList, short classStart, short classEnd, String location) throws Exception {
        if (classStart>classEnd){
            throw new IntegrityViolationException();
        }
        try(Connection connection=
                    SQLDataSource.getInstance().getSQLConnection();

            PreparedStatement stmt=connection.prepareStatement(
                    "insert into class(instructor_id,section_id, class_begin, class_end,dayofweek ,weeklist,location) values (?,?,?,?,?,?,?);"
            )){
            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);
            stmt.setInt(3, classStart);
            stmt.setInt(4, classEnd);
            stmt.setString(5, dayOfWeek.toString());
            Array week = connection.createArrayOf("int", weekList.toArray());
            stmt.setArray(6, week);
            stmt.setString(7, location);
            stmt.execute();
            Statement statement=connection.createStatement();
           resultSet= statement.executeQuery("select max(id) as id from class;");
           resultSet.next();
           return resultSet.getInt("id");
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new Exception();
    }

    @Override
    public void removeCourse(String courseId) throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from course where id='"+courseId+"';");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from course where id='"+courseId+"';");
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void removeCourseSection(int sectionId) throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from coursesection where id="+sectionId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from coursesection where id="+sectionId+";");
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void removeCourseSectionClass(int classId) throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from class where id="+classId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            statement.execute("delete from class where id="+classId+";");
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }

    }

    @Override
    public List<Course> getAllCourses() throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<Course>courses=new ArrayList<>();
        resultSet=statement.executeQuery("select * from course;");
        while(resultSet.next()){
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Course course=new Course();
            course.classHour=resultSet.getInt("class_hour");
            course.credit=resultSet.getInt("credit");
            course.id=resultSet.getString("id");
            course.grading= Course.CourseGrading.valueOf(resultSet.getString("grading"));
            courses.add(course);
        }
        return courses;
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
       List<CourseSection>courseSections=new ArrayList<>();
        resultSet=statement.executeQuery("select * from course join coursesection c on course.id = c.course_id where course_id=" +courseId+
                "and semester_id=" +semesterId+ ";");
        while(resultSet.next()){
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            CourseSection courseSection=new CourseSection();
            courseSection.leftCapacity=resultSet.getInt("leftcapcity");
            courseSection.totalCapacity=resultSet.getInt("totcapcity");
            courseSection.id=resultSet.getInt("id");
            courseSection.name=resultSet.getString("name");
            courseSections.add(courseSection);
        }
        return courseSections;

    }

    @Override
    public Course getCourseBySection(int sectionId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from course join coursesection c on course.id = c.course_id where c.id="+sectionId+
                ";");
        resultSet.next();
        if (resultSet.getRow()==0)throw new EntityNotFoundException();
        Course course=new Course();
        course.grading= Course.CourseGrading.valueOf(resultSet.getString("grading"));
        course.id=resultSet.getString("course_id");
        course.credit=resultSet.getInt("credit");
        course.classHour=resultSet.getInt("class_hour");
        course.name=resultSet.getString("name");
        return course;
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from coursesection join class c on coursesection.id = c.section_id where coursesection.id="+sectionId+";");

        List<CourseSectionClass>courseSectionClasses=new ArrayList<>();
        while (resultSet.next()){
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            CourseSectionClass courseSectionClass=new CourseSectionClass();
            courseSectionClass.classBegin= (short) resultSet.getInt("class_begin");
            courseSectionClass.classEnd= (short) resultSet.getInt("class_end");
            courseSectionClass.id=resultSet.getInt("c.id");
            courseSectionClass.dayOfWeek=DayOfWeek.valueOf(resultSet.getString("dayofweek"));
            courseSectionClass.instructor= (Instructor) new myuser().getUser(resultSet.getInt("instructor_id"));
            courseSectionClass.location=resultSet.getString("location");
            courseSectionClass.weekList= (List<Short>) resultSet.getArray("weeklist");
            courseSectionClasses.add(courseSectionClass);
        }return courseSectionClasses;
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from coursesection join class c on coursesection.id = c.section_id where c.id="+classId+";");
        resultSet.next();
        if (resultSet.getRow()==0)throw new EntityNotFoundException();
        CourseSection courseSection=new CourseSection();
        courseSection.id=resultSet.getInt("coursesection.id");
        courseSection.name=resultSet.getString("name");
        courseSection.totalCapacity=resultSet.getInt("totcapcity");
        courseSection.leftCapacity=resultSet.getInt("leftcapcity");
        return courseSection;

    }

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<Student> students=new ArrayList<>();
        resultSet=statement.executeQuery(
                "select student_id from (select * from coursesection where semester_id="+semesterId+" and course_id='"+courseId+"')x join student_grade on x.id=student_grade.section_id;"
        );
        while (resultSet.next()){
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Student student= (Student) new myuser().getUser(resultSet.getInt("student_id"));
            students.add(student);
        }
        return students;
    }
}
