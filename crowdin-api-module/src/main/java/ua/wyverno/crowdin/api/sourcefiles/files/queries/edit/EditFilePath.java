package ua.wyverno.crowdin.api.sourcefiles.files.queries.edit;

public enum EditFilePath {
    BRANCH_ID("/branchId"),
    DIRECTORY_ID("/directoryId"),
    NAME("/name"),
    TITLE("/title"),
    CONTEXT("/context"),
    PRIORITY("/priority"),
    IMPORT_OPTIONS_CLEAN_TAGS_AGGRESSIVELY("/importOptions/cleanTagsAggressively"),
    IMPORT_OPTIONS_TRANSLATE_HIDDEN_TEXT("/importOptions/translateHiddenText"),
    IMPORT_OPTIONS_TRANSLATE_HYPERLINK_URLS("/importOptions/translateHyperlinkUrls"),
    IMPORT_OPTIONS_TRANSLATE_HIDDEN_ROWS_AND_COLUMNS("/importOptions/translateHiddenRowsAndColumns"),
    IMPORT_OPTIONS_IMPORT_NOTES("/importOptions/importNotes"),
    IMPORT_OPTIONS_IMPORT_HIDDEN_SLIDES("/importOptions/importHiddenSlides"),
    IMPORT_OPTIONS_FIRST_LINE_CONTAINS_HEADER("/importOptions/firstLineContainsHeader"),
    IMPORT_OPTIONS_IMPORT_KEY_AS_SOURCE("/importOptions/importKeyAsSource"),
    IMPORT_OPTIONS_IMPORT_TRANSLATIONS("/importOptions/importTranslations"),
    IMPORT_OPTIONS_SCHEME("/importOptions/scheme"),
    IMPORT_OPTIONS_TRANSLATE_CONTENT("/importOptions/translateContent"),
    IMPORT_OPTIONS_IMPORT_HIDDEN_SHEETS("/importOptions/importHiddenSheets"),
    IMPORT_OPTIONS_TRANSLATE_ATTRIBUTES("/importOptions/translateAttributes"),
    IMPORT_OPTIONS_CONTENT_SEGMENTATION("/importOptions/contentSegmentation"),
    IMPORT_OPTIONS_TRANSLATABLE_ELEMENTS("/importOptions/translatableElements"),
    IMPORT_OPTIONS_SRX_STORAGE_ID("/importOptions/srxStorageId"),
    IMPORT_OPTIONS_HIDE_ATTRIBUTE_VALUES("/importOptions/hideAttributeValues"),
    IMPORT_OPTIONS_CUSTOM_SEGMENTATION("/importOptions/customSegmentation"),
    IMPORT_OPTIONS_EXCLUDED_ELEMENTS("/importOptions/excludedElements"),
    IMPORT_OPTIONS_EXCLUDE_INCLUDE_DIRECTIVES("/importOptions/excludeIncludeDirectives"),
    IMPORT_OPTIONS_EXCLUDED_FRONT_MATTER_ELEMENTS("/importOptions/excludedFrontMatterElements"),
    IMPORT_OPTIONS_EXCLUDE_CODE_BLOCKS("/importOptions/excludeCodeBlocks"),
    IMPORT_OPTIONS_INLINE_TAGS("/importOptions/inlineTags"),
    EXPORT_OPTIONS_EXPORT_PATTERN("/exportOptions/exportPattern"),
    EXPORT_OPTIONS_ESCAPE_QUOTES("/exportOptions/escapeQuotes"),
    EXPORT_OPTIONS_EXPORT_QUOTES("/exportOptions/exportQuotes"),
    EXPORT_OPTIONS_ESCAPE_SPECIAL_CHARACTERS("/exportOptions/escapeSpecialCharacters"),
    EXCLUDED_TARGET_LANGUAGES("/excludedTargetLanguages"),
    ATTACH_LABEL_IDS("/attachLabelIds"),
    DETACH_LABEL_IDS("/detachLabelIds");
    private final String value;
    EditFilePath(String value) {
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
