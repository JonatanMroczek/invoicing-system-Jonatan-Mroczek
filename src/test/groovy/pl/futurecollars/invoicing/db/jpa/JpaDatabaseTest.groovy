package pl.futurecollars.invoicing.db.jpa

import org.springframework.beans.factory.annotation.Autowired
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.AbstractInMemoryDatabaseTest

class JpaDatabaseTest extends AbstractInMemoryDatabaseTest {

    @Autowired
    private InvoiceRepository invoiceRepository
    @Override
    Database getDatabaseInstance() {
        assert invoiceRepository != null
       return new JpaDatabase(invoiceRepository)
    }
}
