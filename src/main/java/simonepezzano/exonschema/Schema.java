package simonepezzano.exonschema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class Schema extends Property {

    List<String> definitions = new LinkedList<>();

    String type;

    String title;

    @JsonProperty("$schema")
    String schema = "http://json-schema.org/draft-07/schema#";

    public Schema(){
        super();
    }

    public Schema(String id, String title){
        this.id = id;
        this.title = title;
    }

}
