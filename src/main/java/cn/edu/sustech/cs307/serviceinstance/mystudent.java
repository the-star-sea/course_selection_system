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
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.*;

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
    public EnrollResult enrollCourse(int studentId, int sectionId) throws Exception {//todo
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from coursesection where id="+sectionId+";");
        if(resultSet.getRow()==0)return EnrollResult.COURSE_NOT_FOUND;
        resultSet.next();
        int left=resultSet.getInt("leftcapcity");
        resultSet=statement.executeQuery("select * from student_grade where student_id="+studentId+" and section_id="+sectionId+";");
        resultSet.next();
        int kind=resultSet.getInt("kind");
        String courseid=new mycourse().getCourseBySection(sectionId).id;
        if(resultSet.getRow()>0) {
            if (kind == 2) return EnrollResult.ALREADY_ENROLLED;
            if (new mystudent().passedCourse(studentId, courseid)) return EnrollResult.ALREADY_PASSED;
        }
        if(!new mystudent().passedPrerequisitesForCourse(studentId,new mycourse().getCourseBySection(sectionId).id))return EnrollResult.PREREQUISITES_NOT_FULFILLED;
if(enrolledcourse(studentId,courseid))return EnrollResult.COURSE_CONFLICT_FOUND;
return null;

    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException, SQLException {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from student_grade where student_id=" + studentId + " and selection_id= " + sectionId + ";");
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            resultSet.next();
            if(resultSet.getInt("kind")==2)throw new IllegalStateException();
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
    public boolean enrolledcourse(int studentId, String courseId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        Map<Course, Grade>maps=new HashMap<>();
        resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and c.course_id=" +courseId+" and  student_id="+studentId+
                " order by semester_begin;");
        if(resultSet.getRow()==0)return false;
        while (resultSet.next()){
            if(resultSet.getInt("kind")==2)return true;
        }
      return false;
    }
    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        Map<Course, Grade>maps=new HashMap<>();
        if(semesterId == null){
            resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and c.semester_id=semester.id order by semester_begin;");
        }else{resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and semester_id=" +semesterId+
                " and c.semester_id=semester.id order by semester_begin;");}
while (resultSet.next()){
    Course course=new mycourse().getCourseBySection(resultSet.getInt("section_id"));
    Grade grade=new mystudent().getgrade(studentId,course.id);
maps.put(course,grade);
}
return maps;
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        PreparedStatement preparedStatement= connection.prepareStatement("select ?-semester_begin from semester where ? between semester_begin and semester_end; ");
        preparedStatement.setDate(1,date);
        preparedStatement.setDate(2,date);
        resultSet=preparedStatement.executeQuery();
        if (resultSet.getRow()==0)throw new EntityNotFoundException();
        resultSet.next();
        int week=resultSet.getInt(1)/7+1;
        preparedStatement=connection.prepareStatement("select location,class_begin,class_end,course.name coursename,coursesection.name sectionname,instructor_id from class ,coursesection,student_grade ,course where ?=any(weeklist) and student_id=? and class.section_id=coursesection.id and coursesection.id=student_grade.section_id and dayofweek=? and course_id=course.id;");
           preparedStatement.setInt(1,week);
           preparedStatement.setInt(2,studentId);
           CourseTable courseTable=new CourseTable();
           courseTable.table=new HashMap<>();
       for(int i=1;i<=7;i++){
           preparedStatement.setString(3,DayOfWeek.of(i).toString());
           resultSet=preparedStatement.executeQuery();
           List<CourseTable.CourseTableEntry>table=new ArrayList<>();
           while(resultSet.next()){
               CourseTable.CourseTableEntry courseTableEntry=new CourseTable.CourseTableEntry();
               courseTableEntry.courseFullName=resultSet.getString("coursename")+"["+resultSet.getString("sectionname")+"]";
               courseTableEntry.classBegin= (short) resultSet.getInt("class_begin");
               courseTableEntry.classEnd= (short) resultSet.getInt("class_end");
               courseTableEntry.instructor= (Instructor) new myuser().getUser(resultSet.getInt("instructor_id"));
               courseTableEntry.location=resultSet.getString("location");
               table.add(courseTableEntry);
           }courseTable.table.put(DayOfWeek.of(i),table);
       }return courseTable;
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
            boolean ans=false;
            for(int i=0;i<pres.length;i++){
                ans=ans|testpre(studentId,pres[i]);
            }
            return ans;
        }
    return false;
    }
    public Grade getgrade(int studentId, String courseId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select kind,student_grade.id from student_grade join coursesection c on c.id = student_grade.section_id where course_id=" +courseId+
                " and student_id=" +studentId+
                ";");
        if(resultSet.getRow()==0)throw new EntityNotFoundException();
        resultSet.next();
        int sgi=resultSet.getInt("student_grade.id");
        int kind=resultSet.getInt("kind");
        if(kind==0){
            resultSet=statement.executeQuery("select grade from student_grade_hundred where student_grade_id="+sgi+";");
            resultSet.next();
            return new HundredMarkGrade((short) resultSet.getInt("grade"));

        }
        if(kind==1) {
            resultSet=statement.executeQuery("select grade from student_grade_pf where student_grade_id="+sgi+";");
            resultSet.next();

           return PassOrFailGrade.valueOf(resultSet.getString("grade")) ;

        }
        return null;
    }
    public boolean passedSection(int studentId, int sectionId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from student_grade where student_id="+studentId+" and section _id="+sectionId+";");
        if(resultSet.getRow()==0)return false;

        while (resultSet.next()){
        int sgi=resultSet.getInt("id");
        int kind=resultSet.getInt("kind");
        if(kind==0){
            resultSet=statement.executeQuery("select grade from student_grade_hundred where student_grade_id="+sgi+";");
            resultSet.next();
            if(resultSet.getInt("grade")>=60)return true;

        }
        else if(kind==1) {
            resultSet=statement.executeQuery("select grade from student_grade_pf where student_grade_id="+sgi+";");
            resultSet.next();
            if(resultSet.getString("grade").equals("PASS"))return true;

        }
      }  return false;
    }
    public boolean passedCourse(int studentId, String courseId) throws Exception {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<CourseSection>courseSections=new ArrayList<>();
        resultSet=statement.executeQuery("select * from course join coursesection c on course.id = c.course_id where course_id=" +courseId+
                 ";");
        if (resultSet.getRow()==0)return false;
        while(resultSet.next()){
            CourseSection courseSection=new CourseSection();
            courseSection.leftCapacity=resultSet.getInt("leftcapcity");
            courseSection.totalCapacity=resultSet.getInt("totcapcity");
            courseSection.id=resultSet.getInt("id");
            courseSection.name=resultSet.getString("name");
            courseSections.add(courseSection);
        }boolean ans=false;
        for(int i=0;i<courseSections.size();i++){
            ans=ans|passedSection(studentId,courseSections.get(i).id);
        }return ans;
    }
    @Override
    public Major getStudentMajor(int studentId) throws SQLException {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select major_id from student where id =" + studentId + ";");
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            resultSet.next();
            Major major=new mymajor().getMajor(resultSet.getInt("major_id"));
            return major;
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }

    }
}
