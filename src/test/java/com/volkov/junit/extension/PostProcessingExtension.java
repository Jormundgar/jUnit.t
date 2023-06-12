package com.volkov.junit.extension;

import com.volkov.junit.service.UserService;
import lombok.Getter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        System.out.println("Post Processing Extension");
        var fields = testInstance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(Getter.class)) {
                field.set(testInstance, new UserService(null));
            }
        }
    }
}
