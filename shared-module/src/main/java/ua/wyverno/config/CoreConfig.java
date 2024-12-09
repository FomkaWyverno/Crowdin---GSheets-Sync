package ua.wyverno.config;

public class CoreConfig implements Config {
    private String crowdinToken;
    private String spreadsheetID;
    private String languageId;
    private long projectID;

    protected CoreConfig() {
    }

    public String getCrowdinToken() {
        return crowdinToken;
    }

    protected void setCrowdinToken(String crowdinToken) {
        if (crowdinToken.isEmpty()) throw new IllegalArgumentException("Crowdin Token cannot be empty!!!");
        this.crowdinToken = crowdinToken;
    }

    public String getSpreadsheetID() {
        return spreadsheetID;
    }

    public void setSpreadsheetID(String spreadsheetID) {
        if (spreadsheetID == null) throw new IllegalArgumentException("spreadsheetID cannot be null!!!");
        if (spreadsheetID.isEmpty()) throw new IllegalArgumentException("spreadsheetID cannot be empty!!!");

        this.spreadsheetID = spreadsheetID;
    }

    public long getProjectID() {
        return projectID;
    }

    public String getLanguageId() {
        return languageId;
    }

    protected CoreConfig setLanguageId(String languageId) {
        this.languageId = languageId;
        return this;
    }

    public CoreConfig setProjectID(long projectID) {
        this.projectID = projectID;
        return this;
    }

    protected void setProjectID(String projectID) {
        if (projectID.isEmpty()) throw new IllegalArgumentException("ProjectID field cannot be empty!!!");
        if (!projectID.matches("\\d+")) throw new IllegalArgumentException("ProjectID must be numeric type!!!");
        this.projectID = Long.parseLong(projectID);
    }
}
