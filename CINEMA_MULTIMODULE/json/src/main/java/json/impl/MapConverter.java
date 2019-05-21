package json.impl;

import json.JsonConverter;
import java.util.Map;

public class MapConverter extends JsonConverter<Map> {
    public MapConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
