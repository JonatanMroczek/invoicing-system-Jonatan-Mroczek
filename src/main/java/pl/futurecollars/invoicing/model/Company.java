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
public class Company {

    @ApiModelProperty(value = "Tax identification number", required = true, example = "555-555-55-55")
    private String taxIdentificationNumber;

    @ApiModelProperty(value = "Company address", required = true, example = "ul. Wilcza 55, 01-180 Warszawa")
    private String address;

    @ApiModelProperty(value = "Company name", required = true, example = "Invoicing company z o. o.")
    private String name;

    @Builder.Default
    @ApiModelProperty(value = "Pension insurance amount", required = true, example = "1328.75")
    private BigDecimal healthInsurance = BigDecimal.ZERO;

    @Builder.Default
    @ApiModelProperty(value = "Health insurance amount", required = true, example = "458.34")
    private BigDecimal pensionInsurance = BigDecimal.ZERO;


}
