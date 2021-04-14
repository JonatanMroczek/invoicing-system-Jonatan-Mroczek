package pl.futurecollars.invoicing.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.futurecollars.invoicing.model.Invoice;

public class JsonService {

    private final ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    }

    public String toJsonObject(Invoice invoice) {
        try {
            return objectMapper.writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Problems encountered when processing (parsing, generating)");
        }
    }

    public Invoice toJavaObject(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Invoice.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("problems encountered when processing (parsing, generating)");
        }

    }

}

