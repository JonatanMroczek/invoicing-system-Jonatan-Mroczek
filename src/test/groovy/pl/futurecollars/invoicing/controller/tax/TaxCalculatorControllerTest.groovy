package pl.futurecollars.invoicing.controller.tax

import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.model.*
import spock.lang.Unroll

import java.time.LocalDate

import static pl.futurecollars.invoicing.Helpers.TestHelpers.company

@Unroll

class TaxCalculatorControllerTest extends AbstractControllerTest {


    def "zeros should be returned when there are no invoices added"() {
        when:
        def taxCalculatorResponse = calculateTax(company(5))

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
        def taxCalculatorResponse = calculateTax(company(6))


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
        def taxCalculatorResponse = calculateTax(company(2))

        then:
        taxCalculatorResponse.income == 3000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 3000
        taxCalculatorResponse.collectedVat == 240.0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 240.0

        when:
        taxCalculatorResponse = calculateTax(company(11))

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
        def taxCalculatorResponse = calculateTax(company(11))

        then:
        taxCalculatorResponse.income == 66000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.incomeMinusCosts == 65000
        taxCalculatorResponse.collectedVat == 5280.0
        taxCalculatorResponse.paidVat == 80.0
        taxCalculatorResponse.vatToReturn == 5200.0
    }

    def "tax is calculated correctly when car is used for personal purposes"() {
        given:
        def invoice = Invoice.builder()
                .date(LocalDate.now())
                .number("12/2225")
                .seller(company(1))
                .buyer(company(2))
                .entries(List.of(
                        InvoiceEntry.builder()
                                .vatValue(BigDecimal.valueOf(23.45))
                                .vatRate(Vat.VAT_8)
                                .netPrice(BigDecimal.valueOf(100))
                                .description("description")
                                .quantity(BigDecimal.ONE)
                                .carExpenses(
                                        Car.builder()
                                                .personalUse(true)
                                                .registrationNumber("KTA 5568Y")
                                                .build()
                                )
                                .build()
                ))
                .build()

        addInvoiceAndReturnId(invoice)

        when:
        def taxCalculatorResponse = calculateTax(invoice.getSeller())

        then: "no proportion - it applies only when you are the buyer"
        taxCalculatorResponse.income == 100
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.incomeMinusCosts == 100
        taxCalculatorResponse.collectedVat == 23.45
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 23.45

        when:
        taxCalculatorResponse = calculateTax(invoice.getBuyer())

        then: "proportion applied - it applies when you are the buyer"
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 111.73
        taxCalculatorResponse.incomeMinusCosts == -111.73
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 11.72
        taxCalculatorResponse.vatToReturn == -11.72
    }

    def "All calculations are executed correctly"() {
        given:
        def ourCompany = Company.builder()
                .taxIdentificationNumber("1234")
                .address("address")
                .name("name")
                .pensionInsurance(BigDecimal.valueOf(514.57))
                .healthInsurance(319.94)
                .build()

        def invoiceWithIncome = Invoice.builder()
                .seller(ourCompany)
                .date(LocalDate.now())
                .number("number")
                .buyer(company(2))
                .entries(List.of(
                        InvoiceEntry.builder()
                                .vatRate(Vat.VAT_8)
                                .vatValue(0.0)
                                .quantity(BigDecimal.ONE)
                                .description("description")
                                .netPrice(76011.62)
                                .build()
                ))
                .build()

        def invoiceWithCosts = Invoice.builder()
                .seller(company(4))
                .date(LocalDate.now())
                .number("numbers")
                .buyer(ourCompany)
                .entries(List.of(
                        InvoiceEntry.builder()
                                .vatRate(Vat.VAT_8)
                                .vatValue(0.0)
                                .quantity(BigDecimal.ONE)
                                .description("description")
                                .netPrice(11329.47)
                                .build()
                ))
                .build()

        addInvoiceAndReturnId(invoiceWithIncome)
        addInvoiceAndReturnId(invoiceWithCosts)

        when:
        def taxCalculatorResponse = calculateTax(ourCompany)

        then:
        with(taxCalculatorResponse) {
            income == 76011.62
            costs == 11329.47
            incomeMinusCosts == 64682.15
            pensionInsurance == 514.57
            incomeMinusCostsMinusPensionInsurance == 64167.58
            incomeMinusCostsMinusPensionInsuranceRounded == 64168
            incomeTax == 12191.92
            healthInsurancePaid == 319.94
            healthInsuranceToSubtract == 275.50
            incomeTaxMinusHealthInsurance == 11916.42
            finalIncomeTax == 11916

            collectedVat == 0
            paidVat == 0
            vatToReturn == 0
        }
    }

}
