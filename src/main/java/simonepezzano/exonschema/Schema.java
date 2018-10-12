package simonepezzano.exonschema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Schema extends Property {

    private String title;

    @JsonProperty("$schema")
    private final String schema = "http://json-schema.org/draft-07/schema#";

    public Schema(){
        super();
    }

    public static Schema create(String schemaString) throws IOException {
        return ExonUtils.deserializeSchema(schemaString);
    }
    public static Schema create(File schema) throws IOException {
        return ExonUtils.deserializeSchema(schema);
    }
    public Schema(String id, String title,String type){
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public String getTitle(){
        return title;
    }

    public String getSchema(){
        return schema;
    }

}
