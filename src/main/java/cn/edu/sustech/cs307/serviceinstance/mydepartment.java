package cn.edu.sustech.cs307.serviceinstance;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.service.*;

import java.util.List;

public class mydepartment implements  DepartmentService {
    @Override
    public int addDepartment(String name) {
        return 0;
    }

    @Override
    public void removeDepartment(int departmentId) {

    }

    @Override
    public List<Department> getAllDepartments() {
        return null;
    }

    @Override
    public Department getDepartment(int departmentId) {
        return null;
    }
}
