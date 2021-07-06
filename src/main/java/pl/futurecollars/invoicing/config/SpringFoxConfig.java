package pl.futurecollars.invoicing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("pl.futurecollars"))
            .paths(PathSelectors.any())
            .build()
            .tags(
                new Tag("Invoice Controller", "Controller used to list / add / update / delete invoices. "),
                new Tag("Tax-controller", "Controller used to calculate taxes."))
            .apiInfo(customApiInfo());

    }

    private ApiInfo customApiInfo() {
        return new ApiInfoBuilder()
            .description("Application to manage set of invoices")
            .title("Private Invoicing")
            .contact(contactInfo())
            .version("v1")
            .build();
    }

    private Contact contactInfo() {
        return new Contact("Jonatan Mroczek", "https://github.com/JonatanMroczek/", "jonatanmroczek@gmail.com");
    }

}
