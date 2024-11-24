package ua.wyverno.crowdin.api.projects;

import com.crowdin.client.projectsgroups.ProjectsGroupsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.projects.queries.ProjectsGetQuery;

@Component
public class ProjectsApiImpl implements ProjectsAPI {
    private final ProjectsGroupsApi projectsGroupsApi;

    @Autowired
    public ProjectsApiImpl(CrowdinApiClient crowdinApiClient) {
        this.projectsGroupsApi = crowdinApiClient.getCrowdinClient().getProjectsGroupsApi();
    }

    @Override
    public ProjectsGetQuery get(long projectID) {
        return new ProjectsGetQuery(this.projectsGroupsApi, projectID);
    }
}
