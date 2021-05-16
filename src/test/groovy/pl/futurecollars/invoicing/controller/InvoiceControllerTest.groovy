package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.Helpers.TestHelpers.invoice

@SpringBootTest
@AutoConfigureMockMvc
@Unroll
class InvoiceControllerTest extends Specification {

    private static final String ENDPOINT = "/invoices"

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }


    def "get all invoices returns empty array when no invoices added"() {

        expect:
        getAllInvoices() == []

    }

    def "all invoices are returned"() {
        given:
        def numberOfInvoices = 3
        def expectedInvoices = addUniqueInvoices(numberOfInvoices)

        when:
        def invoices = getAllInvoices()

        then:
        invoices == expectedInvoices
    }

    def "add invoice returns sequential id"() {
        given:
        def invoiceAsJson = invoiceAsJson(1)

        when:
        def id = addInvoiceAndReturnId(invoiceAsJson)

        then:
        addInvoiceAndReturnId(invoiceAsJson) == id + 1
        addInvoiceAndReturnId(invoiceAsJson) == id + 2
        addInvoiceAndReturnId(invoiceAsJson) == id + 3
    }

    def "get by id returns correct invoice"() {
        given:
        def expectedInvoices = addUniqueInvoices(5)
        def verifiedInvoice = expectedInvoices.get(2)

        when:
        def invoice = getInvoiceById(verifiedInvoice.getId())

        then:
        invoice == verifiedInvoice

    }

    def "invoice date can be modified"() {
        given:
        def id = addInvoiceAndReturnId(invoiceAsJson(10))
        def updatedInvoice = invoice(123)
        updatedInvoice.id = id

        expect:
        mockMvc.perform(
                put("$ENDPOINT/$id")
                        .content(jsonService.toJsonObject(updatedInvoice))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())

        getInvoiceById(id) == updatedInvoice
    }

    def "404 is returned when getting not existing invoice"() {
        given:
        addUniqueInvoices(10)


        expect:
        mockMvc.perform(get("$ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]

    }

    def "404 is returned when deleting not existing invoice"() {
        given:
        addUniqueInvoices(10)


        expect:
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]

    }

    def "404 is returned when updating not existing invoice"() {
        given:
        addUniqueInvoices(10)

        expect:
        mockMvc.perform(put("$ENDPOINT/$id")
                .content(invoiceAsJson(1))
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

        where:
        id << [-2, 0, 500]

    }

    def "can delete invoice"() {
        given:
        def invoices = addUniqueInvoices(10)

        expect:
        invoices.each { invoice -> deleteInvoice(invoice.getId()) }
        getAllInvoices().size() == 0
    }

    private int addInvoiceAndReturnId(String invoiceAsJson) {
        Integer.valueOf(
                mockMvc.perform(
                        post(ENDPOINT)
                                .content(invoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status()
                                .isOk())
                        .andReturn()
                        .response.contentAsString)
    }

    private ResultActions deleteInvoice(int id) {
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNoContent())

    }

    private List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get("$ENDPOINT"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toJavaObject(response, Invoice[])
    }


    private List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(jsonService.toJsonObject(invoice))

            return invoice
        }
    }

    private Invoice getInvoiceById(int id) {
        def invoiceAsString = mockMvc.perform(get("$ENDPOINT/$id")).andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toJavaObject(invoiceAsString, Invoice)
    }


    private String invoiceAsJson(int id) {
        jsonService.toJsonObject(invoice(id))
    }


}
