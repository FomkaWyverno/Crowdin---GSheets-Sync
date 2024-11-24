package ua.wyverno.crowdin.api.projects.queries;

import com.crowdin.client.projectsgroups.ProjectsGroupsApi;
import com.crowdin.client.projectsgroups.model.Project;
import ua.wyverno.crowdin.api.Query;

public class ProjectsGetQuery implements Query<Project> {
    private final ProjectsGroupsApi projectsGroupsApi;
    private final long projectID;
    public ProjectsGetQuery(ProjectsGroupsApi projectsGroupsApi, long projectID) {
        this.projectsGroupsApi = projectsGroupsApi;
        this.projectID = projectID;
    }

    @Override
    public Project execute() {
        return this.projectsGroupsApi.getProject(this.projectID).getData();
    }
}
