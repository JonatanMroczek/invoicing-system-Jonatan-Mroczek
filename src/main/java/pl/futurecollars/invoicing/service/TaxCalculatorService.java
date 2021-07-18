package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

    private final Database <Invoice> database;

    public BigDecimal income(String taxIdentificationNumber) {
        return visit(InvoiceEntry::getNetPrice, sellerPredicate(taxIdentificationNumber));
    }

    public BigDecimal costs(String taxIdentificationNumber) {
        return visit(this::getIncomeValueIncludingCarExpenses, buyerPredicate(taxIdentificationNumber));
    }

    public BigDecimal collectedVat(String taxIdentificationNumber) {

        return visit(InvoiceEntry::getVatValue, sellerPredicate(taxIdentificationNumber));
    }

    public BigDecimal paidVat(String taxIdentificationNumber) {
        return visit(this::getVatValueIncludingCarExpenses, buyerPredicate(taxIdentificationNumber));
    }

    private BigDecimal getVatValueIncludingCarExpenses(InvoiceEntry invoiceEntry) {
        return Optional.ofNullable(invoiceEntry.getCarExpenses())
            .map(Car::isPersonalUse)
            .map(personalCarUsage -> personalCarUsage ? BigDecimal.valueOf(2, 0) : BigDecimal.ONE)
            .map(proportion -> invoiceEntry.getVatValue().divide(proportion))
            .map(value -> value.setScale(2, RoundingMode.FLOOR))
            .orElse(invoiceEntry.getVatValue());
    }

    private BigDecimal getIncomeValueIncludingCarExpenses(InvoiceEntry invoiceEntry) {
        return invoiceEntry.getNetPrice()
            .add(invoiceEntry.getVatValue())
            .subtract(getVatValueIncludingCarExpenses(invoiceEntry));
    }

    public BigDecimal getEarnings(String taxIdentificationNumber) {
        return income(taxIdentificationNumber).subtract(costs(taxIdentificationNumber));
    }

    public BigDecimal getVatToReturn(String taxIdentificationNumber) {
        return collectedVat(taxIdentificationNumber).subtract(paidVat(taxIdentificationNumber));
    }

    public TaxCalculatorResult calculateTaxes(Company company) {
        String taxIdentificationNumber = company.getTaxIdentificationNumber();
        BigDecimal incomeMinusCosts = getEarnings(taxIdentificationNumber);
        BigDecimal incomeMinusCostsMinusPensionInsurance = incomeMinusCosts.subtract(company.getPensionInsurance());
        BigDecimal incomeMinusCostsMinusPensionInsuranceRounded = incomeMinusCostsMinusPensionInsurance.setScale(0, RoundingMode.HALF_UP);
        BigDecimal incomeTax = incomeMinusCostsMinusPensionInsuranceRounded.multiply(BigDecimal.valueOf(19, 2));
        BigDecimal healthInsuranceToSubtract =
            company.getHealthInsurance().multiply(BigDecimal.valueOf(775)).divide(BigDecimal.valueOf(900), RoundingMode.HALF_UP);
        BigDecimal incomeTaxMinusHealthInsurance = incomeTax.subtract(healthInsuranceToSubtract);

        return TaxCalculatorResult.builder()
            .income(income(taxIdentificationNumber))
            .costs(costs(taxIdentificationNumber))
            .incomeMinusCosts(incomeMinusCosts)
            .pensionInsurance(company.getPensionInsurance())
            .incomeMinusCostsMinusPensionInsurance(incomeMinusCostsMinusPensionInsurance)
            .incomeMinusCostsMinusPensionInsuranceRounded(incomeMinusCostsMinusPensionInsuranceRounded)
            .incomeTax(incomeTax)
            .healthInsurancePaid(company.getHealthInsurance())
            .healthInsuranceToSubtract(healthInsuranceToSubtract)
            .incomeTaxMinusHealthInsurance(incomeTaxMinusHealthInsurance)
            .finalIncomeTax(incomeTaxMinusHealthInsurance.setScale(0, RoundingMode.DOWN))

            .paidVat(paidVat(taxIdentificationNumber))
            .collectedVat(collectedVat(taxIdentificationNumber))
            .vatToReturn(getVatToReturn(taxIdentificationNumber))
            .build();

    }

    private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
        return invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxIdentificationNumber);
    }

    private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
        return invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxIdentificationNumber);
    }
    private BigDecimal visit(Function<InvoiceEntry, BigDecimal> invoiceEntriesToSum, Predicate<Invoice> invoicePredicate) {
        return database.getAll().stream()
            .filter(invoicePredicate)
            .flatMap(invoice -> invoice.getEntries().stream())
            .map(invoiceEntriesToSum)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}


