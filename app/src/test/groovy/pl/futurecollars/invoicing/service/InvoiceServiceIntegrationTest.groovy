package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoicing.service.helpers.TestHelpers.invoice

class InvoiceServiceIntegrationTest extends Specification {
    private InvoiceService service
    private List<Invoice> invoices

    def setup() {
        Database db = new InMemoryDatabase()
        service = new InvoiceService(db)

        invoices = (1..12).collect { invoice(it) }
    }

    def "should save invoices by given id"() {
        when:
        def ids = invoices.collect { service.save(it) }

        then:
        ids == (1..invoices.size()).collect()
        ids.collect({ assert service.getById(it).isPresent() })
        ids.collect({ assert service.getById(it).get().getId() == it })
        ids.collect({ assert service.getById(it).get() == invoices.get(it - 1) })

    }

    def "get all returns empty collection when there are no invoices"() {
        expect:
        service.getAll().isEmpty()
    }

    def "get all returns all invoices in the database"() {
        given:
        invoices.collect({ service.save(it) })

        expect:
        service.getAll().size() == invoices.size()
        service.getAll().collect({ assert it == invoices.get(it.getId() - 1) })


    }

    def "should delete invoice"() {
        given:
        invoices.collect({ service.save(it) })
        when:
        service.delete(1)
        then:
        service.getById(1).isEmpty()
    }


    def "deleting not existing invoice is not causing any error"() {
        expect:
        service.delete(100)
    }

    def "it's possible to update the invoice"() {
        given:
        int id = service.save(invoices.get(0))

        when:
        service.update(id, invoices.get(1))

        then:
        service.getById(id).get() == invoices.get(1)
    }

    def "updating not existing invoice throws exception"() {
        when:
        service.update(100, invoices.get(1))

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Id 100 does not exist"
    }

}
