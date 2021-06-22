package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
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

}
