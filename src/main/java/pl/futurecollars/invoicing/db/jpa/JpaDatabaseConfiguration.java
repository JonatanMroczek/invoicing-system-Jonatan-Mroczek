package pl.futurecollars.invoicing.db.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
public class JpaDatabaseConfiguration {

    @Bean
    public Database<Invoice> invoiceJpa(InvoiceRepository invoiceRepository) {
        return new JpaDatabase(invoiceRepository);
    }

    @Bean
    public Database<Company> companyJpa(CompanyRepository companyRepository) {
        return new JpaDatabase(companyRepository);
    }

}
