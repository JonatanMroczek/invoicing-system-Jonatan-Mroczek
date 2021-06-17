package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorResult
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.Helpers.TestHelpers.invoice

@SpringBootTest
@AutoConfigureMockMvc
@Unroll

class AbstractControllerTest extends Specification {
    @Autowired
    protected MockMvc mockMvc

    @Autowired
    protected JsonService jsonService

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

    protected static final String ENDPOINT = "/invoices"
    protected static final String TAX_CALCULATOR_ENDPOINT = "/tax"

    int addInvoiceAndReturnId(String invoiceAsJson) {
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

    protected ResultActions deleteInvoice(int id) {
        mockMvc.perform(delete("$ENDPOINT/$id"))
                .andExpect(status().isNoContent())

    }

    protected List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get("$ENDPOINT"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.toJavaObject(response, Invoice[])
    }


    protected List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(jsonService.toJsonObject(invoice))

            return invoice
        }
    }

    protected Invoice getInvoiceById(int id) {
        def invoiceAsString = mockMvc.perform(get("$ENDPOINT/$id")).andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toJavaObject(invoiceAsString, Invoice)
    }


    protected String invoiceAsJson(int id) {
        jsonService.toJsonObject(invoice(id))
    }

    protected TaxCalculatorResult calculateTax(String taxIdentificationNumber) {
        def result = mockMvc.perform(get("$TAX_CALCULATOR_ENDPOINT/$taxIdentificationNumber"))
                .andExpect((status().isOk()))
                .andReturn()
                .response
                .contentAsString

        jsonService.toJavaObject(result, TaxCalculatorResult);
    }
}
