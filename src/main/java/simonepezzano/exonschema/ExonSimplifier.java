package simonepezzano.exonschema;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        if(property.getItems() != null && property.getItems().anyOf != null) {
            Iterator<Property> iterator = property.getItems().anyOf.iterator();
            List<Property> toRemove = new LinkedList<>();
            List<Property> toAdd = new LinkedList<>();
            Property currentProp = iterator.next();
            while(iterator.hasNext()){
                Property prop2 = iterator.next();
                if(currentProp.hasProperties() && prop2.hasProperties() && currentProp.getProperties().keySet().equals(prop2.getProperties().keySet())){
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
        Iterator<String> iterator = prop1.getProperties().keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Property child1 = prop1.getProperties().get(key);
            Property child2 = prop2.getProperties().get(key);
            if(child1.equals(child2))
                property.addChildProperty(key,child1);
            else {
                Property anyOf = new Property();
                LinkedList<Property> props = new LinkedList<>();
                props.add(child1);
                props.add(child2);
                anyOf.setAnyOf(props);
                property.addChildProperty(key,anyOf);
            }
        }
        property.required = property.getProperties().keySet();
        return property;
    }
}
