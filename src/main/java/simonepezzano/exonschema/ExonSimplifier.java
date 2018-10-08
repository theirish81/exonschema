package simonepezzano.exonschema;

import java.util.*;

/**
 *
 */
public class ExonSimplifier {

    Schema schema;

    public ExonSimplifier(Schema schema){
        this.schema = schema;
    }

    public void analyze(){
        analyze(schema);
    }

    public void analyze(Property property){
        if(property.hasItems() && property.getItems().anyOf != null) {
            Iterator<Property> iterator = property.getItems().anyOf.iterator();
            List<Property> toRemove = new LinkedList<>();
            List<Property> toAdd = new LinkedList<>();
            Property currentProp = iterator.next();
            while(iterator.hasNext()){
                Property prop2 = iterator.next();
                if(currentProp.hasProperties() && prop2.hasProperties() && currentProp.getPropertiesKeys().equals(prop2.getPropertiesKeys())){
                    toRemove.add(currentProp);
                    toRemove.add(prop2);
                    currentProp = merge(currentProp,prop2);
                    toAdd.add(currentProp);
                }
            }
            property.getItems().anyOf.removeAll(toRemove);
            if(toAdd.size()==1 && property.getItems().anyOf.size()==0) {
                property.setItems(toAdd.get(0));
            }
            else
                property.getItems().anyOf.addAll(toAdd);
        } else{
            if(property.getProperties()!=null) {
                Iterator<Property> iterator = property.getProperties().values().iterator();
                while (iterator.hasNext()) {
                    analyze(iterator.next());
                }
            }
        }

    }

    public static Property merge(Property prop1, Property prop2){
        Property property = new Property(prop1.id,prop1.type,prop1.defaultValue);
        Iterator<String> iterator = prop1.getPropertiesKeys().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Property child1 = prop1.getProperties().get(key);
            Property child2 = prop2.getProperties().get(key);
            // If the two children are basically the same. We pick one.
            if(child1.equals(child2))
                property.addChildProperty(key,child1);
            else {
                // If the two children are made of base types, we can merge them
                if(ExonUtils.isBaseType(child1.type) && ExonUtils.isBaseType(child2.type) && !child1.typeEquals(child2.type)){
                    Set<String> newType = mergeTypes(child1.type,child2.type);
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
        property.required = property.getPropertiesKeys();
        return property;
    }

    /**
     * Merges types into a set
     * @param t1 type 1
     * @param t2 type 2
     * @return the merged set
     */
    private static Set<String> mergeTypes(Object t1, Object t2){
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
}
