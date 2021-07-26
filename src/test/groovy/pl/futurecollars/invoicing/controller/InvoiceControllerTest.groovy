package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.Helpers.TestHelpers.invoice
import static pl.futurecollars.invoicing.Helpers.TestHelpers.resetIds

@Unroll
class InvoiceControllerTest extends AbstractControllerTest {


    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService


    def "get all invoices returns empty array when no invoices added"() {

        expect:
        getAllInvoices() == []

    }

    def "add invoice returns sequential id"() {
        given:
        Invoice invoice = invoice(2)

        when:
        def id = addInvoiceAndReturnId(invoice)

        then:
        addInvoiceAndReturnId(invoice) == id + 1
        addInvoiceAndReturnId(invoice) == id + 2
        addInvoiceAndReturnId(invoice) == id + 3
    }

    def "all invoices are returned"() {
        given:
        def numberOfInvoices = 3
        def expectedInvoices = addUniqueInvoices(numberOfInvoices)

        when:
        def invoices = getAllInvoices()

        then:
        invoices.size() == numberOfInvoices
        resetIds(invoices).toString() == resetIds(expectedInvoices).toString()

    }

    def "get by id returns correct invoice"() {
        given:
        def expectedInvoices = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        def invoice = getInvoiceById(verifiedInvoice.getId())

        then:
        resetIds(invoice) == resetIds(verifiedInvoice)


    }

    def "404 is returned when getting not existing invoice"() {
        given:
        addUniqueInvoices(10)


        expect:
        mockMvc.perform(get("$INVOICE_ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]

    }

    def "404 is returned when deleting not existing invoice"() {
        given:
        addUniqueInvoices(10)


        expect:
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]

    }

    def "404 is returned when updating not existing invoice"() {
        given:
        addUniqueInvoices(10)

        expect:
        mockMvc.perform(put("$INVOICE_ENDPOINT/$id")
                .content(invoiceAsJson(1))
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]

    }

    def "invoice can be modified"() {
        given:
        def id = addInvoiceAndReturnId(invoice(4))
        def updatedInvoice = invoice(1)
        updatedInvoice.id = id

        expect:
        mockMvc.perform(
                put("$INVOICE_ENDPOINT/$id")
                        .content(jsonService.toJsonObject(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())

        def invoiceFromDbAfterUpdate = resetIds(getInvoiceById(id)).toString()
        def expectedInvoice = resetIds(updatedInvoice).toString()
        invoiceFromDbAfterUpdate == expectedInvoice
    }

    def "invoice can be deleted"() {
        given:
        def invoices = addUniqueInvoices(10)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }


}
