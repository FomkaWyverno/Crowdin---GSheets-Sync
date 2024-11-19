package ua.wyverno.crowdin.service;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.springframework.lang.NonNull;
import ua.wyverno.crowdin.api.sourcefiles.files.FilesAPI;

import java.util.List;
import java.util.function.Predicate;

public interface CrowdinFilesService {
    FilesAPI files();
}
