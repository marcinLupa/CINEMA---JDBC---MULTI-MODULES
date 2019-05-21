package json.impl;

import json.JsonConverter;
import model.LoyaltyCard;

import java.util.List;

public class LoyaltyCardListConverter extends JsonConverter<List<LoyaltyCard>> {
    public LoyaltyCardListConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
