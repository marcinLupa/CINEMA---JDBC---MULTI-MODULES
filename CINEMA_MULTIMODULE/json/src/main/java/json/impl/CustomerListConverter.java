package json.impl;



import json.JsonConverter;
import model.Customer;

import java.util.List;

public class CustomerListConverter extends JsonConverter<List<Customer>> {
    public CustomerListConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
