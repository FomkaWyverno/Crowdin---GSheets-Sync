package ua.wyverno.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class SyncConfig implements Config {
    private final File file;
    private final Properties properties = new Properties();
    private String crowdinDirectoryRoot = "";
    private String crowdinDirectoryRootTitle = "";

    public SyncConfig(File file) {
        this.file = file;
    }

    protected void setCrowdinDirectoryRoot(String crowdinDirectoryRoot) {
        this.crowdinDirectoryRoot = crowdinDirectoryRoot;
        this.properties.put("crowdinDirectoryRoot", this.crowdinDirectoryRoot);
    }

    public void setCrowdinDirectoryRootTitle(String crowdinDirectoryRootTitle) {
        this.crowdinDirectoryRootTitle = crowdinDirectoryRootTitle;
        this.properties.put("crowdinDirectoryRootTitle", this.crowdinDirectoryRootTitle);
    }

    public void updateCrowdinDirectoryRoot(String crowdinDirectoryRoot) throws IOException {
        this.setCrowdinDirectoryRoot(crowdinDirectoryRoot);
        this.updateFile();
    }

    public void updateCrowdinDirectoryRootTitle(String crowdinDirectoryRootTitle) throws IOException {
        this.setCrowdinDirectoryRootTitle(crowdinDirectoryRootTitle);
        this.updateFile();
    }

    private void updateFile() throws IOException {
        this.properties.store(Files.newBufferedWriter(this.file.toPath()),null);
    }

    public String getCrowdinDirectoryRoot() {
        return crowdinDirectoryRoot;
    }

    public String getCrowdinDirectoryRootTitle() {
        return crowdinDirectoryRootTitle;
    }
}
