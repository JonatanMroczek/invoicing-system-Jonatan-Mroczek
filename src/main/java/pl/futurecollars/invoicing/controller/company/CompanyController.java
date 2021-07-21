package pl.futurecollars.invoicing.controller.company;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.CompanyService;

@RestController
@RequiredArgsConstructor
public class CompanyController implements CompanyApi {

    private final CompanyService companyService;

    @Override
    public List<Company> getAll() {
        return companyService.getAll();
    }

    @Override
    public long save(@RequestBody Company company) {
        return companyService.save(company);
    }

    @Override
    public ResponseEntity<Company> getById(@PathVariable int id) {
        return companyService.getById(id)
            .map(company -> ResponseEntity.ok(company))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> deleteById(@PathVariable int id) {
        return companyService.delete(id)
            .map(company -> ResponseEntity.noContent().build())
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Company company) {
        return companyService.update(id, company)
            .map(com -> ResponseEntity.noContent().build())
            .orElse(ResponseEntity.notFound().build());
    }

}
