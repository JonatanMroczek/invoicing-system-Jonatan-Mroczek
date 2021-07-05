package pl.futurecollars.invoicing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Car {

    private String registrationNumber;
    private boolean personalUse;

}
