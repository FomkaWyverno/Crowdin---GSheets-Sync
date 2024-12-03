package ua.wyverno.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private final CoreConfig coreConfig;
    private final SyncConfig syncConfig;

    @Autowired
    public ConfigLoader() throws IOException, ConfigDamagedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.info("Initializing ConfigLoader...");
        this.coreConfig = this.loadCoreConfig();
        this.syncConfig = this.loadSyncConfig();
        logger.info("ConfigLoader initialized successfully.");
    }

    /**
     * Завантажує конфігурацію для синхронізації, за потреби
     * створює порожній файл, або доповнює файл конфіга якщо не існує певних рядків.
     * @return {@link SyncConfig} конфігурація синхронізації між кроудіном та гугл таблицею
     * @throws IOException При роботі з файлом виникла помилка.
     * @throws ConfigDamagedException Якщо конфіг файлі відсутні поля, або файл не існує, та увімкнено, щоб створювалось виключення.
     * @throws NoSuchMethodException Не існує методу Сеттера у об'єкті Конфіга, щоб встановити значення для поля.
     * @throws IllegalAccessException Не можливо отримати доступ до поля, або до методу.
     * @throws InvocationTargetException Виникли проблеми при виклику Сеттеру об'єкта конфіга.
     */
    private SyncConfig loadSyncConfig() throws ConfigDamagedException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        File file = new File("sync-config.properties");
        logger.info("Loading Sync-Config from file: {}", file.toPath().toAbsolutePath());
        return this.loadConfig(new SyncConfig(file), file, false);
    }

    /**
     * Завантажує основну конфігурацію для запуску програми, за потреби
     * створює порожній файл, або доповнює файл конфіга якщо не існує певних рядків.
     * @return {@link CoreConfig} основна конфігурацію
     * @throws IOException При роботі з файлом виникла помилка.
     * @throws ConfigDamagedException Якщо конфіг файлі відсутні поля, або файл не існує, та увімкнено, щоб створювалось виключення.
     * @throws NoSuchMethodException Не існує методу Сеттера у об'єкті Конфіга, щоб встановити значення для поля.
     * @throws IllegalAccessException Не можливо отримати доступ до поля, або до методу.
     * @throws InvocationTargetException Виникли проблеми при виклику Сеттеру об'єкта конфіга.
     */

    private CoreConfig loadCoreConfig()
            throws ConfigDamagedException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        File file = new File("core-config.properties");
        logger.info("Loading Core-Config from file: {}", file.toPath().toAbsolutePath());
        return loadConfig(new CoreConfig(), file, true);
    }

    /**
     * Завантажує файл конфігурації, та встановлює для об'єкта конфігурації всі поля які прописані у файлі .
     * @param config об'єкт Конфігурації які реалізують інтерфейс {@link Config}.
     * @param file файл де знаходиться конфігурація.
     * @param throwIfMissingFieldsOrNotExistFile чи потрібно викидувати виключення, якщо деякі поля у файлі відсутні, або файл не існує .
     * @return Налаштований конфігураційний об'єкт.
     * @param <T> будь-який об'єкт реалізуючий інтерфейс {@link Config}.
     * @throws IOException При роботі з файлом виникла помилка.
     * @throws ConfigDamagedException Якщо конфіг файлі відсутні поля, або файл не існує, та увімкнено, щоб створювалось виключення.
     * @throws NoSuchMethodException Не існує методу Сеттера у об'єкті Конфіга, щоб встановити значення для поля.
     * @throws IllegalAccessException Не можливо отримати доступ до поля, або до методу.
     * @throws InvocationTargetException Виникли проблеми при виклику Сеттеру об'єкта конфіга.
     */
    private <T extends Config> T loadConfig(T config, File file, boolean throwIfMissingFieldsOrNotExistFile)
            throws IOException, ConfigDamagedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<Field> fieldsConfig = this.getConfigFields(config.getClass());
        Properties properties = this.loadProperties(file, fieldsConfig, throwIfMissingFieldsOrNotExistFile);

        // Перевіряємо на наявність полів
        List<String> missingFields = this.getMissingFields(properties, fieldsConfig);
        if (!missingFields.isEmpty()) { // Якщо не всі присутні поля, викидаємо виключення для зупинки програми
            this.createProperties(properties, file, missingFields);
            if (throwIfMissingFieldsOrNotExistFile) throw new ConfigDamagedException(file + " no has all field CoreConfig!");
        }
        return this.initConfig(config, fieldsConfig, properties);
    }

    /**
     * Шукає всі створені поля у классі Конфігурації, автоматично вмикає доступ до них.
     * @param configClass Класс конфігурації з якого потрібно витягнути всі поля
     * @return {@link List}<{@link Field}> лист з полями
     */
    private List<Field> getConfigFields(Class<? extends Config> configClass) {
        return Arrays.stream(configClass.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .toList();
    }

    /**
     * Завантажує файл properties, у разі відсутності створює файл з потрібними полями.
     * @param file Файл конфігурації
     * @param fieldsConfig Поля конфігурації
     * @param throwIfFileNotExists Чи потрібно викинути виключення, якщо файл не існує.
     * @return {@link Properties} з завантаженими полями з файлу
     * @throws IOException При помилці з завантаженням файлу
     */
    private Properties loadProperties(File file, List<Field> fieldsConfig, boolean throwIfFileNotExists) throws IOException {
        Properties properties = new Properties();

        if (!file.exists() || file.isDirectory()) { // Якщо не існує, або це директорія, тоді створюємо конфіг файл
            this.createProperties(properties, file, fieldsConfig.stream().map(Field::getName).toList());
            String messageWarn = file.toPath().toAbsolutePath() + " - CoreConfig file not exists!!!";
            logger.warn(messageWarn);
            if (throwIfFileNotExists) throw new FileNotFoundException(messageWarn);
        } else {
            properties.load(Files.newInputStream(file.toPath()));
        }
        return properties;
    }

    /**
     * Шукає пропущені поля яких немає у файлу конфігурації
     * @param properties завантажений файл конфігурації у вигляді Проперті
     * @param fields поля конфігураційного об'єкта
     * @return {@link List}<{@link String}> лист з назвами полей які відсутні
     */

    private List<String> getMissingFields(Properties properties, List<Field> fields) {
        return fields.stream()
                .map(Field::getName)
                .filter(fieldName -> !properties.containsKey(fieldName))
                .toList();
    }

    /**
     * Створює файл конфігурації, або доповнює файл конфігурації полями
     * @param properties Проперті, можуть бути поля зі значеннями, щоб зберегти їх, у разі якщо були відсутні певні поля
     * @param file файл де знаходиться конфігурація
     * @param missingFields пропущені поля
     * @throws IOException Виникає якщо щось пішло не так при збереженні файлу
     */
    private void createProperties(Properties properties, File file, List<String> missingFields) throws IOException {
        missingFields.forEach(fieldName -> properties.put(fieldName, ""));
        properties.store(Files.newOutputStream(file.toPath()), null);
    }

    /**
     * Встановлюємо всі значення для конфіг об'єкта за допомогою методів Сеттерів у об'єкті конфігурації.
     * @param config Конфіг об'єкт
     * @param fieldsConfig поля конфігурації
     * @param properties Проперті з полями та даними які потрібно завантажити в Конфігураційний об'єкт
     * @return {@literal <}T extends {@link Config}> налаштований об'єкт конфігурації
     * @param <T> об'єкт конфігурації реалізуючий {@link Config}
     * @throws NoSuchMethodException Не було знайдено сеттера через рефлексію у Конфігураційному об'єкті
     * @throws IllegalAccessException Немає доступу до методу, або поля через рефлексію
     * @throws InvocationTargetException Виникла помилка при виклику методу через рефлексію
     */
    private <T extends Config> T initConfig(T config, List<Field> fieldsConfig, Properties properties)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Додаємо всі значення з конфігу до Конфіг классу
        for (Field field : fieldsConfig) { // Доступ повторно не потрібно ставити, бо вже було раніше надано.
            String fieldName = field.getName();
            String s1 = fieldName.substring(0, 1).toUpperCase();
            String other = fieldName.substring(1);

            Method setMethod = config.getClass().getDeclaredMethod("set" + s1 + other, String.class);
            setMethod.setAccessible(true);
            setMethod.invoke(config, properties.getProperty(fieldName));
        }

        return config;
    }

    public CoreConfig getCoreConfig() {
        return this.coreConfig;
    }
    public SyncConfig getSyncConfig() {return this.syncConfig;}
}
