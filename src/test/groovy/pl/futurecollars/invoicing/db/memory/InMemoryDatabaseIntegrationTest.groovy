package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database

class InMemoryDatabaseIntegrationTest extends AbsteactInMemoryDatabaseTest{
    @Override
    Database getDatabaseInstance() {
        return new InMemoryDatabase()
    }
}
