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


        //semesterService.addSemester("fall",java.sql.Date.valueOf("2005-12-19"),java.sql.Date.valueOf("2005-12-16"));
        //instructorService.addInstructor(3123123,"tong","zhang");
        //List<User> users=new ArrayList<>();
        //users=userService.getAllUsers();
        //List<Department> departments=departmentService.getAllDepartments();
       //departmentService.addDepartment("sb3");
       //departmentService.addDepartment("t3");
        //majorService.addMajor("sd",1);
        //majorService.addMajor("gan",2);
        //semesterService.addSemester("Fall1", java.sql.Date.valueOf("2005-12-17"), java.sql.Date.valueOf("2005-12-02"));
        //courseService.addCourseSection("CS333",1,"Fall1",50);
        //majorService.addMajorCompulsoryCourse(2,"CS333");
        //studentService.addStudent(117,1,"ssd","dd",java.sql.Date.valueOf("2005-12-12"));
        //System.out.println( departmentService.addDepartment("jj"));
       // MajorService majorService=serviceFactory.createService(MajorService.class);
        //System.out.println( majorService.addMajor("jj",3));
        //User user=userService.getUser(1);
        //System.out.println(user.fullName);
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);
    }
}
