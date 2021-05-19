package cn.edu.sustech.cs307.factory;
import cn.edu.sustech.cs307.service.*;
import cn.edu.sustech.cs307.serviceinstance.*;
public class myfactory extends ServiceFactory{
    public myfactory() {
        registerService(StudentService.class, new mystudent());
        registerService(CourseService.class, new mycourse());
        registerService(DepartmentService.class,new mydepartment());
        registerService(InstructorService.class,new myinstructor());
        registerService(MajorService.class,new mymajor());
        registerService(SemesterService.class,new mysemester());
        registerService(UserService.class,new myuser());
        // registerService(<interface name>.class, new <your implementation>());
    }
}
