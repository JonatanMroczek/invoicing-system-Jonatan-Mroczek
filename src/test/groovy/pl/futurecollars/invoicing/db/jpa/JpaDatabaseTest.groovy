package pl.futurecollars.invoicing.db.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.db.AbstractInMemoryDatabaseTest
import pl.futurecollars.invoicing.db.Database

@IfProfileValue(name = "spring.profiles.active", value = "jpa")
@DataJpaTest
class JpaDatabaseTest extends AbstractInMemoryDatabaseTest {

    @Autowired
    private InvoiceRepository invoiceRepository

    @Override
    Database getDatabaseInstance() {
        assert invoiceRepository != null
        return new JpaDatabase(invoiceRepository)
    }
}
