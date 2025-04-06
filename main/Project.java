public class Project {
    private String projectId;
    private String description;
    private double budget;

    public Project(String projectId, String description, double budget) {
        this.projectId = projectId;
        this.description = description;
        this.budget = budget;
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}