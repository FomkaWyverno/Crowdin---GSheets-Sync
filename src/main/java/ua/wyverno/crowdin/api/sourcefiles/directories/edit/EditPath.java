package ua.wyverno.crowdin.api.sourcefiles.directories.edit;

public enum EditPath {
    BRANCH_ID("/branchID"), DIRECTORY_ID("/directoryId"),
    NAME("/name"), TITLE("/title"), EXPORT_PATTERN("/exportPattern"),
    PRIORITY("/priority");
    private final String value;
    EditPath(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
