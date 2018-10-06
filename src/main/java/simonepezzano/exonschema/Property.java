package simonepezzano.exonschema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property implements Comparable<Property>{

    @JsonProperty("$id")
    String id;

    String type;

    Object defaultValue;

    List<Object> examples;

    List<Property> anyOf;

    @JsonIgnore
    transient String _name;

    public Property(){
        examples = new LinkedList<>();
    }

    public Property(String type, Object defaultValue){
        this(null,type,defaultValue);
    }

    public Property(String id, String type, Object defaultValue){
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public void setName(String name){
        this._name = name;
    }

    public void setId(String id){
        this.id = id;
    }


    public int compareTo(Property property){
        return this.type.equals(property.type) ? 0 : -1;
    }

    public void setAnyOf(List<Property> anyOf){
        this.anyOf = anyOf;
    }

    public List<Property> getAnyOf(){
        return anyOf;
    }
    public String getType(){
        return type;
    }
    public List getExamples(){
        return examples;
    }

    public Object getDefaultValue(){
        return defaultValue;
    }

    public void setExamples(List<Object> examples){
        this.examples = examples;
    }

    private Map<String,Property> properties;

    private Property items;

    private Map<String,Property> initAndGetProperties(){
        if(properties == null)
            properties = new HashMap<>();
        return getProperties();
    }
    public void setItems(Property items){
        this.items = items;
    }
    public Map<String,Property> getProperties(){
        return properties;
    }

    public void addChildProperty(String key, Property property) {
        initAndGetProperties().put(key, property);
    }

    public Property getItems(){
        return items;
    }

    public boolean equals(Object obj){
        if(obj instanceof Property) {
            Property otherProp = (Property) obj;
            boolean eq = this.compareTo(otherProp) == 0;
            if(eq){
                if(this.getProperties() != null && otherProp.getProperties() != null){
                    if(this.getProperties().keySet().equals(otherProp.getProperties().keySet())){
                        Iterator<String> iterator = this.getProperties().keySet().iterator();
                        while(iterator.hasNext()){
                            String key = iterator.next();
                            if(!this.getProperties().get(key).equals(otherProp.getProperties().get(key)))
                                return false;
                        }
                    } else return false;
                }
                return true;
            }
        }
        return false;
    }

}
