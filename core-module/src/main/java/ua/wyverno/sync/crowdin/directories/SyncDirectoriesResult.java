package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.List;
import java.util.Map;

public record SyncDirectoriesResult(Directory rootDirectory, Map<Directory, List<GoogleSheet>> groupingSheetByCategoryDir) {
}
