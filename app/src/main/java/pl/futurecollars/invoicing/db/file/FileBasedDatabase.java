package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase implements Database {

    private final Path databasePath;
    private final IdService idService;
    private final FilesService filesService;
    private final JsonService jsonService;

    @Override
    public int save(Invoice invoice) {
        invoice.setId(idService.getNextIdAndIncrement());
        try {
            filesService.appendLineToFile(databasePath, jsonService.toJsonObject(invoice));
            return invoice.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to save invoice", ex);

        }
    }

    @Override
    public Optional<Invoice> getById(int id) {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> containsId(line, id))
                .map(jsonService::toJavaObject)
                .findFirst();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to get invoice with id: " + id, ex);
        }
    }

    @Override
    public List<Invoice> getAll() {

        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .map(jsonService::toJavaObject)
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read invoices from File", ex);

        }

    }

    @Override
    public void update(int id, Invoice updatedInvoice) {
        try {
            List<String> allInvoices = filesService.readAllLines(databasePath);
            var listWithoutInvoiceWithGivenId = allInvoices
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());
            if (allInvoices.size() == listWithoutInvoiceWithGivenId.size()) {
                throw new IllegalArgumentException("Id " + id + " does not exist");
            }
            updatedInvoice.setId(id);
            listWithoutInvoiceWithGivenId.add(jsonService.toJsonObject((updatedInvoice)));

            filesService.writeLinesToFile(databasePath, listWithoutInvoiceWithGivenId);

        } catch (IOException ex) {
            throw new RuntimeException("Failed to update invoice with id: " + id, ex);

        }

    }

    @Override
    public void delete(int id) {
        try {
            var updatedList = filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());
            filesService.writeLinesToFile(databasePath, updatedList);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean containsId(String line, int id) {
        return line.contains("\"id\":" + id + ",");
    }

}
