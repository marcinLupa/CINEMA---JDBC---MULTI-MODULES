package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.ExceptionCode;
import exceptions.MyException;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * class Json Converter is used for parsing from json documents text to objects of needed classes.
 * There is method toJson that is not used, but optionally you can used them to write same json example files.
 * In "impl" folder, there are implementations of JsonConverter for individual Json parsing type.
 *
 * @param <T> some class that is used in app
 */
public abstract class JsonConverter<T> {

    private final String jsonFilename;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public JsonConverter(String jsonFilename) {

        this.jsonFilename = jsonFilename;
    }

    public void toJson(final T element) {
        try (FileWriter fileWriter = new FileWriter(jsonFilename)) {
            if (element == null) {
                throw new MyException(ExceptionCode.JSON,"ELEMENT IS NULL");
            }
            gson.toJson(element, fileWriter);
        } catch (Exception e) {
            throw new MyException(ExceptionCode.JSON, e.getMessage());
        }
    }

    /**
     * @return optional of some class object
     */

    public Optional<T> fromJson() {
        try (FileReader fileReader = new FileReader(jsonFilename)) {
            return Optional.of(gson.fromJson(fileReader, type));
        } catch (Exception e) {
            throw new MyException(ExceptionCode.JSON, e.getMessage());
        }
    }


}