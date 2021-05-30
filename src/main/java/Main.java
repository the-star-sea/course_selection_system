import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.prerequisite.CoursePrerequisite;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
import cn.edu.sustech.cs307.serviceinstance.mymajor;

import java.sql.Date;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static cn.edu.sustech.cs307.dto.Course.CourseGrading.HUNDRED_MARK_SCORE;
import static cn.edu.sustech.cs307.dto.Course.CourseGrading.PASS_OR_FAIL;
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

//        majorService.addMajor("CS", 5);//1
//        majorService.addMajor("IS", 5);//2
//        majorService.addMajor("RE", 6);//3
//        majorService.addMajor("ME", 6);//4
//        majorService.addMajor("AP", 7);//5
//        majorService.addMajor("AL", 7);//6
//        majorService.addMajor("WP", 8);//7
//        majorService.addMajor("EL", 9);//8
//        majorService.addMajor("SL", 9);//9
//
//        semesterService.addSemester("2021Spring", java.sql.Date.valueOf("2021-01-18"), java.sql.Date.valueOf("2021-06-13"));
//        semesterService.addSemester("2020Fall", java.sql.Date.valueOf("2020-9-6"), java.sql.Date.valueOf("2021-1-16"));
//        semesterService.addSemester("2020Spring", java.sql.Date.valueOf("2020-2-9"), java.sql.Date.valueOf("2021-5-31"));

//        studentService.addStudent(20191208, 4, "LONG", "yaya", java.sql.Date.valueOf("2019-08-15"));
//        studentService.addStudent(20190621, 3, "SAI", "xuxu", java.sql.Date.valueOf("2019-08-15"));
//        studentService.addStudent(20181023, 5, "LIN", "kai", java.sql.Date.valueOf("2018-08-15"));
//        studentService.addStudent(20200319, 5, "WANG", "see", java.sql.Date.valueOf("2020-08-15"));
//        studentService.addStudent(20210509, 8, "AI", "nin", java.sql.Date.valueOf("2021-08-15"));
//        studentService.addStudent(20221208, 7, "HAO", "de", java.sql.Date.valueOf("2022-08-15"));
//
//        instructorService.addInstructor(11912301, "hi", "Wor");
//        instructorService.addInstructor(11203453, "yihai", "WU");
//        instructorService.addInstructor(12003467, "yun", "CHEN");
//        instructorService.addInstructor(11507189, "yes", "AH");
//        instructorService.addInstructor(11602131, "wes", "OP");

        Prerequisite pre_1 = new CoursePrerequisite("CS102");
        //courseService.addCourse("CS102", "JavaProgram", 3, 64, HUNDRED_MARK_SCORE, null);
        //courseService.addCourse("CS202", "C++Program", 3, 64, HUNDRED_MARK_SCORE, pre_1);
        //courseService.addCourse("LL103", "Language", 3, 64, HUNDRED_MARK_SCORE, null);
        //courseService.addCourse("WP908", "Entertainment", 1, 32, PASS_OR_FAIL, null);
        //courseService.addCourse("ME101", "MachineBasic", 4, 64, HUNDRED_MARK_SCORE, null);

        //courseService.addCourseSection("CS202", 1, "Lecture", 80);
        //courseService.addCourseSection("CS202", 1, "Lab01", 40);
        //courseService.addCourseSection("CS202", 1, "Lab02", 40);
        //courseService.addCourseSection("WP908", 3, "Lecture", 100);
        //courseService.addCourseSection("WP908", 3, "Outdoor01", 50);
        //courseService.addCourseSection("WP908", 3, "Outdoor02", 50);

        List<Short> li = new LinkedList<>() {{add((short) 1); add((short) 2); add((short) 3); add((short) 4);}};
        //courseService.addCourseSectionClass(4, 11912301, DayOfWeek.THURSDAY, li, (short)10, (short)50, "LycheeHill");
       //courseService.addCourseSectionClass(3, 11507189, DayOfWeek.WEDNESDAY, li, (short)1, (short)51, "LycheeHill");
        //courseService.addCourseSectionClass(6, 11602131, DayOfWeek.MONDAY, li, (short)5, (short)55, "TeachingBuildingOne");
        //courseService.addCourseSectionClass(7, 12003467, DayOfWeek.MONDAY, li, (short)5, (short)55, "TeachingBuildingOne");

        HundredMarkGrade grade_1 = new HundredMarkGrade((short)96);
        //studentService.addEnrolledCourseWithGrade(20191208, 1, grade_1);
        //studentService.addEnrolledCourseWithGrade(20181023, 1, PASS);
        //studentService.addEnrolledCourseWithGrade(20210509, 5, PASS);
        //studentService.addEnrolledCourseWithGrade(20190621, 5, PASS);
        //studentService.addEnrolledCourseWithGrade(20221208, 1, PASS);

        //majorService.addMajorElectiveCourse(3, "CS202");
        //majorService.addMajorCompulsoryCourse(3, "CS102");
        //majorService.addMajorElectiveCourse(7, "WP908");


    }

}

