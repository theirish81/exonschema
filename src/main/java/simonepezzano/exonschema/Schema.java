package simonepezzano.exonschema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class Schema extends Property {

    private final String title;

    @JsonProperty("$schema")
    private final String schema = "http://json-schema.org/draft-07/schema#";

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
