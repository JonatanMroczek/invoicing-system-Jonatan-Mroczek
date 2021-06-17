package pl.futurecollars.invoicing.controller.tax


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import pl.futurecollars.invoicing.controller.AbstractControllerTest
import spock.lang.Unroll

@Unroll
@AutoConfigureMockMvc
class TaxCalculatorControllerTest extends AbstractControllerTest {


    def "zeros should be returned when there are no invoices added"() {
        when:
        def taxCalculatorResponse = calculateTax("5")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 0

    }

    def "zero should be returned when there is no invoice with given id"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax("6")


        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 0
    }

    def "sum of all products is returned for given id"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResponse = calculateTax("2")

        then:
        taxCalculatorResponse.income == 3000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 3000
        taxCalculatorResponse.incomingVat == 240.0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToPay == 240.0

        when:
        taxCalculatorResponse = calculateTax("11")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.earnings == -1000
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 80
        taxCalculatorResponse.vatToPay == -80.0
    }

    def "correct sum is returned if company is buyer and seller"() {
        given:
        addUniqueInvoices(11)

        when:
        def taxCalculatorResponse = calculateTax("11")

        then:
        taxCalculatorResponse.income == 66000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.earnings == 65000
        taxCalculatorResponse.incomingVat == 5280.0
        taxCalculatorResponse.outgoingVat == 80.0
        taxCalculatorResponse.vatToPay == 5200.0
    }


}
