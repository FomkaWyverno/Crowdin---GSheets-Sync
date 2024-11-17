package ua.wyverno.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ConfigLoader {
    private final Config config;

    @Autowired
    public ConfigLoader() throws IOException, ConfigDamagedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        File file = new File("config.properties");
        Properties properties = new Properties();
        properties.load(Files.newInputStream(file.toPath()));

        // Перевіряємо на наявність полів
        Field[] fields = Config.class.getDeclaredFields();
        List<String> missingFieldConfig = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!properties.containsKey(field.getName())) {
                missingFieldConfig.add(field.getName());
            }
        }
        // Якщо є поля які не присутні у конфігу додаємо їх та повідомляємо про помилку
        if (missingFieldConfig.size() > 0) {
            missingFieldConfig.forEach(fieldName -> properties.put(fieldName, ""));
            properties.store(Files.newOutputStream(file.toPath()), null);
            throw new ConfigDamagedException(file + " No has all fields!");
        }

        this.config = new Config();

        for (Field field : fields) { // Доступ повторно не потрібно ставити, бо вже було раніше надано.
            String fieldName = field.getName();
            String s1 = fieldName.substring(0, 1).toUpperCase();
            String other = fieldName.substring(1);

            Method setMethod = this.config.getClass().getDeclaredMethod("set"+s1+other, String.class);
            setMethod.setAccessible(true);
            setMethod.invoke(this.config, properties.getProperty(fieldName));
        }
    }

    public Config getConfig() {
        return config;
    }
}
