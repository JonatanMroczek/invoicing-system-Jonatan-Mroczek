package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import spock.lang.Specification

import static pl.futurecollars.invoicing.Helpers.TestHelpers.invoice

class InvoiceServiceUnitTest extends Specification {
    private InvoiceService service
    private Database database

    def setup() {
        database = Mock()
        service = new InvoiceService(database)
    }

    def "calling save() should call database save"() {
        given:
        def invoice = invoice(1)

        when:
        service.save(invoice)

        then:
        1 * database.save(invoice)


    }

    def "calling getById() should call database getById()"() {
        given:
        def id = 100

        when:
        service.getById(id)

        then:
        1 * database.getById(id)


    }


    def "calling geAll() should call database getAll()"() {
        when:
        service.getAll()

        then:
        1 * database.getAll()

    }

    def "calling update() should call database update()"() {
        given:
        def id = 100
        def invoice = invoice(id)

        when:
        service.update(100, invoice)

        then:
        1 * database.update(id, invoice)

    }

    def "calling delete() should call database delete()"() {
        given:
        def id = 2

        when:
        service.delete(id)

        then:
        1 * database.delete(id)
    }
}
