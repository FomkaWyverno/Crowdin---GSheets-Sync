package ua.wyverno.config;

public class Config {
    private String token;
    private String spreadsheetID;
    private long projectID;
    protected Config() {}

    protected void setToken(String token) {
        if (token.isEmpty()) throw new IllegalArgumentException("Token cannot be empty!!!");
        this.token = token;
    }

    public void setSpreadsheetID(String spreadsheetID) {
        if (spreadsheetID == null) throw new IllegalArgumentException("spreadsheetID cannot be null!!!");
        if (spreadsheetID.isEmpty()) throw new IllegalArgumentException("spreadsheetID cannot be empty!!!");

        this.spreadsheetID = spreadsheetID;
    }

    protected void setProjectID(String projectID) {
        if (projectID.isEmpty()) throw new IllegalArgumentException("ProjectID field cannot be empty!!!");
        if (!projectID.matches("\\d+")) throw new IllegalArgumentException("ProjectID must be numeric type!!!");
        this.projectID = Long.parseLong(projectID);
    }
    public String getToken() {
        return token;
    }

    public String getSpreadsheetID() {
        return spreadsheetID;
    }

    public long getProjectID() {
        return projectID;
    }
}
