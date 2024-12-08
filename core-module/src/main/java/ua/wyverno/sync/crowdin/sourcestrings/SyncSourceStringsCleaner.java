package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.RemoveBatchStringRequestBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class SyncSourceStringsCleaner {

    private final BufferedReader reader;

    @Autowired
    public SyncSourceStringsCleaner() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Очищає від не потрібних вихідних рядків
     *
     * @param requiredStrings потрібні рядки
     * @param allStrings      всі рядки у Кроудіні
     * @return Повертає запит з видаленням не потрібних рядків
     */
    public List<RemoveBatchStringRequestBuilder> cleanSourceStrings(List<SourceString> requiredStrings, List<SourceString> allStrings) {
        List<Long> requiredStringId = requiredStrings.stream().map(SourceString::getId).toList();
        List<SourceString> noReuiredList = allStrings.stream()
                .filter(string -> !requiredStringId.contains(string.getId()))
                .toList();

        List<RemoveBatchStringRequestBuilder> removeRequests = new ArrayList<>();

        noReuiredList.stream()
                .filter(this::askToDeleteSourceString)
                .forEach(string -> removeRequests.add(new RemoveBatchStringRequestBuilder().stringID(string.getId())));

        return removeRequests;
    }

    private boolean askToDeleteSourceString(SourceString sourceString) {
        try {
            System.out.printf("""
                    Do you want to delete source string?
                    Id: %s
                    Text: %s
                    Context: %s
                    Print (yes/no):\s""", sourceString.getIdentifier(), sourceString.getText(), sourceString.getContext());
            String userInput = reader.readLine();
            return "yes".equalsIgnoreCase(userInput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
