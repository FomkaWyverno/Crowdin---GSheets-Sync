package ua.wyverno.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class ConfigLoader {
    private final Config config;

    @Autowired
    public ConfigLoader() throws IOException, ConfigDamagedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File file = new File("config.properties");
        Properties properties = new Properties();
        List<Field> fieldsConfig = this.getConfigFields();

        if (!file.exists() || file.isDirectory()) { // Якщо не існує, або це директорія, тоді створюємо конфіг файл
            this.createProperties(properties, file, fieldsConfig.stream().map(Field::getName).toList());
            throw new FileNotFoundException(file.toPath().toAbsolutePath() + " - Config file not exists!!!");
        }
        properties.load(Files.newInputStream(file.toPath()));

        // Перевіряємо на наявність полів
        List<String> missingFields = this.getMissingFields(properties, fieldsConfig);
        if (!missingFields.isEmpty()) { // Якщо не всі присутні поля
            this.createProperties(properties, file, missingFields);
            throw new ConfigDamagedException(file + " no has all field Config!");
        }

        this.config = new Config();

        // Додаємо всі значення з конфігу до Конфіг классу
        for (Field field : fieldsConfig) { // Доступ повторно не потрібно ставити, бо вже було раніше надано.
            String fieldName = field.getName();
            String s1 = fieldName.substring(0, 1).toUpperCase();
            String other = fieldName.substring(1);

            Method setMethod = this.config.getClass().getDeclaredMethod("set"+s1+other, String.class);
            setMethod.setAccessible(true);
            setMethod.invoke(this.config, properties.getProperty(fieldName));
        }
    }

    private List<Field> getConfigFields() {
        return Arrays.stream(Config.class.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .toList();
    }
    private List<String> getMissingFields(Properties properties, List<Field> fields) {
        return fields.stream()
                .map(Field::getName)
                .filter(fieldName -> !properties.containsKey(fieldName))
                .toList();
    }
    private void createProperties(Properties properties, File file, List<String> fields) throws ConfigDamagedException, IOException {
        fields.forEach(fieldName -> properties.put(fieldName, ""));
        properties.store(Files.newOutputStream(file.toPath()), null);
    }

    public Config getConfig() {
        return config;
    }
}
