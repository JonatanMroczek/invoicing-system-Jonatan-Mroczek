package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class InvoiceEntry {

    @ApiModelProperty(value = "Product/service description", required = true, example = "Xbox one")
    private String description;

    @ApiModelProperty(value = " Number of Products services", required = true, example = "2")
    private int quantity;

    @ApiModelProperty(value = "Product/service net price", required = true, example = "250.01")
    private BigDecimal price;

    @ApiModelProperty(value = "Tax value of product/service", required = true, example = "50.01")
    private BigDecimal vatValue;

    @ApiModelProperty(value = "Tax rate", required = true, example = "VAT_23")
    private Vat vatRate;

}


