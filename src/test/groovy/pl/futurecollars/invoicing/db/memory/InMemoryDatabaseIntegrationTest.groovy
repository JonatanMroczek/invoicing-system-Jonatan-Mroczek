package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.AbstractInMemoryDatabaseTest
import pl.futurecollars.invoicing.db.Database

class InMemoryDatabaseIntegrationTest extends AbstractInMemoryDatabaseTest {
    @Override
    Database getDatabaseInstance() {
        return new InMemoryDatabase()
    }
}
