package sc2002.bto.interfaces;

import sc2002.bto.entity.Project;
/**
 * Defines operations for managing BTO projects.
 * This interface is implemented by classes responsible for reviewing and approving projects.
 * 
 */
public interface IProjectManagement {
    /**
     * Reviews the details of a project.
     * 
     * @param p The project to review
     */
    void reviewProject(Project p);
    /**
     * Approves a project, making it officially available.
     * 
     * @param p The project to approve
     */
    void approveProject(Project p);
}
