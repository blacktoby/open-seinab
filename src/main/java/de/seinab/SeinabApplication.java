package de.seinab;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import static java.util.Collections.singletonList;

@SpringBootApplication
public class SeinabApplication {

    public static void main(String[] args) {
        args = ArrayUtils.add(args,
                "--spring.config.location=classpath:/application.properties,file:${CONFIG_FILE}");
        SpringApplication.run(SeinabApplication.class, args);
    }

    public SeinabApplication(FreeMarkerConfigurer freeMarkerConfigurer) {
        freeMarkerConfigurer.getTaglibFactory().setClasspathTlds(singletonList("/META-INF/security.tld"));
    }

}
