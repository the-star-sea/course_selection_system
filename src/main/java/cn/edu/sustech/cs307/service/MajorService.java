package cn.edu.sustech.cs307.service;

import cn.edu.sustech.cs307.dto.Major;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
import java.util.List;

@ParametersAreNonnullByDefault
public interface MajorService {
    int addMajor(String name, int departmentId) throws SQLException;

    /**
     * To remove an entity from the system, related entities dependent on this entity
     * (usually rows referencing the row to remove through foreign keys in a relational database) shall be removed together.
     *
     * More specifically, when remove a major, the related students should be removed accordingly
     * @param majorId
     */
    void removeMajor(int majorId) throws SQLException;

    List<Major> getAllMajors() throws SQLException;

    /**
     * If there is no Major about specific id, throw EntityNotFoundException.
     * @param majorId
     * @return
     */
    Major getMajor(int majorId) throws SQLException;

    /**
     * Binding a course id {@code courseId} to major id {@code majorId}, and the selection is compulsory.
     * @param majorId the id of major
     * @param courseId the course id
     */
    void addMajorCompulsoryCourse(int majorId, String courseId);

    /**
     * Binding a course id{@code courseId} to major id {@code majorId}, and the selection is elective.
     * @param majorId the id of major
     * @param courseId the course id
     */
    void addMajorElectiveCourse(int majorId, String courseId);
}
