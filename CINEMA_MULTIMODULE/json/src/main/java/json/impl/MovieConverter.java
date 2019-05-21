package json.impl;


import json.JsonConverter;
import model.Movie;

public class MovieConverter  extends JsonConverter<Movie> {
    public MovieConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
