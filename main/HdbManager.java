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
    public void reviewProject(Project p) {
        System.out.println("Reviewing project: " + p.getProjectID());
        // Add logic to review project details
    }

    @Override
    public void approveProject(Project p) {
        System.out.println("Approving project: " + p.getProjectID());
        p.setApprovalStatus(true);
    }

    // Method to create a new project
    public void createProject(Project p) {
        projectCreate.add(p);
        System.out.println("Project created: " + p.getProjectID());
    }

    // Method to delete a project
    public void deleteProject(Project p) {
        projectCreate.remove(p);
        System.out.println("Project deleted: " + p.getProjectID());
    }

    // Generate a report based on ReportType
    public Report generateReport(ReportType type) {
        System.out.println("Generating report of type: " + type);
        return new Report(type);
    }
}
