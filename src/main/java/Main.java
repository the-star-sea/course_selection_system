import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
import cn.edu.sustech.cs307.serviceinstance.mymajor;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.sustech.cs307.dto.Course.CourseGrading.HUNDRED_MARK_SCORE;

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

        departmentService.addDepartment("CSE");//1
        departmentService.addDepartment("MEE");//2
        departmentService.addDepartment("ALE");//3
        departmentService.addDepartment("WPE");//4
        departmentService.addDepartment("LLE");//5

        majorService.addMajor("CS", 1);//1
        majorService.addMajor("IS", 1);//2
        majorService.addMajor("RE", 2);//3
        majorService.addMajor("ME", 2);//4
        majorService.addMajor("AP", 3);//5
        majorService.addMajor("AL", 3);//6
        majorService.addMajor("WP", 4);//7
        majorService.addMajor("EL", 5);//8
        majorService.addMajor("SL", 5);//9

        semesterService.addSemester("2021Spring", java.sql.Date.valueOf("2021-01-18"), java.sql.Date.valueOf("2021-06-13"));
        semesterService.addSemester("2020Fall", java.sql.Date.valueOf("2020-9-6"), java.sql.Date.valueOf("2021-1-16"));
        semesterService.addSemester("2020Spring", java.sql.Date.valueOf("2020-2-9"), java.sql.Date.valueOf("2021-5-31"));

        studentService.addStudent(20191208, 2, "LONG", "yaya", java.sql.Date.valueOf("2019-08-15"));
        studentService.addStudent(20190621, 1, "SAI", "xuxu", java.sql.Date.valueOf("2019-08-15"));
        studentService.addStudent(20181023, 5, "LIN", "kai", java.sql.Date.valueOf("2018-08-15"));
        studentService.addStudent(20200319, 5, "WANG", "see", java.sql.Date.valueOf("2020-08-15"));
        studentService.addStudent(20210509, 8, "AI", "nin", java.sql.Date.valueOf("2021-08-15"));
        studentService.addStudent(20221208, 7, "HAO", "de", java.sql.Date.valueOf("2022-08-15"));

        //studentService.setEnrolledCourseGrade();
    }

}

