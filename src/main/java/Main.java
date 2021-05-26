import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
import cn.edu.sustech.cs307.serviceinstance.mymajor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        ServiceFactory serviceFactory= Config.getServiceFactory();
        UserService userService=serviceFactory.createService(UserService.class);
        InstructorService instructorService=serviceFactory.createService(InstructorService.class);
        CourseService courseService=serviceFactory.createService(CourseService.class);
        DepartmentService departmentService=serviceFactory.createService(DepartmentService.class);
        instructorService.addInstructor(1,"sb","ssbb");
        //List<Department> departments=departmentService.getAllDepartments();
        //System.out.println( departmentService.addDepartment("jj"));
       // MajorService majorService=serviceFactory.createService(MajorService.class);
        //System.out.println( majorService.addMajor("jj",3));
        //User user=userService.getUser(1);
        //System.out.println(user.fullName);
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);
    }
}
