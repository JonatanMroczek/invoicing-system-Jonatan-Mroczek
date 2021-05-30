package pl.futurecollars.invoicing.Helpers

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelpers {
    static company(int id) {
        new Company(("$id").repeat(10), "ul. Jesionowa $id/1 80-250 Gdańsk, Polska",
                "Amper $id sp z o. o.")
    }

    static product(int id) {
        new InvoiceEntry("Ozonowanie $id", 1, BigDecimal.valueOf(id * 100), BigDecimal.valueOf(id * 100 * 0.08), Vat.VAT_8)
    }

    static invoice(int id) {
        new Invoice(LocalDate.now(), company(id), company(id), List.of(product(id)))
    }

}

