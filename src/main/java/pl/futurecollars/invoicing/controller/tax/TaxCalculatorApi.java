package pl.futurecollars.invoicing.controller.tax;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.futurecollars.invoicing.service.TaxCalculatorResult;

@RequestMapping("tax")
@Api(tags = {"Tax controller"})

public interface TaxCalculatorApi {

    @ApiOperation(value = "Get incomes, costs, vat and taxes to pay")
    @GetMapping(value = "/{taxIdentificationNumber}", produces = {"application/json;charset=UTF-8"})
    TaxCalculatorResult calculateTaxes(@PathVariable @ApiParam(example = "523-532-55-55") String taxIdentificationNumber);

}
