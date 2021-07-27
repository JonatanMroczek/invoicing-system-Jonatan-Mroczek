package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements Database<T> {

    private final Path databasePath;
    private final IdService idService;
    private final FilesService filesService;
    private final JsonService jsonService;
    private final Class<T> clazz;

    @Override
    public long save(T item) {
        item.setId(idService.getNextIdAndIncrement());
        try {
            filesService.appendLineToFile(databasePath, jsonService.toJsonObject(item));
            return item.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to save invoice", ex);

        }
    }

    @Override
    public Optional<T> getById(long id) {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> containsId(line, id))
                .map(line -> jsonService.toJavaObject(line, clazz))
                .findFirst();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to get item with id: " + id, ex);
        }
    }

    @Override
    public List<T> getAll() {

        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .map(line -> jsonService.toJavaObject(line, clazz))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read items from File", ex);

        }

    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        try {
            List<String> allInvoices = filesService.readAllLines(databasePath);
            var listWithoutInvoiceWithGivenId = allInvoices
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());

            updatedItem.setId(id);

            listWithoutInvoiceWithGivenId.add(jsonService.toJsonObject(updatedItem));

            filesService.writeLinesToFile(databasePath, listWithoutInvoiceWithGivenId);

            allInvoices.removeAll(listWithoutInvoiceWithGivenId);

            return allInvoices.isEmpty() ? Optional.empty() : Optional.of(jsonService.toJavaObject(allInvoices.get(0), clazz));

        } catch (IOException ex) {
            throw new RuntimeException("Failed to update invoice with id: " + id, ex);

        }

    }

    @Override
    public Optional<T> delete(long id) {
        try {
            var allItems = filesService.readAllLines(databasePath);

            var updatedList = allItems
                .stream()
                .filter(line -> !containsId(line, id))
                .collect(Collectors.toList());

            filesService.writeLinesToFile(databasePath, updatedList);

            allItems.removeAll(updatedList);

            return allItems.isEmpty() ? Optional.empty() : Optional.of(jsonService.toJavaObject(allItems.get(0), clazz));

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete item with id " + id);
        }
    }

    private boolean containsId(String line, long id) {
        return line.contains("{\"id\":" + id + ",");
    }

}
