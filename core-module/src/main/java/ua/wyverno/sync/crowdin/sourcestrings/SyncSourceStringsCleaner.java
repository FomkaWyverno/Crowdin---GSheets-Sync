package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class SyncSourceStringsCleaner {

    private final BufferedReader reader;

    @Autowired
    public SyncSourceStringsCleaner() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void cleanSourceStrings(List<SourceString> requiredStrings, List<SourceString> existsStrings) {

    }
}
