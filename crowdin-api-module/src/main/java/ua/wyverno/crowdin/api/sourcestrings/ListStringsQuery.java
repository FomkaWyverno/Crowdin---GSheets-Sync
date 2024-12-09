package ua.wyverno.crowdin.api.sourcestrings;

import com.crowdin.client.sourcestrings.SourceStringsApi;
import ua.wyverno.crowdin.api.ListQuery;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class ListStringsQuery<T, Q extends ListStringsQuery<T,Q>> extends ListQuery<T, Q> {
    private final SourceStringsApi sourceStringsApi;
    private final long projectID;
    private String orderBy;
    private Integer denormalizePlaceholders;
    private List<Integer> labelsIDs;
    private Long fileID;
    private Long directoryID;
    private String croQL;
    private String filterAPI;
    private String scope;

    public ListStringsQuery(SourceStringsApi sourceStringsApi ,long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
    }

    /**
     * Default: "id"<br/>
     * Enum: "id" "text" "identifier" "context" "createdAt" "updatedAt" "type"<br/>
     * Example: orderBy=createdAt desc,text,type<br/>
     * Read more about <a href="https://support.crowdin.com/developer/api/v2/#section/Introduction/Sorting">sorting rules</a>
     * @param orderBy рядок як саме має бути впорядкований список
     * @return {@link Q}
     */
    public Q orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this.self();
    }

    /**
     * Default: 0<br/>
     * Enum: 0 1<br/>
     * Enable denormalize placeholders<br/>
     * @param denormalizePlaceholders true convert to 1, false to 0
     * @return {@link Q}
     */
    public Q denormalizePlaceholders(Boolean denormalizePlaceholders) {
        if (denormalizePlaceholders != null) {
            if (denormalizePlaceholders) {
                this.denormalizePlaceholders = 1;
            } else {
                this.denormalizePlaceholders = 0;
            }
        }
        return this.self();
    }

    /**
     * Example: labelIds=1,2,3,4,5<br/>
     * Filter strings by labelIds. Label Identifiers. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels</a>
     * @param labelsIDs
     * @return {@link Q}
     */
    public Q labelsIDs(List<Integer> labelsIDs) {
        this.labelsIDs = labelsIDs;
        return this.self();
    }

    /**
     * File Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.files.getMany">List Files<a/><br/>
     *<br/>
     * Note: Can't be used with taskId, directoryId or branchId in same request
     * @param fileID
     * @return {@link Q}
     */
    public Q fileID(Long fileID) {
        this.fileID = fileID;
        return this.self();
    }

    /**
     * Directory Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.directories.getMany">List Directories</a><br/>
     * <br/>
     * Note: Can't be used with taskId, fileId or branchId in same request
     * @param directoryID
     * @return {@link Q}
     */
    public Q directoryID(Long directoryID) {
        this.directoryID = directoryID;
        return this.self();
    }

    /**
     * Filter strings by <a href="https://developer.crowdin.com/croql/">CroQL<a/><br/>
     * <br/>
     * Note: Can be used only with denormalizePlaceholders, orderBy, offset and limit in same request
     * @param croQL
     * @return {@link Q}
     */
    public Q croQL(String croQL) {
        this.croQL = URLEncoder.encode(croQL, StandardCharsets.UTF_8);
        return this.self();
    }

    /**
     * Filter strings by identifier, text or context
     * @param filterAPI
     * @return {@link Q}
     */
    public Q filterAPI(String filterAPI) {
        this.filterAPI = URLEncoder.encode(filterAPI, StandardCharsets.UTF_8);
        return this.self();
    }

    /**
     * Enum: "identifier" "text" "context"<br/>
     * Specify field to be the target of filtering. It can be one scope or a list of comma-separated scopes
     * @param scope
     * @return
     */
    public Q scope(String scope) {
        this.scope = scope;
        return this.self();
    }

    protected SourceStringsApi getSourceStringsApi() {
        return sourceStringsApi;
    }

    protected long getProjectID() {
        return projectID;
    }

    protected String getOrderBy() {
        return orderBy;
    }

    protected Integer getDenormalizePlaceholders() {
        return denormalizePlaceholders;
    }

    protected String getLabelsIDs() {
        if (this.labelsIDs == null) return null;
        StringBuilder labelsBuilder = new StringBuilder();
        this.labelsIDs.forEach(labelID -> labelsBuilder.append(labelID).append(","));
        labelsBuilder.deleteCharAt(labelsBuilder.length()-1);
        return labelsBuilder.toString();
    }

    protected Long getFileID() {
        return fileID;
    }

    protected Long getDirectoryID() {
        return directoryID;
    }

    protected String getCroQL() {
        return croQL;
    }

    protected String getFilterAPI() {
        return filterAPI;
    }

    protected String getScope() {
        return scope;
    }
}
