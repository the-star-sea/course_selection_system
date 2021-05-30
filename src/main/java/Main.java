import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
import cn.edu.sustech.cs307.serviceinstance.mymajor;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
//        List<Semester> semesters=semesterService.getAllSemesters();
//        System.out.println(semesters);
        //userService.removeUser(11911607);
//        User user= userService.getUser(20190621);
//        System.out.println(user);
        //semesterService.addSemester("fall",java.sql.Date.valueOf("2005-12-19"),java.sql.Date.valueOf("2005-12-16"));
        //instructorService.addInstructor(3123123,"tong","zhang");
//        List<User> users=new ArrayList<>();
//        users=userService.getAllUsers();
//        System.out.println(users);
//        Major major=studentService.getStudentMajor(20221208);
//        System.out.println(major);
//        List<Department> departments=departmentService.getAllDepartments();
//        System.out.println(departments);
//        Department department=departmentService.getDepartment(1);
//        System.out.println(department);


        //User user=userService.getUser(1);
        //System.out.println(user.fullName);
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);

    }
}
