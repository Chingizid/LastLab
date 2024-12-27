package com.collecton2.reflection2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class InjectorOther {

    private Properties properties;

    public InjectorOther(String propertiesFileName) {
        properties = loadProperties(propertiesFileName);
    }

    private Properties loadProperties(String fileName){
         Properties props = new Properties();
         try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
               System.out.println("Файл свойств не найден: " + fileName);
                return props;
             }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
         }
        return props;
    }
    public <T> T inject(T target) {
        Class<?> targetClass = target.getClass();
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoInjectable.class)) {
                Class<?> fieldType = field.getType();
                String implClassName = properties.getProperty(fieldType.getName());

                if (implClassName != null && !implClassName.isEmpty()) {
                    try {
                        Class<?> implClass = Class.forName(implClassName);
                        Object dependencyInstance = implClass.getDeclaredConstructor().newInstance();

                        if (fieldType.isInstance(dependencyInstance)) {
                            field.setAccessible(true);
                            field.set(target, dependencyInstance);
                        } else {
                            System.err.println("Error: " + implClassName + " does not implement " + fieldType.getName());
                        }
                    } catch (Exception e) {
                        System.err.println("Error inject dependency for field " + field.getName() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                  System.err.println("No implementation class found for " + fieldType.getName() + " in properties file");
                }
            }
        }
        return target;
    }
}