package ua.wyverno.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.api.services.sheets.v4.model.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SynchronizeSheetManager {
    private final static Logger logger = LoggerFactory.getLogger(SynchronizeSheetManager.class);
    private final static File IGNORE_LIST_FILE = new File("synchronize.sheets.json");
    private final ObjectMapper mapper;
    private final BufferedReader reader;

    private final Map<String, SheetEntity> sheetSkipMapBySheetId;
    @Autowired
    public SynchronizeSheetManager() throws IOException {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.reader = new BufferedReader(new InputStreamReader(System.in));

        this.sheetSkipMapBySheetId = this.loadSheetSkipMap();
    }
    /**
     * Завантажує із JSON-файлу - Мапу аркушів які слід пропускати синхронізацію з таблиці.
     * @return Мапа сутностей аркушів {@link SheetEntity} - де є {@link SheetEntity#sheetId} та {@link SheetEntity#sheetName}.
     */
    private Map<String, SheetEntity> loadSheetSkipMap() throws IOException {
        final Set<SheetEntity> sheetIgnoreEntities;
        if (IGNORE_LIST_FILE.exists() && IGNORE_LIST_FILE.isFile()) {
            sheetIgnoreEntities = this.mapper.readValue(IGNORE_LIST_FILE, new TypeReference<>(){});
        } else {
            sheetIgnoreEntities = new HashSet<>();
        }
        return sheetIgnoreEntities.stream()
                .collect(Collectors.toMap(
                        SheetEntity::sheetId,
                        Function.identity(),
                        (exists, replacement) -> replacement));
    }

    /**
     * Перевіряє, чи потрібно ігнорувати аркуш.
     * Якщо аркуша немає у списку, запитує користувача й оновлює список.
     * @param sheet Об'єкт аркуша з Гугл таблиці.
     * @return true, якщо таблицю потрібно ігнорувати, інакше false.
     */
    public boolean shouldSkipSheetSynchronize(Sheet sheet) {
        try {
            String sheetTitle = sheet.getProperties().getTitle();
            String sheetId = sheet.getProperties().getSheetId().toString();

            if (this.sheetSkipMapBySheetId.containsKey(sheetId)) {
                boolean result = this.skipSheetSynchronize(sheetId, sheetTitle);
                if (result) {
                    logger.info("Sheet '{}' Id: '{}' is ignored.", sheetTitle, sheetId);
                } else {
                    logger.info("Sheet '{}' Id: '{}' is not ignored.", sheetTitle, sheetId);
                }
                return result;
            }

            if (this.askIgnoredSheet(sheetId, sheetTitle)) {
                this.addSheetToMap(sheetId, sheetTitle, true);
                return true;
            }
            this.addSheetToMap(sheetId, sheetTitle, false);

            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Додає інформацію про таблиці, чи потрібно її ігнорувати
     * @param sheetId айді таблиці
     * @param sheetTitle назва таблиці
     * @param isIgnore чи потрібно ігнорувати
     * @throws IOException проблеми з оновленням файлу конфігурації
     */
    private void addSheetToMap(String sheetId, String sheetTitle, boolean isIgnore) throws IOException {
        this.sheetSkipMapBySheetId.put(sheetId, new SheetEntity(sheetId, sheetTitle, isIgnore));
        logger.debug("Added to ignored sheet: {} Id: {}", sheetTitle, sheetId);
        this.updateFile();
    }

    /**
     * Перевіряє чи слід пропустити синхронізацію з Кроудіном для аркуша, чи ні
     * @param sheetId айді аркуша
     * @param sheetTitle назва аркуша
     * @return true якщо цей аркуш потрібно пропустити синхронізацію, false якщо не потрібно пропускати синхронізацію
     */
    private boolean skipSheetSynchronize(String sheetId, String sheetTitle) {
        SheetEntity sheetIgnore = this.sheetSkipMapBySheetId.get(sheetId);
        Objects.requireNonNull(sheetIgnore, "No information for sheet: " + sheetTitle + " Id: " + sheetId);
        return sheetIgnore.skipSheetSync();
    }

    /**
     * Запитує у користувача, чи потрібно пропустити синхронізацію цього аркуша.
     *
     * @param sheetId айді аркуш
     * @param sheetTitle назва аркуша
     * @return якщо було введено yes - поверне true все інше false
     * @throws IOException при читанні потоку консолі
     */
    private boolean askIgnoredSheet(String sheetId, String sheetTitle) throws IOException {
        System.out.printf("Do you want to skip sheet synchronization - sheet name: '%s' id: '%s'? (yes/no): ", sheetTitle, sheetId);
        String userInput = this.reader.readLine();
        return "yes".equalsIgnoreCase(userInput);

    }

    /**
     * Оновлює JSON файл значень інформації про аркуші які потрібно ігнорувати
     * @throws IOException виникає при записі файлу
     */
    private void updateFile() throws IOException {
        this.mapper.writeValue(IGNORE_LIST_FILE, this.sheetSkipMapBySheetId.values());
    }

    /**
     * Представлення сутності аркуша
     * @param sheetId айді аркуша
     * @param sheetName назва аркуша
     * @param skipSheetSync чи ігнорується він системою синхронізації
     */
    private record SheetEntity(String sheetId, String sheetName, boolean skipSheetSync) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            SheetEntity that = (SheetEntity) o;
            return sheetId.equals(that.sheetId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.sheetId);
        }
    }
}
