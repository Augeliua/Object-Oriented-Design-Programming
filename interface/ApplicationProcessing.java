public interface ApplicationProcessing {
    void processApplication(String applicationDetails);
    void validateApplication(String applicationDetails);
    void updateApplicationStatus(String status);
}