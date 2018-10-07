package simonepezzano.exonschema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import java.util.*;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {

    @JsonProperty("$id")
    String id;

    String type;

    Object defaultValue;

    Set<Object> examples;

    List<Property> anyOf;

    Set<String> required;

    @JsonIgnore
    transient  Object _value;

    public Property(){

    }

    public Property(String id, String type, Object defaultValue){
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public void setAnyOf(List<Property> anyOf){
        this.anyOf = anyOf;
    }

    public List<Property> getAnyOf(){
        return anyOf;
    }

    public Set getExamples(){
        return examples;
    }

    public Set<String> getRequired() { return required; }

    public Object getDefaultValue(){
        return defaultValue;
    }

    public void setExamples(Set<Object> examples){
        this.examples = examples;
    }
    public void addExamples(Set set){
        if(examples == null)
            examples = new HashSet<>();
        examples.addAll(set);
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

    public String getType(){
        return type;
    }

    public boolean equals(Object obj){
        if(obj instanceof Property) {
            Property otherProp = (Property) obj;
            if(this.type == null && otherProp.type == null)
                return true;
            boolean eq = this.type.equals(otherProp.type);
            if(eq){
                if(this.hasProperties() && otherProp.hasProperties()){
                    if(this.getProperties().keySet().equals(otherProp.getProperties().keySet())){
                        Iterator<String> iterator = this.getProperties().keySet().iterator();
                        while(iterator.hasNext()){
                            String key = iterator.next();
                            if(!this.getProperties().get(key).equals(otherProp.getProperties().get(key)))
                                return false;
                        }
                    } else return false;
                }
                if(this.getItems() != null && otherProp.getItems() != null){
                    return this.getItems().equals(otherProp.getItems());
                }
                return true;
            }
        }
        return false;
    }

    public void intersectRequires(Set<String> required){
        if(this.required != null && required != null)
            this.required = Sets.intersection(this.required,required);
    }

    public boolean hasProperties(){
        return properties != null && properties.size() > 0;
    }

}
