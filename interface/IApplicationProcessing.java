public interface IApplicationProcessing {
    void processApplication(Application a);
    void validateApplication(Application a);
    void updateApplicationStatus();
}