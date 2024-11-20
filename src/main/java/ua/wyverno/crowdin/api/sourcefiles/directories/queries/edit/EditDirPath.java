package ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit;

public enum EditDirPath {
    BRANCH_ID("/branchID"), DIRECTORY_ID("/directoryId"),
    NAME("/name"), TITLE("/title"), EXPORT_PATTERN("/exportPattern"),
    PRIORITY("/priority");
    private final String value;
    EditDirPath(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
