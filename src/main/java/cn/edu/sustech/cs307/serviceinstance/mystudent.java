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
    Connection connection;
    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try {
            if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
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
    public synchronized Course getCourseBySection(int sectionId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
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
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }
    @Override
    public synchronized List<CourseSearchEntry> searchCourse(int studentId, int semesterId, @Nullable String searchCid, @Nullable String searchName, @Nullable String searchInstructor, @Nullable DayOfWeek searchDayOfWeek, @Nullable Short searchClassTime, @Nullable List<String> searchClassLocations, CourseType searchCourseType, boolean ignoreFull, boolean ignoreConflict, boolean ignorePassed, boolean ignoreMissingPrerequisites, int pageSize, int pageIndex){
        String sql = null;
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
           sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name,users.name instructor_name ,section_id,class.class_end class_end,class_begin class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class where course.id=coursesection.course_id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and coursesection.semester_id="+semesterId;
            List<CourseSearchEntry>courseSearchEntries=new ArrayList<>();
            if(searchCourseType==CourseType.PUBLIC){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name,users.name instructor_name ,section_id,class.class_end class_end,class_begin class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class where course.id=coursesection.course_id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and course.coursetype='PUBLIC' and coursesection.semester_id="+semesterId;
            }
            if(searchCourseType==CourseType.MAJOR_COMPULSORY){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name,users.name instructor_name ,section_id,class.class_end class_end,class_begin,class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class,student,major_course " +
                        "where course.id=coursesection.course_id and major_course.course_id=course.id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and student.id= "+studentId+" and major_course.major_id=student.major_id and course.coursetype='MAJOR_COMPULSORY' and coursesection.semester_id="+semesterId;
            }
            if(searchCourseType==CourseType.MAJOR_ELECTIVE){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name,users.name instructor_name ,section_id,class.class_end class_end,class_begin,class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class,student,major_course " +
                        "where course.id=coursesection.course_id and major_course.course_id=course.id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and student.id= "+studentId+" and major_course.major_id=student.major_id and course.coursetype='MAJOR_ELECTIVE' and coursesection.semester_id="+semesterId;
            }
            if(searchCourseType==CourseType.CROSS_MAJOR){
                sql="select distinct section_id,course_id,course_name from (select course.id course_id,course.name||'['||coursesection.name||']' course_name,users.name instructor_name ,section_id,class.class_end class_end,class_begin,class_begin, location,leftcapcity,dayofweek   from course,coursesection,users,class,student,major_course " +
                        "where course.id=coursesection.course_id and major_course.course_id=course.id and users.kind=1 and coursesection.id=class.section_id and class.instructor_id=users.id and student.id= "+studentId+" and major_course.major_id<>student.major_id and course.coursetype!='PUBLIC' and coursesection.semester_id="+semesterId;
            }
            if(searchCid!=null)
                sql+=" and course_id='"+searchCid+"'";
            if(searchName!=null)
                sql+=" and course_name='"+searchName+"'";
            if(searchInstructor!=null)
                sql+=" and users.name='"+searchInstructor+"'";
            if(searchDayOfWeek!=null)
                sql+=" and dayofweek='"+searchDayOfWeek+"'";
            if(!ignoreFull)
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
                    if(!passedPrerequisitesForCourse(studentId,courses.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}
                }}
            if(!ignorePassed){
                for(int i=0;i<sections.size();i++){
                    if(passedCourse(studentId,courses.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}
                }}
            if(!ignoreConflict){
                for(int i=0;i<sections.size();i++){
                    if(conflict(studentId,sections.get(i))){sections.remove(i);courses.remove(i);names.remove(i);}
                }}

            for(int i=pageIndex;i<=pageIndex+pageSize&&i<sections.size();i++){
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
                courseSearchEntry.sectionClasses=new HashSet<>(getCourseSectionClasses(sections.get(i)));
                //courseSearchEntry.conflictCourseNames=getConflict(sections.get(i),sections,names);
                courseSearchEntries.add(courseSearchEntry);
            }return courseSearchEntries;
        }catch (SQLException sqlException){
            System.out.println(sql);
            throw new IntegrityViolationException();
        }

    }
    public synchronized List<CourseSectionClass> getCourseSectionClasses(int sectionId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select class_begin, class_end, c.id id, dayofweek, instructor_id, location, weeklist from coursesection join class c on coursesection.id = c.section_id where coursesection.id="+sectionId+";");

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
                List wa=Arrays.asList(tmp);
                courseSectionClass.weekList= new HashSet<Short>(wa);
                courseSectionClasses.add(courseSectionClass);
            }return courseSectionClasses;
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            throw new IntegrityViolationException();
        }
    }

    public synchronized User getUser(int userId) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            ResultSet resultSet1=statement.executeQuery("select * from users where id ="+userId+";");
            resultSet1.next();
            if (resultSet1.getRow()==0)throw new EntityNotFoundException();
            int kind=resultSet1.getInt("kind");
            String name=resultSet1.getString("name");

            if(kind==0){
                resultSet1=statement.executeQuery("select * from student where id ="+userId+";");
                resultSet1.next();
                Student student= new Student();
                student.enrolledDate=resultSet1.getDate("enrolled_date");
                student.id=userId;
                student.fullName=name;student.major=getMajor(resultSet1.getInt("major_id"));
                return student;}
            Instructor instructor= new Instructor();
            instructor.fullName=name;
            instructor.id=userId;
            return instructor ;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    public synchronized Major getMajor(int majorId){
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from major where id ="+majorId+";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            Major major=new Major();
            major.id=majorId;
            major.name=resultSet.getString("name");
            major.department=getDepartment(resultSet.getInt("department_id"));
            return major;
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }
    }

    public synchronized Department getDepartment(int departmentId) {//ok
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
            throw new IntegrityViolationException();
        }
    }

    private synchronized List<String> getConflict(Integer sectionid, ArrayList<Integer> sections, ArrayList<String> names) throws SQLException {//todo
        List<String>conflict=new ArrayList<>();
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        List<CourseSectionClass>classes=getCourseSectionClasses(sectionid);
      for(int i=0;i<sections.size();i++){
          List<CourseSectionClass>classs=getCourseSectionClasses(sections.get(i));
          if(classconflict(classes,classs))conflict.add(names.get(i) );
      }return conflict;
    }

    private synchronized boolean classconflict(List<CourseSectionClass> classes, List<CourseSectionClass> classs) {
        for(CourseSectionClass class1:classes){
            for(CourseSectionClass class2:classs){
                if(class1.dayOfWeek==class2.dayOfWeek)return true;
                if(Math.max(class1.classBegin,class2.classBegin)<Math.min(class1.classEnd,class2.classEnd))return true;
                for(Object week:class1.weekList){//todo
                    for(Object week2:class2.weekList){
                        if(week==week2)
                            return true;
                    }
                }
            }
        }return false;
    }

    @Override
    public synchronized EnrollResult enrollCourse(int studentId, int sectionId)  {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from coursesection where id="+sectionId+";");
            resultSet.next();
            if(resultSet.getRow()==0)return EnrollResult.COURSE_NOT_FOUND;
            int left=resultSet.getInt("leftcapcity");
            resultSet=statement.executeQuery("select * from student_grade where student_id="+studentId+" and section_id="+sectionId+";");
            resultSet.next();
            String courseid=getCourseBySection(sectionId).id;
            if(resultSet.getRow()>0) {
                int kind=resultSet.getInt("kind");
                if (kind == 2) return EnrollResult.ALREADY_ENROLLED;
                if (passedCourse(studentId, courseid)) return EnrollResult.ALREADY_PASSED;
            }
            if(!passedPrerequisitesForCourse(studentId,getCourseBySection(sectionId).id))return EnrollResult.PREREQUISITES_NOT_FULFILLED;
            if(enrolledcourse(studentId,courseid))return EnrollResult.COURSE_CONFLICT_FOUND;
            if(conflict(studentId,sectionId))return EnrollResult.COURSE_CONFLICT_FOUND;//考虑了location
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

    private synchronized boolean conflict(int studentId,int sectionId ) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        List<CourseSectionClass> classes =getCourseSectionClasses(sectionId);
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
    public synchronized void dropCourse(int studentId, int sectionId) throws IllegalStateException{
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from student_grade where student_id=" + studentId + " and section_id= " + sectionId + ";");
            resultSet.next();
            if (resultSet.getRow()==0)throw new EntityNotFoundException();
            if(resultSet.getInt("kind")!=2)throw new IllegalStateException();
            statement.execute("delete from student_grade where student_id=" + studentId + " and section_id= " + sectionId + ";");
        }catch (SQLException exception){
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade){
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
    public synchronized void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
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
    public synchronized boolean enrolledcourse(int studentId, String courseId) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select student_grade.* from student_grade, coursesection c,semester where student_grade.section_id = c.id and c.course_id='" +courseId+"' and  student_id="+studentId+
                " order by semester_begin;");
        while (resultSet.next()){
            if(resultSet.getRow()==0)return false;
            if(resultSet.getInt("kind")==2)return true;
        }
      return false;
    }
    @Override
    public synchronized Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId)  {
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            Map<Course, Grade>maps=new HashMap<>();
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
            throw new IntegrityViolationException();
        }
    }

    @Override
    public synchronized CourseTable getCourseTable(int studentId, Date date) {
        try{
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
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
                    courseTableEntry.instructor= (Instructor)getUser(resultSet.getInt("instructor_id"));
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
    public synchronized boolean passedPrerequisitesForCourse(int studentId, String courseId) {
        try {
            if(connection==null){
                connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select * from course where id='"+courseId+"';");
            resultSet.next();
            int pre_id=resultSet.getInt("prerequisite_id");
            if(pre_id == -1){
                return true;
            }

            return testpre(studentId,pre_id);
        }catch (SQLException sqlException){
            throw new IntegrityViolationException();
        }

    }

    private synchronized boolean testpre(int studentId, int pre_id) throws SQLException {

        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
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
    public synchronized Grade getgrade(int studentId, String courseId) throws SQLException {

        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
            Statement statement = connection.createStatement();
            resultSet=statement.executeQuery("select kind,student_grade.id from student_grade join coursesection c on c.id = student_grade.section_id where course_id='" +courseId+
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

    public synchronized boolean passedSection(int studentId, int sectionId) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
        Statement statement = connection.createStatement();
        resultSet=statement.executeQuery("select * from student_grade where student_id="+studentId+" and section_id="+sectionId+";");
        while (resultSet.next()){
            if(resultSet.getRow()==0)return false;
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
    public synchronized boolean passedCourse(int studentId, String courseId) throws SQLException {
        if(connection==null){
            connection= SQLDataSource.getInstance().getSQLConnection();}
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
    public synchronized Major getStudentMajor(int studentId){
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
            throw new IntegrityViolationException();
        }

    }
}
