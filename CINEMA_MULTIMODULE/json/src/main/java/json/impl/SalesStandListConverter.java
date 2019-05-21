package json.impl;

import json.JsonConverter;
import model.SalesStand;

import java.util.List;

public class SalesStandListConverter extends JsonConverter<List<SalesStand>> {
    public SalesStandListConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
