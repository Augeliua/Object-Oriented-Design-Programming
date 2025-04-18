package sc2002.bto.interfaces;

import sc2002.bto.entity.Project;

public interface IProjectManagement {
    void reviewProject(Project p);
    void approveProject(Project p);
}
