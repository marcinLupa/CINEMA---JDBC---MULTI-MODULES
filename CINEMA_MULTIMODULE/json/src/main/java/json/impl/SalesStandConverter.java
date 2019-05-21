package json.impl;

import json.JsonConverter;
import model.SalesStand;

public class SalesStandConverter extends JsonConverter<SalesStand> {
    public SalesStandConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
