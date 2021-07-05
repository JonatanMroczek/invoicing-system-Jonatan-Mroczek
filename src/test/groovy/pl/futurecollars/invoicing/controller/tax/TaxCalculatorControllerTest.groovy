package pl.futurecollars.invoicing.controller.tax


import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Unroll

import static pl.futurecollars.invoicing.Helpers.TestHelpers.company

@Unroll

class TaxCalculatorControllerTest extends AbstractControllerTest {


    def "zeros should be returned when there are no invoices added"() {
        when:
        def taxCalculatorResponse = calculateTax("5")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.vatToReturn == 0


    }

    def "zero should be returned when there is no invoice with given id"() {
        given:
        addUniqueInvoices(5)

        when:
        def taxCalculatorResponse = calculateTax("6")


        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 0
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "sum of all products is returned for given id"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResponse = calculateTax("2")

        then:
        taxCalculatorResponse.income == 3000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 3000
        taxCalculatorResponse.collectedVat == 240.0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 240.0

        when:
        taxCalculatorResponse = calculateTax("11")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.incomeMinusCosts == -1000
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 80
        taxCalculatorResponse.vatToReturn == -80.0
    }

    def "correct sum is returned if company is buyer and seller"() {
        given:
        addUniqueInvoices(11)

        when:
        def taxCalculatorResponse = calculateTax("11")

        then:
        taxCalculatorResponse.income == 66000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.incomeMinusCosts == 65000
        taxCalculatorResponse.collectedVat == 5280.0
        taxCalculatorResponse.paidVat == 80.0
        taxCalculatorResponse.vatToReturn == 5200.0
    }


}
