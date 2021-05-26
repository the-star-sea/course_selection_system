import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
public class Main {
    public static void main(String[] args) {
        ServiceFactory serviceFactory= Config.getServiceFactory();
        DepartmentService departmentService=serviceFactory.createService(DepartmentService.class);
        InstructorService instructorService=serviceFactory.createService(InstructorService.class);
       // CourseService courseService=serviceFactory.createService(CourseService.class);

        //departmentService.removeDepartment(0);
        //departmentService.removeDepartment(1);

        //departmentService.addDepartment("CS");
        //departmentService.addDepartment("EE");

        instructorService.addInstructor(1,"zhang","tong");
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);
    }
}
