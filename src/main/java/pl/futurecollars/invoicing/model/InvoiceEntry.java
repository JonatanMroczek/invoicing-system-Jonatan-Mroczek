package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InvoiceEntry {

    @ApiModelProperty(value = "Product/service description", required = true, example = "Xbox one")
    private String description;

    @ApiModelProperty(value = " Number of Products services", required = true, example = "2")
    private int quantity;

    @ApiModelProperty(value = "Product/service net price", required = true, example = "250.01")
    private BigDecimal price;

    @ApiModelProperty(value = "Tax value of product/service", required = true, example = "50.01")
    @Builder.Default
    private BigDecimal vatValue = BigDecimal.ZERO;

    @ApiModelProperty(value = "Tax rate", required = true, example = "VAT_23")
    private Vat vatRate;

    @ApiModelProperty(value = "Car this expense is related to, empty if expense is not related to car")
    private Car carExpenses;

}
