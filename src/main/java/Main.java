import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.factory.*;
import cn.edu.sustech.cs307.service.*;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        ServiceFactory serviceFactory= Config.getServiceFactory();
        UserService userService=serviceFactory.createService(UserService.class);
        CourseService courseService=serviceFactory.createService(CourseService.class);
        User user=userService.getUser(1);
        System.out.println(user.fullName);
        //StudentService.EnrollResult result=studentService.enrollCourse(3,2);
        //System.out.println(result);
    }
}
