import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.prerequisite.AndPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.OrPrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
import cn.edu.sustech.cs307.serviceinstance.mymajor;

import java.sql.Date;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;

import static cn.edu.sustech.cs307.dto.Course.CourseGrading.HUNDRED_MARK_SCORE;
import static cn.edu.sustech.cs307.dto.Course.CourseGrading.PASS_OR_FAIL;
import static cn.edu.sustech.cs307.dto.grade.PassOrFailGrade.FAIL;
import static cn.edu.sustech.cs307.dto.grade.PassOrFailGrade.PASS;

public class Main {
    public static void main(String[] args) throws Exception {

        ServiceFactory serviceFactory= Config.getServiceFactory();
        UserService userService=serviceFactory.createService(UserService.class);
        MajorService majorService=serviceFactory.createService(MajorService.class);
        InstructorService instructorService=serviceFactory.createService(InstructorService.class);
        CourseService courseService=serviceFactory.createService(CourseService.class);
        DepartmentService departmentService=serviceFactory.createService(DepartmentService.class);
        StudentService studentService=serviceFactory.createService(StudentService.class);
        SemesterService semesterService=serviceFactory.createService(SemesterService.class);

//        departmentService.addDepartment("CSE");//1
//        departmentService.addDepartment("MEE");//2
//        departmentService.addDepartment("ALE");//3
//        departmentService.addDepartment("WPE");//4
//        departmentService.addDepartment("LLE");//5
        //departmentService.removeDepartment("");

//        majorService.addMajor("CS", 1);//1
//        majorService.addMajor("IS", 1);//2
//        majorService.addMajor("RE", 2);//3
//        majorService.addMajor("ME", 2);//4
//        majorService.addMajor("AP", 3);//5
//        majorService.addMajor("AL", 3);//6
//        majorService.addMajor("WP", 4);//7
//        majorService.addMajor("EL", 5);//8
//        majorService.addMajor("SL", 5);//9
//
//        semesterService.addSemester("2021Spring", java.sql.Date.valueOf("2021-01-18"), java.sql.Date.valueOf("2021-06-13"));
//        semesterService.addSemester("2020Fall", java.sql.Date.valueOf("2020-9-6"), java.sql.Date.valueOf("2021-1-16"));
//        semesterService.addSemester("2020Spring", java.sql.Date.valueOf("2020-2-9"), java.sql.Date.valueOf("2021-5-31"));

//        studentService.addStudent(20191208, 5, "LONG", "yaya", java.sql.Date.valueOf("2019-08-15"));
//        studentService.addStudent(20190621, 4, "SAI", "xuxu", java.sql.Date.valueOf("2019-08-15"));
//        studentService.addStudent(20181023, 12, "LIN", "kai", java.sql.Date.valueOf("2018-08-15"));
//        studentService.addStudent(20200319, 9, "WANG", "see", java.sql.Date.valueOf("2020-08-15"));
//        studentService.addStudent(20210509, 8, "AI", "nin", java.sql.Date.valueOf("2021-08-15"));
//        studentService.addStudent(20221208, 7, "HAO", "de", java.sql.Date.valueOf("2022-08-15"));
//
//        instructorService.addInstructor(11912301, "hi", "Wor");
//        instructorService.addInstructor(11203453, "yihai", "WU");
//        instructorService.addInstructor(12003467, "yun", "CHEN");
//        instructorService.addInstructor(11507189, "yes", "AH");
//        instructorService.addInstructor(11602131, "wes", "OP");
        Prerequisite pre_1 = new CoursePrerequisite("CS102");
        Prerequisite pre_2 = new CoursePrerequisite("MA203");
        Prerequisite pre_3 = new CoursePrerequisite("MA101");
        Prerequisite pre_1_2 = new AndPrerequisite(new LinkedList<>() {{add(pre_1); add(pre_2);}});
        Prerequisite pre_4 = new CoursePrerequisite("LL103");
        Prerequisite pre_5 = new CoursePrerequisite("LL104");
        Prerequisite pre_4_5 = new OrPrerequisite(List.of(pre_4, pre_5));
        Prerequisite pre_4_5_wp = new AndPrerequisite(List.of(pre_4_5, pre_1));

//        courseService.addCourse("CS102", "JavaProgram", 3, 64, HUNDRED_MARK_SCORE, null);
//        courseService.addCourse("CS202", "C++Program", 3, 64, HUNDRED_MARK_SCORE, pre_1);
//        courseService.addCourse("LL103", "Language", 3, 64, HUNDRED_MARK_SCORE, null);
//        courseService.addCourse("WP908", "Entertainment", 1, 32, PASS_OR_FAIL, null);
//        courseService.addCourse("ME101", "MachineBasic", 4, 64, HUNDRED_MARK_SCORE, null);
//        courseService.addCourse("MA101", "Calculus", 4, 64, HUNDRED_MARK_SCORE, null);
//        courseService.addCourse("MA203", "GaiTong", 3, 64, HUNDRED_MARK_SCORE, pre_3);
//        courseService.addCourse("CS602", "DSAA", 3, 64, HUNDRED_MARK_SCORE, pre_1_2);
//        courseService.addCourse("LL104", "Lang", 3, 64, PASS_OR_FAIL, null);
//        courseService.addCourse("LL304", "Uage", 3, 64, HUNDRED_MARK_SCORE, pre_4_5);
//        courseService.addCourse("WP117", "Ana", 3, 64, HUNDRED_MARK_SCORE, pre_4_5_wp);
        //courseService.addCourse("AB209", "HiAll", 3, 64, HUNDRED_MARK_SCORE, null);
        //courseService.addCourse("AB304", "WhatUp", 2, 64, HUNDRED_MARK_SCORE, null);
        //courseService.removeCourse("ME101");
        //courseService.removeCourse("ABC");
        //courseService.removeCourseSection(4);
        //courseService.removeCourseSectionClass(6);
        List<Course> dd = new ArrayList<>();
        //dd = courseService.getAllCourses();
        //List<User> uu = new ArrayList<>();
        //uu = userService.getAllUsers();
//        courseService.addCourseSection("CS202", 1, "Lecture", 80);
//        courseService.addCourseSection("CS202", 1, "Lab01", 40);
//        courseService.addCourseSection("CS202", 1, "Lab02", 40);
//        courseService.addCourseSection("WP908", 3, "Lecture", 100);
//        courseService.addCourseSection("WP908", 3, "Outdoor01", 50);
//        courseService.addCourseSection("WP908", 3, "Outdoor02", 50);
//        courseService.addCourseSection("MA203", 2, "Lecture", 80);
//        courseService.addCourseSection("LL304", 2, "Lecture", 65);
//        courseService.addCourseSection("ME101", 2, "Experiment", 45);
//        courseService.addCourseSection("WP117",1,"Lecture", 30);
//        courseService.addCourseSection("LL104",1,"Lecture", 37);


        List<Short> li = new LinkedList<>() {{add((short) 1); add((short) 2); add((short) 3); add((short) 4);}};
//        courseService.addCourseSectionClass(4, 11912301, DayOfWeek.THURSDAY, li, (short)10, (short)50, "LycheeHill");
//       courseService.addCourseSectionClass(3, 11507189, DayOfWeek.WEDNESDAY, li, (short)1, (short)51, "LycheeHill");
//        courseService.addCourseSectionClass(6, 11602131, DayOfWeek.MONDAY, li, (short)5, (short)55, "TeachingBuildingOne");
//        courseService.addCourseSectionClass(7, 12003467, DayOfWeek.MONDAY, li, (short)5, (short)55, "TeachingBuildingOne");

        HundredMarkGrade grade_1 = new HundredMarkGrade((short)96);
        HundredMarkGrade grade_2 = new HundredMarkGrade((short)84);
        HundredMarkGrade grade_3 = new HundredMarkGrade((short)92);
        //studentService.addEnrolledCourseWithGrade(20191208, 1, grade_1);
        //studentService.addEnrolledCourseWithGrade(20181023, 1, PASS);
        //studentService.addEnrolledCourseWithGrade(20210509, 5, PASS);
        //studentService.addEnrolledCourseWithGrade(20190621, 5, PASS);
        //studentService.addEnrolledCourseWithGrade(20221208, 1, PASS);
        //studentService.addEnrolledCourseWithGrade(20190621, 9, grade_2);
        //studentService.addEnrolledCourseWithGrade(20200319, 12, grade_2);
//        studentService.addEnrolledCourseWithGrade(20221208, 11, FAIL);
        //courseService.addCourseSection("CS602", 1, "Lecture", 120);
        //courseService.addCourseSection("CS102", 1, "Lecture", 120);
        //courseService.addCourseSection("LL304", 3, "Lect", 120);
        //courseService.addCourseSection("MA101", 2, "Lect", 117);
        //courseService.addCourseSection("WP117", 1, "Lect", 117);
        //courseService.addCourseSection("WP117", 1, "Lab", 62);
        //studentService.setEnrolledCourseGrade(20221208, 11, PASS);
//        studentService.addEnrolledCourseWithGrade(20181023, 8, grade_3);
//        studentService.addEnrolledCourseWithGrade(20221208, 7, grade_3);
//        //studentService.addEnrolledCourseWithGrade(20221208, 7, grade_3);
        //studentService.addEnrolledCourseWithGrade(20221208, 1, grade_2);
//        boolean y = studentService.passedPrerequisitesForCourse(20221208, "CS602");
//        //List<>
//        System.out.println(y);
        //studentService.dropCourse(20190621, 5);
//        Major m = new Major();
//        m = studentService.getStudentMajor(20190521);
//        System.out.println(m.toString());
//        G
        //HundredMarkGrade grade_4 = new HundredMarkGrade((short)67);
        //studentService.addEnrolledCourseWithGrade(20190621, 7, grade_4);
        //List<Department> departments=departmentService.getAllDepartments();
        //List<Major>majors=majorService.getAllMajors();
        //List<Course>courses=courseService.getAllCourses();
        //List<User>users=userService.getAllUsers();
        //System.out.println(departments);
        //System.out.println(majors);
        //System.out.println(courses);
        //System.out.println(users);
        //studentService.setEnrolledCourseGrade(20190621, 9, grade_1);
        //studentService.passedPrerequisitesForCourse();
        List<CourseSearchEntry>searchEntries=studentService.searchCourse(20221208,1,null,
                null,null,null,null,
                null, StudentService.CourseType.PUBLIC,false,false,
                true,true,1,0);
        System.out.println(searchEntries);
//        majorService.addMajorElectiveCourse(4, "CS202");
//        majorService.addMajorCompulsoryCourse(12, "LL103");
//        majorService.addMajorElectiveCourse(10, "WP908");
//        majorService.addMajorElectiveCourse(7, "ME101");
        //studentService.enrollCourse(20191208, 18);
        //studentService.enrollCourse(20221208, 7); //ALREADY_PASSED check√
        //studentService.enrollCourse(20191208, 18); //SUCCESS check√
        //studentService.enrollCourse(20221208, 30); //COURSE_NOT_FOUND check√
        //studentService.enrollCourse(20221208, 18); //ALREADY_ENROLLED check√
        HundredMarkGrade gra_0 = new HundredMarkGrade((short) 85);
        //studentService.enrollCourse(20221208,10); //PREREQUISITES_NOT_FULFILLED check√
        //boolean con = studentService.
        //studentService.enrollCourse(20190621, 5); //COURSE_IS_FULL check√ COURSE_CONFLICT_FOUND check√
        //studentService.dropCourse(20190621, 7);
        //studentService.addEnrolledCourseWithGrade(20221208, 9, gra_0);

    }

}

