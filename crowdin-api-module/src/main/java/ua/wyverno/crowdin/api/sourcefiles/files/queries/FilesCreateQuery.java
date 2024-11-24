package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.AddFileRequest;
import com.crowdin.client.sourcefiles.model.ExportOptions;
import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcefiles.model.ImportOptions;
import ua.wyverno.crowdin.api.Query;

import java.util.List;

public class FilesCreateQuery implements Query<FileInfo> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private long storageID;
    private String name;
    private Long directoryId;
    private String title;
    private String context;
    private String type;
    private Integer parserVersion;
    private ImportOptions importOptions;
    private ExportOptions exportOptions;
    private List<String> excludedTargetLanguages;
    private List<Long> attachLabelIds;
    public FilesCreateQuery(SourceFilesApi sourceFilesApi, long projectID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
    }

    /**
     * @param storageID айді створеного сховища (тимчасового файла) на Crowdin
     * @return {@link FilesCreateQuery}
     */
    public FilesCreateQuery storageID(long storageID) {
        this.storageID = storageID;
        return this;
    }

    /**
     * @param name назва файлу
     * @return {@link FilesCreateQuery}
     */
    public FilesCreateQuery name(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param directoryId Directory Identifier — defines directory to which file will be added. Get via List Directories<br/>
     *<br/>
     * <span style="font-weight:bold">Note:</span> Can't be used with branchId in same request
     */
    public FilesCreateQuery directoryId(Long directoryId) {
        this.directoryId = directoryId;
        return this;
    }

    /**
     * @param title Use to provide more details for translators. Title is available in UI only
     */
    public FilesCreateQuery title(String title) {
        this.title = title;
        return this;
    }

    /**
     * @param context Use to provide context about whole file
     */
    public FilesCreateQuery context(String context) {
        this.context = context;
        return this;
    }

    /**
     * @param type Default: "auto"
     * Enum: "auto" "android" "macosx" "resx" "properties" "gettext" "yaml" "php" "json" "xml" "ini" "rc" "resw" "resjson" "qtts" "joomla" "chrome" "dtd" "dklang" "flex" "nsh" "wxl" "xliff" "xliff_two" "html" "haml" "txt" "csv" "md" "mdx_v1" "mdx_v2" "flsnp" "fm_html" "fm_md" "mediawiki" "docx" "xlsx" "sbv" "properties_play" "properties_xml" "maxthon" "go_json" "dita" "idml" "mif" "stringsdict" "plist" "vtt" "vdf" "srt" "stf" "toml" "contentful_rt" "svg" "js" "coffee" "ts" "i18next_json" "xaml" "arb" "adoc" "fbt" "webxml" "nestjs_i18n"<br/>
     * Values available:<br/>
     *<br/>
     * empty value or 'auto' — Try to detect file type by extension or MIME type<br/>
     * 'android' — Android (*.xml)<br/>
     * 'macosx' — Mac OS X / iOS (*.strings)<br/>
     * 'resx' — .NET, Windows Phone (*.resx)<br/>
     * 'properties' — Java (*.properties)<br/>
     * 'gettext' — GNU GetText (*.po, *.pot)<br/>
     * 'yaml' — Ruby On Rails (*.yaml, *.yml)<br/>
     * 'php' — Hypertext Preprocessor (*.php)<br/>
     * 'json' — Generic JSON (*.json)<br/>
     * 'xml' — Generic XML (*.xml)<br/>
     * 'ini' — Generic INI (*.ini)<br/>
     * 'rc' — Windows Resources (*.rc)<br/>
     * 'resw' — Windows 8 Metro (*.resw)<br/>
     * 'resjson' — Windows 8 Metro (*.resjson)<br/>
     * 'qtts' — Nokia Qt (*.ts)<br/>
     * 'joomla' — Joomla localizable resources (*.ini)<br/>
     * 'chrome' — Google Chrome Extension (*.json)<br/>
     * 'dtd' — Mozilla DTD (*.dtd)<br/>
     * 'dklang' — Delphi DKLang (*.dklang)<br/>
     * 'flex' — Flex (*.properties)<br/>
     * 'nsh' — NSIS Installer Resources (*.nsh)<br/>
     * 'wxl' — WiX Installer (*.wxl)<br/>
     * 'xliff' — XLIFF (*.xliff, *.xlf)<br/>
     * 'xliff_two' — XLIFF 2.0 (*.xliff, *.xlf)<br/>
     * 'html' — HTML (*.html, *.htm, *.xhtml, *.xhtm, *.xht, *.hbs, *.liquid)<br/>
     * 'haml' — Haml (*.haml)<br/>
     * 'txt' — Plain Text (*.txt)<br/>
     * 'csv' — Comma Separated Values (*.csv)<br/>
     * 'md' — Markdown (*.md, *.text, *.markdown...)<br/>
     * 'flsnp' — MadCap Flare (*.flnsp, .flpgpl .fltoc)<br/>
     * 'fm_html' — Jekyll HTML (*.html)<br/>
     * 'fm_md' — Jekyll Markdown (*.md)<br/>
     * 'mediawiki' — MediaWiki (*.wiki, *.wikitext, *.mediawiki)<br/>
     * 'docx' — Microsoft Office, OpenOffice.org Documents, Adobe InDesign, Adobe FrameMaker(*.docx, *.dotx, *.docm, *.dotm, *.xlsx, *.xltx, *.xlsm, *.xltm, *.pptx, *.potx, *.ppsx, *.pptm, *.potm, *.ppsm, *.odt, *.ods, *.odg, *.odp, *.imdl, *.mif)<br/>
     * 'xlsx' — Microsoft Excel (*.xlsx)<br/>
     * 'sbv' — Youtube .sbv (*.sbv)<br/>
     * 'properties_play' — Play Framework<br/>
     * 'properties_xml' — Java Application (*.xml)<br/>
     * 'maxthon' — Maxthon Browser (*.ini)<br/>
     * 'go_json' — Go (*.gotext.json)<br/>
     * 'dita' — DITA Document (*.dita, *.ditamap)<br/>
     * 'mif' — Adobe FrameMaker (*.mif)<br/>
     * 'idml' — Adobe InDesign (*.idml)<br/>
     * 'stringsdict' — iOS (*.stringsdict)<br/>
     * 'plist' — Mac OS property list (*.plist)<br/>
     * 'vtt' — Video Subtitling and WebVTT (*.vtt)<br/>
     * 'vdf' — Steamworks Localization Valve Data File (*.vdf)<br/>
     * 'srt' — SubRip .srt (*.srt)<br/>
     * 'stf' — Salesforce (*.stf)<br/>
     * 'toml' — Toml (*.toml)<br/>
     * 'contentful_rt' — Contentful (*.json)<br/>
     * 'svg' — SVG (*.svg)<br/>
     * 'js' — JavaScript (*.js)<br/>
     * 'coffee' — CoffeeScript (*.coffee)<br/>
     * 'nestjs_i18n' - NestJS i18n<br/>
     * Note: Use docx type to import each cell as a separate source string for XLSX file<br/>
     */
    public FilesCreateQuery type(String type) {
        this.type = type;
        return this;
    }

    /**
     * @param parserVersion Using latest parser version by default.<br/>
     * <span style="font-weight:bold;">Note:</span> Must be used together with type
     */
    public FilesCreateQuery parserVersion(Integer parserVersion) {
        this.parserVersion = parserVersion;
        return this;
    }

    /**
     * @param importOptions
     * Spreadsheet File Import Options (object) or Xml File Import Options (object) or WebXml File Import Options (object) or Docx File Import Options (object) or Html File Import Options (object) or Html with Front Matter File Import Options (object) or Mdx v1 File Import Options (object) or Mdx v2 File Import Options (object) or Md File Import Options (object) or StringCatalog File Import Options (object) or Adoc File Import Options (object) or Other Files Import Options (object) (File Import Options)
     */
    public FilesCreateQuery importOptions(ImportOptions importOptions) {
        this.importOptions = importOptions;
        return this;
    }

    /**
     * @param exportOptions
     * General File Export Options (object) or Property File Export Options (object) or JavaScript File Export Options (object) or Md File Export Options (object) or Mdx v1 File Export Options (object) or Mdx v2 File Export Options (object) (File Export Options)
     */
    public FilesCreateQuery exportOptions(ExportOptions exportOptions) {
        this.exportOptions = exportOptions;
        return this;
    }

    /**
     * @param excludedTargetLanguages Set Target Languages the file should not be translated into. Do not use this option if the file should be available for all project languages.
     */
    public FilesCreateQuery excludedTargetLanguages(List<String> excludedTargetLanguages) {
        this.excludedTargetLanguages = excludedTargetLanguages;
        return this;
    }

    /**
     * @param attachLabelIds Attach labels to strings. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels<a/>
     */
    public FilesCreateQuery attachLabelIds(List<Long> attachLabelIds) {
        this.attachLabelIds = attachLabelIds;
        return this;
    }

    @Override
    public FileInfo execute() {
        AddFileRequest addFileRequest = new AddFileRequest();
        addFileRequest.setStorageId(this.storageID);
        addFileRequest.setName(this.name);
        addFileRequest.setDirectoryId(this.directoryId);
        addFileRequest.setTitle(this.title);
        addFileRequest.setContext(this.context);
        addFileRequest.setType(this.type);
        addFileRequest.setImportOptions(this.importOptions);
        addFileRequest.setExportOptions(this.exportOptions);
        addFileRequest.setExcludedTargetLanguages(this.excludedTargetLanguages);
        addFileRequest.setAttachLabelIds(this.attachLabelIds);

        return this.sourceFilesApi.addFile(this.projectID, addFileRequest).getData();
    }
}
