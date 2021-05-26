import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;
public class Main {
    public static void main(String[] args) {
        ServiceFactory serviceFactory= Config.getServiceFactory();
        UserService userService=serviceFactory.createService(UserService.class);
        CourseService courseService=serviceFactory.createService(CourseService.class);
        userService.getUser(1);
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);
    }
}
