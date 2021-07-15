package pl.futurecollars.invoicing.Helpers

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelpers {
    static company(int id) {
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Jesionowa $id/1 80-250 Gdańsk, Polska")
                .name("Amper $id sp z o. o.")
                .pensionInsurance(BigDecimal.TEN * BigDecimal.valueOf(id).setScale(2))
                .healthInsurance(BigDecimal.valueOf(100) * BigDecimal.valueOf(id).setScale(2))
                .build()
    }

    static product(int id) {

        InvoiceEntry.builder()
                .description("Ozonowanie $id")
                .quantity(1)
                .netPrice(BigDecimal.valueOf(id * 1000).setScale(2))
                .vatValue(BigDecimal.valueOf(id * 1000 * 0.08).setScale(2))
                .vatRate(Vat.VAT_8)
                .build()

    }

    static invoice(int id) {
        Invoice.builder()
                .date(LocalDate.now())
                .buyer(company(id + 10))
                .seller(company(id))
                .entries((1..id).collect({ product(it) }))
                .build()
    }

}

