package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.Helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Stepwise

class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    private static final String ENDPOINT = "/invoices"


    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updatedDate = LocalDate.of(2020, 02, 28)

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

        given:
        def originalInvoice = TestHelpers.invoice(1)
        def invoiceAsJson = jsonService.toJsonObject(originalInvoice)

        when:
        def response = mockMvc.perform(post(ENDPOINT).content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        response == "1"
    }

    def "one invoice is returned when getting all invoices"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect((status().isOk()))
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.toJavaObject(response, Invoice[])

        then:
        invoices.size() == 1
        invoices[0].toString() == expectedInvoice.toString()
        invoices[0] == originalInvoice
    }

    def "invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = mockMvc.perform(get("$ENDPOINT/1"))
                .andExpect((status().isOk()))
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.toJavaObject(response, Invoice)

        then:
        invoices == expectedInvoice

    }

    def "invoice date can be modified"() {

        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.date = updatedDate

        def invoiceAsJson = jsonService.toJsonObject(modifiedInvoice)

        expect:
        mockMvc.perform(put("$ENDPOINT/1").content(invoiceAsJson).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
    }

    def "updated invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1
        expectedInvoice.date = updatedDate

        when:
        def response = mockMvc.perform(get("$ENDPOINT/1"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoices = jsonService.toJavaObject(response, Invoice)

        then:
        invoices == expectedInvoice
    }

    def "can delete invoice"() {

        expect:
        mockMvc.perform(delete("$ENDPOINT/1")).andExpect(status().isNoContent())

        and:
        mockMvc.perform(delete("$ENDPOINT/1")).andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("/$ENDPOINT/1")).andExpect(status().isNotFound())
    }

}