package json.impl;

import json.JsonConverter;
import model.Movie;
import java.util.List;

public class MovieListConverter extends JsonConverter<List<Movie>> {
    public MovieListConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
