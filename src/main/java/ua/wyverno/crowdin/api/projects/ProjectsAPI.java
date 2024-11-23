package ua.wyverno.crowdin.api.projects;

import ua.wyverno.crowdin.api.projects.queries.ProjectsGetQuery;

public interface ProjectsAPI {
    ProjectsGetQuery get(long projectID);
}
