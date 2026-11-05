package com.hellmetz.festival.config;

import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.model.Scene;
import com.hellmetz.festival.model.Style;
import com.hellmetz.festival.service.EditionService;
import com.hellmetz.festival.service.SceneService;
import com.hellmetz.festival.service.StyleService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Convertit les identifiants (String issus des <select>) en entites JPA,
 * pour que le binding des formulaires sur les champs @ManyToOne fonctionne
 * (ex: th:field="*{scene}" avec des <option th:value="${s.id}">).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StyleService styleService;
    private final SceneService sceneService;
    private final EditionService editionService;

    public WebMvcConfig(StyleService styleService,
                        SceneService sceneService,
                        EditionService editionService) {
        this.styleService = styleService;
        this.sceneService = sceneService;
        this.editionService = editionService;
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Style>() {
            @Override
            public Style convert(@NonNull String id) {
                if (id.isBlank()) return null;
                return styleService.findById(Long.valueOf(id));
            }
        });
        registry.addConverter(new Converter<String, Scene>() {
            @Override
            public Scene convert(@NonNull String id) {
                if (id.isBlank()) return null;
                return sceneService.findById(Long.valueOf(id));
            }
        });
        registry.addConverter(new Converter<String, Edition>() {
            @Override
            public Edition convert(@NonNull String id) {
                if (id.isBlank()) return null;
                return editionService.findById(Long.valueOf(id));
            }
        });
    }
}
