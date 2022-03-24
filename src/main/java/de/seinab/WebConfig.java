package de.seinab;

import de.seinab.backend.datatable.resolver.DataTablesEditorArgumentResolver;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private DataTablesEditorArgumentResolver dataTablesEditorArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(dataTablesEditorArgumentResolver);
    }

    @Bean
    public TemplateLoader emailTemplateLoad() throws IOException {
        return new ClassTemplateLoader(this.getClass(), "/templates/email/");
    }
}
