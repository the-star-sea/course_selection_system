package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.service.*;

import java.util.List;

public class myinstructor implements InstructorService{
    @Override
    public void addInstructor(int userId, String firstName, String lastName) {

    }

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) {
        return null;
    }
}
