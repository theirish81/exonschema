package simonepezzano.exonschema;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ExonSimplifier {

    Schema schema;

    private int similarityRate;

    public ExonSimplifier(Schema schema){
        this(schema,3);
    }

    public ExonSimplifier(Schema schema,int similarityRate){
        this.schema = schema;
        this.similarityRate = similarityRate;
    }

    public void analyze(){
        analyze(schema);
    }

    public void analyze(Property property){
        if(property.hasItems() && property.getItems().anyOf != null) {
            Iterator<Property> iterator = property.getItems().anyOf.iterator();
            List<Property> toRemove = new LinkedList<>();
            HashSet<Property> toAdd = new HashSet<>();
            Property currentProp = iterator.next();
            while(iterator.hasNext()){
                Property prop2 = iterator.next();
                if(currentProp.hasProperties() && prop2.hasProperties() && ExonUtils.haveSimilarProps(currentProp,prop2,similarityRate) /*&& currentProp.getPropertiesKeys().equals(prop2.getPropertiesKeys())*/){
                    toRemove.add(currentProp);
                    toRemove.add(prop2);
                    currentProp = ExonUtils.merge(currentProp,prop2,similarityRate);
                    toAdd.add(currentProp);
                }
            }
            property.getItems().anyOf.removeAll(toRemove);
            if(toAdd.size()==1 && property.getItems().anyOf.size()==0) {
                property.setItems(toAdd.iterator().next());
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
}
