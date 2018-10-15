/*
 * @author 2018 Simone Pezzano
 * ---
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package simonepezzano.exonschema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import java.util.*;

/**
 * The JsonSchema property
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {

    @JsonProperty("$id")
    private String id;

    private Object type;

    private Object defaultValue;

    private Set<Object> examples;

    private List<Property> anyOf;

    private Set<String> required;

    private Map<String,Property> properties;

    private Property items;

    /**
     * Base constructor. It initializes the ID as the hashCode method relies on it.
     * Mostly used by deserializers
     */
    public Property(){
        id = UUID.randomUUID().toString();
    }

    /**
     * Default constructor
     * @param id the ID of the property
     * @param type the type of the object
     * @param defaultValue its default value
     */
    public Property(String id, Object type, Object defaultValue){
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setAnyOf(List<Property> anyOf){
        this.anyOf = anyOf;
    }

    public List<Property> getAnyOf(){
        return anyOf;
    }

    public boolean hasAnyOf(){
        return anyOf != null;
    }

    public Set getExamples(){
        return examples;
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

    public void setRequired(Set<String> required){
        this.required = required;
    }

    public Set<String> getRequired() { return required; }

    public Object getDefaultValue(){
        return defaultValue;
    }

    /**
     * Inits the properties map if necessary, and returns it
     * @return the properties map
     */
    private Map<String,Property> initAndGetProperties(){
        if(properties == null)
            properties = new HashMap<>();
        return getProperties();
    }

    public Map<String,Property> getProperties(){
        return properties;
    }

    public void setProperties(Map<String,Property> properties){
        this.properties = properties;
    }

    /**
     * @return the keys of all child properties
     */
    @JsonIgnore
    public Set<String> getPropertiesKeys(){
        if(hasProperties())
            return getProperties().keySet();
        else
            return new HashSet<>();
    }

    public Property getProperty(String key){
        return getProperties().get(key);
    }

    /**
     * @return true if the properties map is not null and has items in it
     */
    public boolean hasProperties(){
        return properties != null && properties.size() > 0;
    }

    /**
     * Adds a child property
     * @param key the key of the property
     * @param property the property
     */
    public void addChildProperty(String key, Property property) {
        initAndGetProperties().put(key, property);
    }

    public void setItems(Property items){
        this.items = items;
    }

    public Property getItems(){
        return items;
    }

    /**
     * @return True if the items object is not null
     */
    public boolean hasItems(){
        return items != null;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getType(){
        return type;
    }


    /**
     * @return true if the type is actually a single string
     */
    @JsonIgnore
    public boolean isSingleType(){
        return type == null || type instanceof String;
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
     * The crazy, crazy equivalentTo method. This method does not really represent equality, but a sufficient similarity
     * @param obj the object to compare to
     * @return true if the two objects are similar
     */
    public boolean equivalentTo(Object obj){
        if(obj instanceof Property) {
            Property otherProp = (Property) obj;

            // This happens when we are comparing anyOf properties
            if(this.type == null && otherProp.type == null)
                return true;

            //If types are equal or compatible...
            if(this.typeEquals(otherProp.type)){
                if(this.hasProperties() && otherProp.hasProperties()){
                    // If the names of the child properties are the same...
                    if(this.getPropertiesKeys().equals(otherProp.getPropertiesKeys())){
                        // For each property name...
                        for(final String key : this.getPropertiesKeys()){
                            /*
                             * If the child property from the current object and the proposed object are
                             * not similar, then we can pretty much accept the two objects are not similar
                             */
                            if(!this.getProperty(key).equivalentTo(otherProp.getProperty(key)))
                                return false;
                        }
                        // If the child properties names are not the same
                    } else return false;
                }
                // If the current object and the proposed object have the "items" field
                if(this.hasItems() && otherProp.hasItems()){
                    // ... we verify whether the "items" fields are similar
                    return this.getItems().equivalentTo(otherProp.getItems());
                }
                // If the types are equal, but any other scenario does not trigger, we assume they are similar
                return true;
            }
        }
        // In any other case
        return false;
    }

    /**
     * Finds common items between this object's "required" set, and the proposed set of required keys
     * @param required a set of strings
     */
    public void intersectRequires(Set<String> required){
        if(this.required != null && required != null)
            this.required = Sets.intersection(this.required,required);
    }

    public int hashCode(){
        return id.hashCode();
    }

    public Schema asSchema(String title){
        Schema schema = new Schema(id,title,getTypeAsString());
        schema.setProperties(properties);
        return schema;
    }

}
