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
    Connection connection;
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try {
            if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
            String name=firstName+lastName;
//            if(name.matches("[ a-zA-Z]+"))name=firstName+" "+lastName;
            PreparedStatement statement = connection.prepareStatement("insert into users(id,firstname,lastname,kind) values ("+userId+",'"+firstName+"','"+lastName+"',0);" +
                    "insert into student(id,enrolled_date,major_id) values (" +userId+
                    ",?,"+majorId+");");
            statement.setDate(1,enrolledDate);
            statement.execute();
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }
    public Course getCourseBySection(int sectionId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet8=statement.executeQuery("select * from course join coursesection c on course.id = c.course_id where c.id="+sectionId+
                    ";");
            resultSet8.next();
            if (resultSet8.getRow()==0){throw new EntityNotFoundException();}else {
            Course course=new Course();
            course.grading= Course.CourseGrading.valueOf(resultSet8.getString("grading"));
            course.id=resultSet8.getString("course_id");
            course.credit=resultSet8.getInt("credit");
            course.classHour=resultSet8.getInt("class_hour");
            course.name=resultSet8.getString("name");
            return course;}
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }
    @Override
    public List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid, @Nullable String searchName, @Nullable String searchInstructor, @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime, @Nullable List<String> searchClassLocations, CourseType searchCourseType, boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed, boolean ignoreMissingPrerequisites, int pageSize, int pageIndex){
        String sql = null;
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
           sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name ,section_id,class.class_end class_end,class_begin class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class where course.id=coursesection.course_id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and coursesection.semester_id="+semesterId;
            List<CourseSearchEntry>courseSearchEntries=new ArrayList<>();
            if(searchCourseType==CourseType.PUBLIC){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name ,section_id,class.class_end class_end,class_begin class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class where course.id=coursesection.course_id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and course.coursetype='PUBLIC' and coursesection.semester_id="+semesterId;
            }
            if(searchCourseType==CourseType.MAJOR_COMPULSORY){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name ,section_id,class.class_end class_end,class_begin,class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class,student,major_course " +
                        "where course.id=coursesection.course_id and major_course.course_id=course.id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and student.id= "+studentId+" and major_course.major_id=student.major_id and course.coursetype='MAJOR_COMPULSORY' and coursesection.semester_id="+semesterId;
            }
            if(searchCourseType==CourseType.MAJOR_ELECTIVE){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name ,section_id,class.class_end class_end,class_begin,class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class,student,major_course " +
                        "where course.id=coursesection.course_id and major_course.course_id=course.id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and student.id= "+studentId+" and major_course.major_id=student.major_id and course.coursetype='MAJOR_ELECTIVE' and coursesection.semester_id="+semesterId;
            }
            if(searchCourseType==CourseType.CROSS_MAJOR){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name ,section_id,class.class_end class_end,class_begin,class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class,student,major_course " +
                        "where course.id=coursesection.course_id and major_course.course_id=course.id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and student.id= "+studentId+" and major_course.major_id<>student.major_id and course.coursetype!='PUBLIC' and coursesection.semester_id="+semesterId;
            }
            if(searchCid!=null)
                sql+=" and course.id like '%"+searchCid+"%'";
            if(searchName!=null)
                sql+=" and course.name||'['||coursesection.name||']'like '%"+searchName+"%'";
            if(searchInstructor!=null)
                sql+=" and (" + "users.firstname like '" + searchInstructor + "%' or users.lastname like '" + searchInstructor + "%' or users.firstname||users.lastname like '" + searchInstructor + "%' or users.firstname||' '||users.lastname like '" + searchInstructor + "%')";
            if(searchDayOfWeek!=null)
                sql+=" and dayofweek='"+searchDayOfWeek+"'";
            if(ignoreFull)
                sql+=" and leftcapcity>0";
            if(searchClassTime!=null)
                sql+=" and class_begin<="+searchClassTime+" and class_end>="+searchClassTime;

            if(searchClassLocations!=null){
                if(searchClassLocations.size()==0){
                    return courseSearchEntries;
                }
                sql+=" and (location like '%'||'";
                sql+=searchClassLocations.get(0);
                sql+="'||'%'";
                for(int i=1;i<searchClassLocations.size();i++){
                    sql+=" or location like '%'||'";
                    sql+=searchClassLocations.get(i);
                    sql+="'||'%'";
                }
                sql+=")";
            }
            sql+=")aa order by course_id,course_name;";
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(sql);
            ArrayList<Integer>sections=new ArrayList<>();
            ArrayList<String>courses=new ArrayList<>();
            ArrayList<String>names=new ArrayList<>();
            while (resultSet.next()){
                if(resultSet.getRow()==0)return courseSearchEntries;
                sections.add(resultSet.getInt(1));
                courses.add(resultSet.getString(2));
                names.add(resultSet.getString(3));
            }
            if(ignoreMissingPrerequisites){
                int i = 0;
                while(i < sections.size()){
                    if(!passedPrerequisitesForCourse(studentId,courses.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}else{
                        i++;
                    }
                }
            }
            if(ignorePassed){
                int i = 0;
                while(i < sections.size()){
                    if(passedCourse(studentId,courses.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}else{
                        i++;
                    }
                }
            }
            if(ignoreConflict){
                int i = 0;
                while(i < sections.size()){

                    if(conflict(studentId, sections.get(i),semesterId)||enrolledcourse(studentId,getCourseBySection(sections.get(i)).id,semesterId)){sections.remove(i);courses.remove(i);names.remove(i);}else{
                        i++;
                    }
                }
            }
           sql="select distinct course.name||'['||coursesection.name||']' course_name,coursesection.id section_id from coursesection , course , student_grade where coursesection.course_id=course.id and student_grade.section_id=coursesection.id  and student_grade.student_id="+studentId+" and coursesection.semester_id="+semesterId+";";
        resultSet=statement.executeQuery(sql);
            ArrayList<Integer>sections1=new ArrayList<>();
         ArrayList<String>names1=new ArrayList<>();
            while (resultSet.next()){
                if(resultSet.getRow()==0)break;
                sections1.add(resultSet.getInt(2));
                names1.add(resultSet.getString(1));
            }
            for(int i=pageIndex*pageSize;i<pageIndex*pageSize+pageSize&&i<sections.size();i++){
                CourseSearchEntry courseSearchEntry=new CourseSearchEntry();
                courseSearchEntry.course=getCourseBySection(sections.get(i));
                resultSet=statement.executeQuery("select * from coursesection where id="+sections.get(i)+ ";");
                resultSet.next();
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                CourseSection courseSection=new CourseSection();
                courseSection.leftCapacity=resultSet.getInt("leftcapcity");
                courseSection.totalCapacity=resultSet.getInt("totcapcity");
                courseSection.id=resultSet.getInt("id");
                courseSection.name=resultSet.getString("name");

                courseSearchEntry.section=courseSection;
                List<CourseSectionClass>temp=getCourseSectionClasses(sections.get(i));
                courseSearchEntry.sectionClasses=new HashSet<>();
                for (int j = 0; j < temp.size(); j++) {
                    courseSearchEntry.sectionClasses.add(temp.get(j));
                }
                courseSearchEntry.conflictCourseNames=getConflict(sections.get(i),sections1,names1);
                courseSearchEntries.add(courseSearchEntry);
            }
            return courseSearchEntries;
        }catch (SQLException sqlException){
          //System.out.println(sql);
            sqlException.printStackTrace();
            throw new IntegrityViolationException();
        }

    }
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select class_begin, class_end, c.id id, dayofweek, instructor_id, location, weeklist from coursesection join class c on coursesection.id = c.section_id where coursesection.id="+sectionId+";");

            List<CourseSectionClass>courseSectionClasses=new ArrayList<>();
            while (resultSet.next()){
                if (resultSet.getRow()==0)throw new EntityNotFoundException();
                CourseSectionClass courseSectionClass=new CourseSectionClass();
                courseSectionClass.classBegin= (short) resultSet.getInt("class_begin");
                courseSectionClass.classEnd= (short) resultSet.getInt("class_end");
                courseSectionClass.id=resultSet.getInt("id");
                courseSectionClass.dayOfWeek=DayOfWeek.valueOf(resultSet.getString("dayofweek"));
                courseSectionClass.instructor= (Instructor) getUser(resultSet.getInt("instructor_id"));
                courseSectionClass.location=resultSet.getString("location");
                Array array=resultSet.getArray("weeklist");
                //int[] tmp=(int[])array.getArray();
                Object[] tmp=(Object[])array.getArray();
                ArrayList<Short>wa=new ArrayList<>();
                for(Object ob:tmp){
                    wa.add(((Integer) ob).shortValue());
                }
                courseSectionClass.weekList= new HashSet<Short>(wa);
                courseSectionClasses.add(courseSectionClass);
            }return courseSectionClasses;
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new EntityNotFoundException();
        }
    }

    public User getUser(int userId) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet1 =statement.executeQuery("select * from users where id ="+userId+";");
            resultSet1.next();
            if (resultSet1.getRow()==0){throw new EntityNotFoundException();}
            else{
                int kind= resultSet1.getInt("kind");
                String firstName= resultSet1.getString("firstname");
                String lastName=resultSet1.getString("lastname");
                String name=firstName+lastName;
                if(name.matches("[ a-zA-Z]+"))name=firstName+" "+lastName;
                if(kind==0){
                    resultSet1 =statement.executeQuery("select * from student where id ="+userId+";");
                    resultSet1.next();
                    Student student= new Student();
                    student.enrolledDate= resultSet1.getDate("enrolled_date");
                    student.id=userId;
                    student.fullName=name;
                    student.major=getMajor(resultSet1.getInt("major_id"));
                    return student;}
                Instructor instructor= new Instructor();
                instructor.fullName=name;
                instructor.id=userId;
                return instructor ;
            }

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new EntityNotFoundException();
        }
    }

    public Major getMajor(int majorId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from major where id ="+majorId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Major major=new Major();
            major.id=majorId;
            major.name=resultSet.getString("name");
            major.department=getDepartment(resultSet.getInt("department_id"));
            return major;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    public Department getDepartment(int departmentId) {//ok
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet2 = statement.executeQuery("select * from department where id =" + departmentId + ";");
            resultSet2.next();
            if (resultSet2.getRow()==0)throw new EntityNotFoundException();
            Department department = new Department();
            department.id = departmentId;
            department.name = resultSet2.getString("name");
            return department;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    private List<String> getConflict(Integer sectionid, ArrayList<Integer> sections, ArrayList<String> names) throws SQLException {//todo
        List<String>conflict=new ArrayList<>();
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        List<CourseSectionClass>classes=getCourseSectionClasses(sectionid);
      for(int i=0;i<sections.size();i++){
          List<CourseSectionClass>classs=getCourseSectionClasses(sections.get(i));
          if(classconflict(classes,classs)||getCourseBySection(sectionid).id.equals(getCourseBySection(sections.get(i)).id))conflict.add(names.get(i) );
      }return conflict;
    }

    private boolean classconflict(List<CourseSectionClass> classes, List<CourseSectionClass> classs) {
        for(CourseSectionClass class1:classes){
            for(CourseSectionClass class2:classs){
                boolean conflicts=true;
                if(!(class1.dayOfWeek==class2.dayOfWeek))conflicts= false;
                if((class1.classEnd<class2.classBegin&&class1.classEnd<class2.classEnd)||(class2.classEnd<class1.classEnd&&class2.classEnd<class1.classBegin))conflicts= false;
                boolean cc=true;
                for(Object week:class1.weekList){//todo
                    for(Object week2:class2.weekList){
                        if(week.toString().equals(week2.toString()))
                            cc=false;
                    }
                }if(cc)conflicts=false;
                if(conflicts)return true;
            }
        }return false;
    }

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId)  {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            String sql="select * from coursesection where id="+sectionId+";";
            ResultSet resultSet=statement.executeQuery(sql);
            resultSet.next();
            if(resultSet.getRow()==0)return EnrollResult.COURSE_NOT_FOUND;
            int semester_id=resultSet.getInt("semester_id");
            int left=resultSet.getInt("leftcapcity");
            resultSet=statement.executeQuery("select distinct kind from student_grade where student_id="+studentId+" and section_id="+sectionId+";");

            String courseid=getCourseBySection(sectionId).id;
resultSet.next();
            if(resultSet.getRow()>0) {
                return EnrollResult.ALREADY_ENROLLED;
//                int kind=resultSet.getInt(1);
//                if (kind == 2) return EnrollResult.ALREADY_ENROLLED;
//                while(resultSet.next()){
//                if (resultSet.getInt(1) == 2) return EnrollResult.ALREADY_ENROLLED;}

            } if (passedCourse(studentId, courseid)) return EnrollResult.ALREADY_PASSED;
            if(!passedPrerequisitesForCourse(studentId,getCourseBySection(sectionId).id))return EnrollResult.PREREQUISITES_NOT_FULFILLED;
            if(enrolledcourse(studentId,courseid,semester_id))return EnrollResult.COURSE_CONFLICT_FOUND;
            if(conflict(studentId,sectionId,semester_id))return EnrollResult.COURSE_CONFLICT_FOUND;
            if(left<=0)return EnrollResult.COURSE_IS_FULL;
            try{
                statement.execute("insert into student_grade(student_id,section_id,kind)values (" +studentId+","+sectionId+
                        ",2);update coursesection set leftcapcity=leftcapcity-1 where id="+sectionId+";");
                return EnrollResult.SUCCESS;
            }
            catch (Exception exception){
                return EnrollResult.UNKNOWN_ERROR;
            }
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

  public boolean conflict(int studentId, int sectionId,int semester_id) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        List<CourseSectionClass> classes =getCourseSectionClasses(sectionId);
        ResultSet resultSet=statement.executeQuery("select distinct class.* from  student_grade ,coursesection,class where student_id=" +studentId+
                " and coursesection.id=student_grade.section_id and coursesection.id=class.section_id  and coursesection.semester_id="+semester_id+";");
        while(resultSet.next()){
            //String location=resultSet.getString("location");
if(resultSet.getRow()==0)return false;
            Array array=resultSet.getArray("weeklist");
            Object[]weeklists=(Object[])array.getArray();
            String dayofweek=resultSet.getString("dayofweek");
            int class_begin=resultSet.getInt("class_begin");
            int class_end=resultSet.getInt("class_end");
            for(int i=0;i<classes.size();i++){
                Boolean conflicts=true;
            //if(classes.get(i).location==location)return false;
                String tm=classes.get(i).dayOfWeek.toString();
               Boolean v=!tm.equals(dayofweek);
            if(v)conflicts=false;
            if((class_end>classes.get(i).classEnd&&class_begin>classes.get(i).classEnd)||(classes.get(i).classEnd>class_end&&classes.get(i).classBegin>class_end))conflicts= false;
                Boolean tem=true;//no overlap
            for(Object week:classes.get(i).weekList){//todo
                    for(Object week1:weeklists){
                        if(week.toString().equals(week1.toString()))
                            tem=false;
                    }
                }
            if(tem)conflicts=false;
            if(conflicts)return true;
//            for(Short week:classes.get(i).weekList){
//                for(int k=0;k<weeklists.length;k++){
//                    if(week==weeklists[k])
//                    return true;
//                }
//            }

            }
        } return false;
    }

    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException{
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from student_grade where student_id=" + studentId + " and kind=2 and section_id= " + sectionId + ";");
            resultSet.next();
            if (resultSet.getInt("kind") != 2) throw new IllegalStateException();
            else {
                statement.execute("delete from student_grade where student_id=" + studentId + " and kind=2 and section_id= " + sectionId + ";");
                statement.execute("update coursesection set leftcapcity=leftcapcity+1 where id="+sectionId+";");
            }
        }catch (SQLException exception){
            throw new IllegalStateException();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            if(grade==null){
                statement.execute("insert into student_grade(student_id,section_id) values (" +studentId+","+sectionId+
                        ");");
            }Course.CourseGrading courseGrading=getCourseBySection(sectionId).grading;
            if(grade instanceof HundredMarkGrade){

                if(courseGrading== Course.CourseGrading.PASS_OR_FAIL)throw new IntegrityViolationException();

                statement.executeUpdate("insert into student_grade(student_id,section_id,kind) values (" +studentId+","+sectionId+","+
                        "0);",Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSetx1=statement.getGeneratedKeys();
                resultSetx1.next();
                int id2=resultSetx1.getInt(1);
                statement.execute("insert into student_grade_hundred (student_grade_id,grade) values("+id2+","+((HundredMarkGrade) grade).mark+")");
            }
            if(grade instanceof PassOrFailGrade){//todo
                if(courseGrading== Course.CourseGrading.HUNDRED_MARK_SCORE)throw new IntegrityViolationException();
                statement.executeUpdate("insert into student_grade(student_id,section_id,kind) values (" +studentId+","+sectionId+","+
                        "1);",Statement.RETURN_GENERATED_KEYS);
                ResultSet resultSetx2=statement.getGeneratedKeys();
                resultSetx2.next();
                int id1=resultSetx2.getInt(1);
                statement.execute("insert into student_grade_pf (student_grade_id,grade) values("+id1+",'"+((PassOrFailGrade) grade).name()+"');");
            }
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            if(grade instanceof HundredMarkGrade){
                statement.execute("update student_grade set kind=0 where student_id=" +studentId+" and section_id="+sectionId+ ";");
                ResultSet resultSet=statement.executeQuery("select id from student_grade where student_id=" +studentId+" and section_id="+sectionId+ ";");
                resultSet.next();
                int id=resultSet.getInt("id");
                statement.execute("update student_grade_hundred set grade="+((HundredMarkGrade) grade).mark+" where student_grade_id="+id+";");
            }
            else if(grade instanceof PassOrFailGrade){
                statement.execute("update student_grade set kind=1 where student_id=" +studentId+" and section_id="+sectionId+ ";");
                ResultSet resultSet=statement.executeQuery("select id from student_grade where student_id=" +studentId+" and section_id="+sectionId+ ";");
                resultSet.next();
                int id=resultSet.getInt("id");
                statement.execute("update student_grade_pf set grade='"+((PassOrFailGrade) grade).name()+"' where student_grade_id="+id+";");
            }
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }
    public  boolean enrolledcourse(int studentId, String courseId, int semester_id) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
//        if(studentId==11713333&&courseId.equals("CS205")){
//            System.out.println("haha");
//        }
        Statement statement = connection.createStatement();
        String sql="select distinct student_grade.* from student_grade, coursesection c where student_grade.section_id = c.id and c.course_id='" +courseId+"' and  student_id="+studentId+
                "and c.semester_id=" +semester_id+
                ";";
        ResultSet resultSet=statement.executeQuery(sql);
        resultSet.next();
            if(resultSet.getRow()==0)return false;
            return true;

    }
    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId)  {
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            Map<Course, Grade>maps=new HashMap<>();
            ResultSet resultSet;
            if(semesterId == null){
                resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and c.semester_id=semester.id order by semester_begin;");
            }else{resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and semester_id=" +semesterId+
                    " and c.semester_id=semester.id order by semester_begin;");}
            while (resultSet.next()){
                Course course=getCourseBySection(resultSet.getInt("section_id"));
                Grade grade=getgrade(studentId,course.id);
                maps.put(course,grade);
            }
            return maps;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            PreparedStatement preparedStatement1= connection.prepareStatement("select ?-semester_begin,id from semester where ? between semester_begin and semester_end; ");
            preparedStatement1.setDate(1,date);
            preparedStatement1.setDate(2,date);
            ResultSet resultSet=preparedStatement1.executeQuery();
            resultSet.next();
            CourseTable courseTable=new CourseTable();

            if (resultSet.getRow()==0){
                courseTable.table=new HashMap<>();
                for(int i=1;i<=7;i++){
                    courseTable.table.put(DayOfWeek.of(i),new HashSet<>());
                }
            }
            else {
                int week=resultSet.getInt(1)/7+1;
                int semester_id=resultSet.getInt("id");
                PreparedStatement preparedStatement=connection.prepareStatement("select location,class_begin,class_end,course.name coursename,coursesection.name sectionname,instructor_id from class ,coursesection,student_grade ,course where ?=any(weeklist) and student_id=? and class.section_id=coursesection.id and coursesection.id=student_grade.section_id and dayofweek=? and course_id=course.id and coursesection.semester_id="+semester_id+";");
                preparedStatement.setInt(1,week);
                preparedStatement.setInt(2,studentId);

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
                        courseTableEntry.instructor= (Instructor)getUser(resultSet.getInt("instructor_id"));
                        courseTableEntry.location=resultSet.getString("location");
                        table.add(courseTableEntry);
                    }
                    Set result=new HashSet(table);
                    courseTable.table.put(DayOfWeek.of(i),result);
                }

            }return courseTable;
        }catch (SQLException sqlException){
            throw new EntityNotFoundException();
        }
    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from course where id='"+courseId+"';");
            resultSet.next();
            int pre_id=resultSet.getInt("prerequisite_id");
            if(pre_id == -1){
                return true;
            }

            return testpre(studentId,pre_id);
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new IntegrityViolationException();
        }

    }

    private boolean testpre(int studentId, int pre_id) throws SQLException {

        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from prerequisite where id="+pre_id+";");
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

        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select kind,student_grade.id from student_grade join coursesection c on c.id = student_grade.section_id where course_id='" +courseId+
                    "' and student_id="+studentId+";");
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
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        Statement statement1 = connection.createStatement();
       ResultSet resultSet77=statement.executeQuery("select * from student_grade where student_id="+studentId+" and kind<>2 and section_id="+sectionId+";");
        while (resultSet77.next()){

            if(resultSet77.getRow()==0)return false;
        int sgi=resultSet77.getInt("id");
        int kind=resultSet77.getInt("kind");
            ResultSet resultSet9;
        if(kind==0){
            resultSet9=statement1.executeQuery("select grade from student_grade_hundred where student_grade_id="+sgi+";");
            resultSet9.next();
            if(resultSet9.getInt("grade")>=60)return true;

        }
        else if(kind==1) {
            resultSet9=statement1.executeQuery("select grade from student_grade_pf where student_grade_id="+sgi+";");
            resultSet9.next();
            if(resultSet9.getString("grade").equals("PASS"))return true;

        }
      }  return false;
    }
    public boolean passedCourse(int studentId, String courseId) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        List<CourseSection>courseSections=new ArrayList<>();
        ResultSet resultSet=statement.executeQuery("select id from coursesection where course_id='"+courseId+"';");
        boolean ans=false;
        while(resultSet.next()){
            int u=resultSet.getRow();
            if (resultSet.getRow()==0)return false;
            ans=ans|passedSection(studentId,resultSet.getInt(1));
        }
        return ans;
    }
    @Override
    public Major getStudentMajor(int studentId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select major_id from student where id =" + studentId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();

            Major major=getMajor(resultSet.getInt("major_id"));
            return major;
        }catch (SQLException exception){
            throw new EntityNotFoundException();
        }

    }
}
