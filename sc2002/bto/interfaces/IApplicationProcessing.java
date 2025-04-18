package sc2002.bto.interfaces;

import sc2002.bto.entity.Application;

public interface IApplicationProcessing {
    void processApplication(Application a);
    void validateApplication(Application a);
    void updateApplicationStatus();
}
