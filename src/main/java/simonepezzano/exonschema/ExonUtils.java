package simonepezzano.exonschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ExonUtils {

    public static final String getType(Object data){
        final String name = data.getClass().getSimpleName();
        switch(name){
            case "Boolean":
                return "boolean";
            case "Integer":
                return "integer";
            case "Float":
            case "Double":
                return "number";
            case "String":
                return "string";
            case "Map":
            case "LinkedHashMap":
                return "object";
            case "ArrayList":
                return "array";
            default:
                return "string";
        }
    }

    public static Object getDefault(String type){
        switch (type){
            case "boolean": return false;
            case "integer": return 0;
            case "number": return 0.0;
            case "string": return "";
            default: return null;
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static final String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static final Object deserialize(File file) throws IOException {
        return objectMapper.readValue(file,Object.class);
    }

    public static final Object deserialize(String json) throws IOException {
        return objectMapper.readValue(json,Object.class);
    }
}
