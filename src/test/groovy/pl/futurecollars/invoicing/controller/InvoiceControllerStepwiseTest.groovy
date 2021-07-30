package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.Helpers.TestHelpers
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.Helpers.TestHelpers.resetIds

@SpringBootTest
@AutoConfigureMockMvc
@Stepwise
@WithMockUser

class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    @Autowired
    private Database<Invoice> database

    @Shared
    private int invoiceId

    private static final String ENDPOINT = "/invoices"


    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updatedDate = LocalDate.of(2020, 02, 28)

    def "database is reset to ensure clean state"() {
        expect:
        database != null

        when:
        database.reset()

        then:
        database.getAll().size() == 0
    }

    def "get all invoices returns empty array when no invoices added"() {

        when:
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        response == "[]"
    }

    def "can add an invoice"() {

        def invoiceAsJson = jsonService.toJsonObject(originalInvoice)

        when:
        invoiceId = Integer.valueOf(mockMvc.perform(post(ENDPOINT).content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .response
                .contentAsString)
        then:
        invoiceId > 0
    }

    def "one invoice is returned when getting all invoices"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId

        when:
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect((status().isOk()))
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.toJavaObject(response, Invoice[])

        then:
        invoices.size() == 1
        resetIds(invoices[0]) == resetIds(expectedInvoice)

    }

    def "invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$invoiceId"))
                .andExpect((status().isOk()))
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.toJavaObject(response, Invoice)

        then:
        resetIds(invoice) == resetIds(expectedInvoice)

    }

    def "invoice date can be modified"() {

        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.date = updatedDate

        def invoiceAsJson = jsonService.toJsonObject(modifiedInvoice)

        expect:
        mockMvc.perform(put("$ENDPOINT/$invoiceId").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(status().isNoContent())
    }

    def "updated invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId
        expectedInvoice.date = updatedDate

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$invoiceId"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.toJavaObject(response, Invoice)

        then:
        resetIds(invoice) == resetIds(expectedInvoice)
    }

    def "can delete invoice"() {

        expect:
        mockMvc.perform(delete("$ENDPOINT/$invoiceId").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isNoContent())

        and:
        mockMvc.perform(delete("$ENDPOINT/$invoiceId").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("/$ENDPOINT/$invoiceId")).andExpect(status().isNotFound())
    }

}