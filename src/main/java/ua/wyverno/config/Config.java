package ua.wyverno.config;

public class Config {
    private String token;
    private long projectID;
    protected Config() {}

    protected void setToken(String token) {
        this.token = token;
    }
    protected void setProjectID(String projectID) {
        this.projectID = Long.parseLong(projectID);
    }
    public String getToken() {
        return token;
    }

    public long getProjectID() {
        return projectID;
    }
}
