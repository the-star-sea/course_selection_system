package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.grade.PassOrFailGrade;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.*;

import javax.annotation.Nullable;
import java.sql.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.*;

public class mystudent implements StudentService{
    ResultSet resultSet;
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            String name=firstName+lastName;
            if(name.matches("[a-zA-Z]+"))name=firstName+" "+lastName;
            PreparedStatement statement = connection.prepareStatement("insert into users(id,name,kind) values ("+userId+",'"+name+"',0);" +
                    "insert into student(id,enrolled_date,major_id) values (" +userId+
                    ",?,"+majorId+");");
            statement.setDate(1,enrolledDate);
            statement.execute();
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid, @Nullable String searchName, @Nullable String searchInstructor, @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime, @Nullable List<String> searchClassLocations, CourseType searchCourseType, boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed, boolean ignoreMissingPrerequisites, int pageSize, int pageIndex){
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            String sql="select distinct section_id,course_id,course_name from (select course_id,course.name||'['||section.name||']' course_name,users.name instructor_name ,section_id,class.class_end class_end,class_begin class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class where course_id=coursesection.course_id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id ";
            List<CourseSearchEntry>courseSearchEntries=new ArrayList<>();
            if(searchCid!=null)
                sql+=" and course_id='"+searchCid+"'";
            if(searchName!=null)
                sql+=" and course_name='"+searchName+"'";
            if(searchInstructor!=null)
                sql+=" and instructor_name='"+searchInstructor;
            if(searchDayOfWeek!=null)
                sql+=" and dayofweek='"+searchDayOfWeek+"'";
            if(!ignorePassed)
                sql+=" and leftcapcity>0";
            if(searchClassLocations!=null)
                sql+=" and class_begin<="+searchClassTime+" and class_end>="+searchClassTime;
            if(searchClassLocations!=null){
                sql+=" and location in (";
                sql+=searchClassLocations.get(0);
                for(int i=1;i<searchClassLocations.size();i++){
                    sql+=(","+searchClassLocations.get(i));
                }
                sql+=")";
            }
            sql+=")aa order by course_id;";
            Statement statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);
            ArrayList<Integer>sections=new ArrayList<>();
            ArrayList<String>courses=new ArrayList<>();
            ArrayList<String>names=new ArrayList<>();
            while (resultSet.next()){
                if(resultSet.getRow()==0)return courseSearchEntries;
                sections.add(resultSet.getInt(1));
                courses.add(resultSet.getString(2));
                names.add(resultSet.getString(3));
            }
            if(!ignoreMissingPrerequisites){
                for(int i=0;i<sections.size();i++){
                    if(!new mystudent().passedPrerequisitesForCourse(studentId,courses.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}
                }}
            if(!ignorePassed){
                for(int i=0;i<sections.size();i++){
                    if(!new mystudent().passedCourse(studentId,courses.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}
                }}
            if(!ignoreConflict){
                for(int i=0;i<sections.size();i++){
                    if(!new mystudent().conflict(studentId,sections.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}
                }}
            if(searchCourseType==CourseType.PUBLIC){

            }//todo
            if(searchCourseType==CourseType.MAJOR_COMPULSORY){

            }
            if(searchCourseType==CourseType.MAJOR_ELECTIVE){

            }
            if(searchCourseType==CourseType.CROSS_MAJOR){

            }
            for(int i=pageIndex;i<pageIndex+pageSize;i++){
                CourseSearchEntry courseSearchEntry=new CourseSearchEntry();
                courseSearchEntry.course=new mycourse().getCourseBySection(sections.get(i));
                resultSet=statement.executeQuery("select * from section where section_id="+sections.get(i)+ ";");
                resultSet.next();
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                CourseSection courseSection=new CourseSection();
                courseSection.leftCapacity=resultSet.getInt("leftcapcity");
                courseSection.totalCapacity=resultSet.getInt("totcapcity");
                courseSection.id=resultSet.getInt("id");
                courseSection.name=resultSet.getString("name");

                courseSearchEntry.section=courseSection;
                courseSearchEntry.sectionClasses=new HashSet<>(new mycourse().getCourseSectionClasses(sections.get(i)));
                courseSearchEntry.conflictCourseNames=getConflict(sections.get(i),sections,names);
                courseSearchEntries.add(courseSearchEntry);
            }return courseSearchEntries;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    private List<String> getConflict(Integer sectionid, ArrayList<Integer> sections, ArrayList<String> names) throws SQLException {//todo
        List<String>conflict=new ArrayList<>();
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<CourseSectionClass>classes=new mycourse().getCourseSectionClasses(sectionid);
     resultSet.next();
      for(int i=0;i<sections.size();i++){
          List<CourseSectionClass>classs=new mycourse().getCourseSectionClasses(sections.get(i));
          if(classconflict(classes,classs))conflict.add(names.get(i) );
      }return conflict;
    }

    private boolean classconflict(List<CourseSectionClass> classes, List<CourseSectionClass> classs) {
        for(CourseSectionClass class1:classes){
            for(CourseSectionClass class2:classs){
                if(class1.dayOfWeek==class2.dayOfWeek)return true;
                if(Math.max(class1.classBegin,class2.classBegin)<Math.min(class1.classEnd,class2.classEnd))return true;
                for(Short week:class1.weekList){
                    for(Short week2:class2.weekList){
                        if(week==week2)
                            return true;
                    }
                }
            }
        }return false;
    }

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId)  {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from coursesection where id="+sectionId+";");
            resultSet.next();
            if(resultSet.getRow()==0)return EnrollResult.COURSE_NOT_FOUND;
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
            if(new mystudent().enrolledcourse(studentId,courseid))return EnrollResult.COURSE_CONFLICT_FOUND;
            if(new mystudent().conflict(studentId,sectionId))return EnrollResult.COURSE_CONFLICT_FOUND;//考虑了location
            if(left<=0)return EnrollResult.COURSE_IS_FULL;
            try{
                statement.execute("insert into student_grade(student_id,section_id)values (" +studentId+","+sectionId+
                        ");update coursesection set leftcapcity=leftcapcity-1 where id="+sectionId+";");
                return EnrollResult.SUCCESS;
            }
            catch (Exception exception){
                return EnrollResult.UNKNOWN_ERROR;
            }
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    private boolean conflict(int studentId,int sectionId ) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<CourseSectionClass> classes =new mycourse().getCourseSectionClasses(sectionId);
        resultSet=statement.executeQuery("select class.* from  student_grade ,coursesection,class where student_id=" +studentId+
                " and coursesection.id=student_grade.section_id and coursesection.id=class.section_id and kind=2");
        while(resultSet.next()){
            //String location=resultSet.getString("location");

            Array array=resultSet.getArray("weeklist");
            Object[]weeklists=(Object[])array.getArray();
            String dayofweek=resultSet.getString("dayofweek");
            int class_begin=resultSet.getInt("class_begin");
            int class_end=resultSet.getInt("class_end");
            for(int i=0;i<classes.size();i++){
            //if(classes.get(i).location==location)return false;
            if(classes.get(i).dayOfWeek.toString().equals(dayofweek))return true;
            if(Math.max(class_begin,classes.get(i).classBegin)<=Math.min(class_end,classes.get(i).classEnd))return true;
            for(Short week:classes.get(i).weekList){
                for(int k=0;k<weeklists.length;k++){
                    if(week==weeklists[k])
                    return true;
                }
            }

            }
        } return false;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException{
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from student_grade where student_id=" + studentId + " and section_id= " + sectionId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            if(resultSet.getInt("kind")==2)throw new IllegalStateException();
            statement.execute("delete from student_grade where student_id=" + studentId + " and section_id= " + sectionId + ";");
        }catch (SQLException exception){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade){
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            if(grade==null){
                statement.execute("insert into student_grade(student_id,section_id) values (" +studentId+","+sectionId+
                        ");");
            }Course.CourseGrading courseGrading=new mycourse().getCourseBySection(sectionId).grading;
            if(grade instanceof HundredMarkGrade){

                if(courseGrading== Course.CourseGrading.PASS_OR_FAIL)throw new IntegrityViolationException();
                statement.execute("insert into student_grade(student_id,section_id,kind) values (" +studentId+","+sectionId+","+
                        "0);");
                resultSet=statement.executeQuery("select max(id)as id from student_grade;");
                resultSet.next();
                statement.execute("insert into student_grade_hundred (student_grade_id,grade) values("+resultSet.getInt("id")+","+((HundredMarkGrade) grade).mark+")");
            }
            if(grade instanceof PassOrFailGrade){
                if(courseGrading== Course.CourseGrading.HUNDRED_MARK_SCORE)throw new IntegrityViolationException();
                statement.execute("insert into student_grade(student_id,section_id,kind) values (" +studentId+","+sectionId+","+
                        "1);");
                resultSet=statement.executeQuery("select max(id)as id from student_grade;");
                resultSet.next();
                statement.execute("insert into student_grade_pf (student_grade_id,grade) values("+resultSet.getInt("id")+",'"+((PassOrFailGrade) grade).name()+"');");
            }
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            if(grade instanceof HundredMarkGrade){
                statement.execute("update student_grade set kind=0 where student_id=" +studentId+" and section_id="+sectionId+ ";");
                resultSet=statement.executeQuery("select id from student_grade where student_id=" +studentId+" and section_id="+sectionId+ ";");
                resultSet.next();
                int id=resultSet.getInt("id");
                statement.execute("update student_grade_hundred set grade="+((HundredMarkGrade) grade).mark+" where student_grade_id="+id+";");
            }
            else if(grade instanceof PassOrFailGrade){
                statement.execute("update student_grade set kind=1 where student_id=" +studentId+" and section_id="+sectionId+ ";");
                resultSet=statement.executeQuery("select id from student_grade where student_id=" +studentId+" and section_id="+sectionId+ ";");
                resultSet.next();
                int id=resultSet.getInt("id");
                statement.execute("update student_grade_pf set grade='"+((PassOrFailGrade) grade).name()+"' where student_grade_id="+id+";");
            }
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }
    public boolean enrolledcourse(int studentId, String courseId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and c.course_id='" +courseId+"' and  student_id="+studentId+
                " order by semester_begin;");
        if(resultSet.getRow()==0)return false;
        while (resultSet.next()){
            if(resultSet.getInt("kind")==2)return true;
        }
      return false;
    }
    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId)  {
        try{
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
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        try{
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement preparedStatement1= connection.prepareStatement("select ?-semester_begin from semester where ? between semester_begin and semester_end; ");
            preparedStatement1.setDate(1,date);
            preparedStatement1.setDate(2,date);
            resultSet=preparedStatement1.executeQuery();
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();

            int week=resultSet.getInt(1)/7+1;
            PreparedStatement preparedStatement=connection.prepareStatement("select location,class_begin,class_end,course.name coursename,coursesection.name sectionname,instructor_id from class ,coursesection,student_grade ,course where ?=any(weeklist) and student_id=? and class.section_id=coursesection.id and coursesection.id=student_grade.section_id and dayofweek=? and course_id=course.id;");
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
                }
                Set result=new HashSet(table);
                courseTable.table.put(DayOfWeek.of(i),result);
            }return courseTable;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from course where id='"+courseId+"';");
            resultSet.next();
            int pre_id=resultSet.getInt("prerequisite_id");

            return testpre(studentId,pre_id);
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    private boolean testpre(int studentId, int pre_id) throws SQLException {

            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from prerequisite where id="+pre_id+";");
            resultSet.next();
            Array array=resultSet.getArray("content");
            //Object[]pres=(Object[])array.getArray();

            int kind=resultSet.getInt("kind");
            if(kind==0){
                resultSet=statement.executeQuery("select * from course where pre_base_id="+pre_id+";");
                resultSet.next();
                return passedCourse(studentId,resultSet.getString("id"));
            }
            else if(kind==1){
                Object[]pres=(Object[])array.getArray();
                boolean ans=true;
                for(int i=0;i<pres.length;i++){
                    ans=ans&testpre(studentId, (Integer) pres[i]);
                }
                return ans;
            }
            else if(kind==2){
                Object[]pres=(Object[])array.getArray();
                boolean ans=false;
                for(int i=0;i<pres.length;i++){
                    ans=ans|testpre(studentId, (Integer) pres[i]);
                }
                return ans;
            }
            return false;


    }
    public Grade getgrade(int studentId, String courseId) throws SQLException {

            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select kind,student_grade.id from student_grade join coursesection c on c.id = student_grade.section_id where course_id='" +courseId+
                    " and student_id=" +studentId+
                    "';");
            resultSet.next();
            if(resultSet.getRow()==0)throw new EntityNotFoundException();

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

    public boolean passedSection(int studentId, int sectionId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from student_grade where student_id="+studentId+" and section_id="+sectionId+";");
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
    public boolean passedCourse(int studentId, String courseId) throws SQLException {
        Connection connection= SQLDataSource.getInstance().getSQLConnection();
        Statement statement = connection.createStatement();
        List<CourseSection>courseSections=new ArrayList<>();
        resultSet=statement.executeQuery("select c.leftcapcity, c.totcapcity, c.id id, c.name nname from course join coursesection c on course.id = c.course_id where course_id='" +courseId+
                 "';");
        while(resultSet.next()){
            if (resultSet.getRow()==0)return false;
            CourseSection courseSection=new CourseSection();
            courseSection.leftCapacity=resultSet.getInt("leftcapcity");
            courseSection.totalCapacity=resultSet.getInt("totcapcity");
            courseSection.id=resultSet.getInt("id");
            courseSection.name=resultSet.getString("nname");
            courseSections.add(courseSection);
        }boolean ans=false;
        for(int i=0;i<courseSections.size();i++){
            ans=ans|passedSection(studentId,courseSections.get(i).id);
        }return ans;
    }
    @Override
    public Major getStudentMajor(int studentId){
        try {
            Connection connection= SQLDataSource.getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select major_id from student where id =" + studentId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();

            Major major=new mymajor().getMajor(resultSet.getInt("major_id"));
            return major;
        }catch (SQLException exception){
            throw new IntegrityViolationException();
        }

    }
}
