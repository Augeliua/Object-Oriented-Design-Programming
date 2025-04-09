import java.util.List;
import java.util.ArrayList;

public class HDBManager implements IProjectManagement {
    private String managerName;
    private List<Project> projectCreate;

    public HDBManager(String managerName) {
        this.managerName = managerName;
        this.projectCreate = new ArrayList<>();
    }

    // Implementing IProjectManagement methods
    @Override
    public void reviewProject(Project project) {
        System.out.println("Reviewing project: " + project.getProjectID());
        // Add logic to review project details
    }

    @Override
    public void approveProject(Project project) {
        System.out.println("Approving project: " + project.getProjectID());
        project.setApprovalStatus(true);
    }

    // Method to create a new project
    public void createProject(Project project) {
        projectCreate.add(project);
        System.out.println("Project created: " + project.getProjectID());
    }

    // Method to delete a project
    public void deleteProject(Project project) {
        projectCreate.remove(project);
        System.out.println("Project deleted: " + project.getProjectID());
    }

    // Generate a report based on ReportType
    public Report generateReport(ReportType type) {
        System.out.println("Generating report of type: " + type);
        return new Report(type);
    }
}
