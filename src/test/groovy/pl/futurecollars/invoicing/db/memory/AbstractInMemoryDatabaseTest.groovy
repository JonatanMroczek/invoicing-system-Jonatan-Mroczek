package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoicing.Helpers.TestHelpers.invoice

abstract class AbstractInMemoryDatabaseTest extends Specification {


    List<Invoice> invoices = (1..12).collect { invoice(it) }

    abstract Database getDatabaseInstance()



    def "should save invoices returning sequential id, invoice should have id set to correct value, get by id returns saved invoice"() {
        given:
        Database database = getDatabaseInstance()

        when:
        def ids = invoices.collect{it.id database.save(it) }

        then:
        ids == (1..invoices.size()).collect()
        ids.forEach({ assert database.getById(it).isPresent() })
        ids.forEach({ assert database.getById(it).get().getId() == it })
        ids.forEach({ assert database.getById(it).get() == invoices.get(it - 1) })
    }


    def "get by id returns empty optional when there is no invoice with given id"() {
        expect:
        database.getById(1).isEmpty()
    }

    def "get all returns empty collection if there were no invoices"() {
        !database.getAll().isEmpty()
    }

    def "get all returns all invoices in the database"() {
        given:
        invoices.collect({ database.save(it) })

        expect:
        database.getAll().forEach({ assert it == invoices.get(it.getId() - 1) })

    }

    def "should delete invoice"() {
        given:
        invoices.collect({ database.save(it) })
        when:
        database.delete(1)
        then:
        database.getById(1).isEmpty()
    }

    def "can delete all invoices"() {
        given:
        invoices.forEach({ database.save(it) })

        when:
        invoices.forEach({ database.delete(it.getId()) })

        then:
        database.getAll().isEmpty()
    }

    def "deleting not existing invoice returns empty optional"() {
        expect:
        database.delete(100) == Optional.empty();
    }

    def "it's possible to update invoice"() {
        when:
        int id = database.save(invoices.get(0))
        then:
        database.update(id, invoices.get(1))
        expect:
        database.getById(id).get() == invoices.get(1)
    }

    def "updating not existing invoice returns Optional.empty"() {
        expect:
        database.update(120, invoices.get(1)) == Optional.empty()


    }

}


