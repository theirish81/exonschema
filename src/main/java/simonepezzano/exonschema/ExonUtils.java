package simonepezzano.exonschema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Sets;

import java.io.*;
import java.util.*;

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

    public static boolean isBaseType(Object type){
        // Null is a base type
        if(type == null)
            return true;
        Set<String> types = new HashSet<>();
        //If the proposed type is a string
        if(type instanceof String)
            types.add(type.toString());
        else
            //If the proposed type is a collection of types
            types = (Set<String>) type;

        Iterator<String> iterator = types.iterator();
        while(iterator.hasNext()) {
            String t = iterator.next();
            switch (t) {
                // If it's a base type, we accept the base case, true
                case "boolean":
                case "integer":
                case "number":
                case "string":
                case "null":
                    break;
                default:
                    // In any other case, we immediately return false
                    return false;
            }
        }
        return true;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static final String serializeJsonPayload(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static final Object deserializeJsonPayload(File file) throws IOException {
        return objectMapper.readValue(file,Object.class);
    }

    public static final Object deserializeJsonPayload(String json) throws IOException {
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

    public static final Schema deserializeSchema(String schemaString) throws IOException {
        return objectMapper.readValue(schemaString,Schema.class);
    }

    public static final Schema deserializeSchema(File schema) throws IOException {
        return objectMapper.readValue(schema,Schema.class);
    }

    public static final Property deserializeProperty(String propertyString) throws IOException {
        return objectMapper.readValue(propertyString,Property.class);
    }

    public static Set<String> mergeTypes(Object t1, Object t2){
        Set<String> types = new HashSet<>();
        if(t1 instanceof String)
            types.add(t1.toString());
        else
            types.addAll((Collection<? extends String>) t1);
        if(t2 instanceof String)
            types.add(t2.toString());
        else
            types.addAll((Collection<? extends String>) t2);
        return types;
    }

    public static Property merge(Property prop1, Property prop2,int similarityRate){
        Property property = new Property(prop1.id,prop1.type,prop1.defaultValue);
        Iterator<String> iterator = prop1.getPropertiesKeys().iterator();
        Set<String> removeFromRequired = new HashSet<>();
        while(iterator.hasNext()){
            String key = iterator.next();
            Property child1 = prop1.getProperties().get(key);
            Property child2 = prop2.getProperties().get(key);
            // If the two children are basically the same. We pick one.
            if(child2 == null){
                property.addChildProperty(key,child1);
                removeFromRequired.add(key);
            } else
            if(child1.equals(child2))
                property.addChildProperty(key,child1);
            else {
                // If the two children are made of base types, we can merge them
                if(ExonUtils.isBaseType(child1.type) && ExonUtils.isBaseType(child2.type) && !child1.typeEquals(child2.type)){
                    Set<String> newType = ExonUtils.mergeTypes(child1.type,child2.type);
                    child1.type = newType;
                    property.addChildProperty(key,child1);
                }else {
                    // The two children represent different secenarios, then we do an anyOf
                    Property anyOf = new Property();
                    LinkedList<Property> props = new LinkedList<>();
                    props.add(child1);
                    props.add(child2);
                    anyOf.setAnyOf(props);
                    property.addChildProperty(key, anyOf);
                }
            }
        }
        // Composing the required field
        property.required = Sets.newHashSet(property.getPropertiesKeys());
        property.required.removeAll(removeFromRequired);
        return property;
    }

    public static boolean haveSimilarProps(Property property1, Property property2,int similarityRate){
        Set<String> props = Sets.intersection(property1.getPropertiesKeys(),property2.getPropertiesKeys());
        return props.size()>property1.getPropertiesKeys().size()/similarityRate && props.size()>property2.getPropertiesKeys().size()/similarityRate;
    }

}
