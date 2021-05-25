import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
public class Main {
    public static void main(String[] args) {
        ServiceFactory serviceFactory= Config.getServiceFactory();
        DepartmentService departmentService=serviceFactory.createService(DepartmentService.class);
       // CourseService courseService=serviceFactory.createService(CourseService.class);
        departmentService.addDepartment("sb");
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);
    }
}
