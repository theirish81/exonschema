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

    Object type;

    Object defaultValue;

    Set<Object> examples;

    List<Property> anyOf;

    Set<String> required;

    @JsonIgnore
    transient  Object _value;

    public Property(){

    }

    public Property(String id, Object type, Object defaultValue){
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

    /**
     * Inits the examples collection, if necessary, and adds a set of examples
     * @param set a set of examples
     */
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

    public Object getType(){
        return type;
    }

    /**
     * @return true if the type is actually a single string
     */
    @JsonIgnore
    public boolean isSingleType(){
        return type != null ?  type instanceof String : true;
    }

    /**
     * @return the type as a string
     */
    @JsonIgnore
    public String getTypeAsString(){
        return (type != null) ? type.toString() : null;
    }

    /**
     * @return the type as a string
     */
    @JsonIgnore
    public Set<String> getTypeAsSet(){
        return (Set<String>) type;
    }

    /**
     * Verifies whether the proposed type is equal to the the type property, or is present in the type collection
     * @param type the type to verify
     * @return true when the proposed type is equal to the type property, ro is present in the type collection
     */
    public boolean typeEquals(Object type){
        /*
         * When the current type is null and the incoming type isn't, they are certainly different.
         * We probably ended up comparing an anyOf and another property.
         */
        if(this.type == null && type != null)
            return false;

        // Both single types, straight comparison
        if(isSingleType() && type instanceof String)
            return this.type.equals(type);
        else
            // Current isn't single type, but the other is. Check whether the provided type is in the current list
            if(!isSingleType() && type instanceof  String)
                return this.getTypeAsSet().contains(type);
            else
                // Comparing the two lists
                return this.type.equals(type);
    }

    /**
     * The crazy, crazy equals method. This method does not really represent equality, but a sufficient similarity
     * @param obj the object to compare to
     * @return true if the two objects are similar
     */
    public boolean equals(Object obj){
        if(obj instanceof Property) {
            Property otherProp = (Property) obj;

            // This happens when we are comparing anyOf properties
            if(this.type == null && otherProp.type == null)
                return true;

            boolean eq = this.typeEquals(otherProp.type);
            //If types are equal or compatible...
            if(eq){
                if(this.hasProperties() && otherProp.hasProperties()){
                    // If the names of the child properties are the same...
                    if(this.getPropertiesKeys().equals(otherProp.getPropertiesKeys())){
                        Iterator<String> iterator = this.getPropertiesKeys().iterator();
                        // For each property name...
                        while(iterator.hasNext()){
                            String key = iterator.next();
                            /*
                             * If the child property from the current object and the proposed object are
                             * not similar, then we can pretty much accept the two objects are not similar
                             */
                            if(!this.getProperties().get(key).equals(otherProp.getProperties().get(key)))
                                return false;
                        }
                        // If the child properties names are not the same
                    } else return false;
                }
                // If the current object and the proposed object have the "items" field
                if(this.hasItems() && otherProp.hasItems()){
                    // ... we verify whether the "items" fields are similar
                    return this.getItems().equals(otherProp.getItems());
                }
                // If the types are equal, but any other scenario does not trigger, we assume they are similar
                return true;
            }
        }
        // In any other case
        return false;
    }

    public void intersectRequires(Set<String> required){
        if(this.required != null && required != null)
            this.required = Sets.intersection(this.required,required);
    }

    public boolean hasProperties(){
        return properties != null && properties.size() > 0;
    }
    public boolean hasItems(){
        return items != null;
    }

    @JsonIgnore
    public Set<String> getPropertiesKeys(){
        if(hasProperties())
            return getProperties().keySet();
        else
            return new HashSet<>();
    }

    public int hashCode(){
        return id.hashCode();
    }

}
