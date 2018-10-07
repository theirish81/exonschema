package simonepezzano.exonschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;

/**
 *
 */
public class ExonUtils {

    public static final String getType(Object data){
        final String name = data != null ? data.getClass().getSimpleName() : null;
        if(name == null)
            return "null";
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

    public static boolean isBaseType(String type){
        switch(type){
            case "boolean":
            case "integer":
            case "number":
            case "string":
                return true;
            default: return false;
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

    public static final String load(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = reader.readLine())!= null){
            sb.append(line+"\n");
        }
        return sb.toString();
    }
}
