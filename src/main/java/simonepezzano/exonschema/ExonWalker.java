package simonepezzano.exonschema;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class ExonWalker {

    Schema schema;
    Object data;

    LinkedList<String> depthStack;

    public ExonWalker(Object data, String id,String title){
        this.data = data;
        schema = new Schema(id,title,ExonUtils.getType(data));
        depthStack = new LinkedList<>();
    }

    public void analyze(){
        analyze(data,schema);
    }

    protected Property analyze(Object item,Property currentElement){
        final String type = ExonUtils.getType(item);
        switch(type){
            case "object": {
                currentElement.required = getRequired((Map<String,Object>)item);
                Iterator<Map.Entry<String, Object>> iterator = ((Map<String, Object>) item).entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> it = iterator.next();
                    depthStack.addLast("/properties/"+it.getKey());
                    final String localType = ExonUtils.getType(it.getValue());
                    Property prop = new Property(stackToString(depthStack), localType, ExonUtils.getDefault(localType));
                    prop._value = it.getValue();
                    analyze(it.getValue(), prop);
                    currentElement.addChildProperty(it.getKey(), prop);
                    depthStack.removeLast();
                }
                return currentElement;
            }
            case "array": {
                List<Property> collectedItems = new LinkedList<>();
                Iterator iterator = ((List)item).iterator();
                int cnt = 0;
                while(iterator.hasNext()){
                    Object localItem = iterator.next();
                    final String localType = ExonUtils.getType(localItem);
                    depthStack.addLast("/items_"+cnt);
                    Property prop = new Property(stackToString(depthStack),localType,ExonUtils.getDefault(localType));
                    prop._value = localItem;
                    analyze(localItem,prop);
                    collectedItems.add(prop);
                    depthStack.removeLast();
                    cnt++;
                }
                List<Property> cont = detectDifferentProps(collectedItems);
                if(cont.size()==1){
                    currentElement.setItems(cont.get(0));
                } else{
                    Property anyOf = new Property();
                    anyOf.setAnyOf(cont);
                    currentElement.setItems(anyOf);
                }
                return currentElement;
            }
            default: {
                Set<Object> examples = new HashSet<>();
                examples.add(item);
                currentElement.setExamples(examples);
                return currentElement;
            }
        }
    }

    public static List<Property> detectDifferentProps(List<Property> props){
        List<Property> types = new LinkedList<>();
        types.add(props.get(0));
        Iterator<Property> iterator1 = props.iterator();
        while(iterator1.hasNext()){
            Property currentItem = iterator1.next();
            Iterator<Property> iterator2 = types.iterator();
            boolean compareSuccess = false;
            while(iterator2.hasNext()){
                Property savedItem = iterator2.next();
                if(currentItem.equals(savedItem)) {
                    compareSuccess = true;
                    if(ExonUtils.isBaseType(currentItem.type))
                        savedItem.addExamples(currentItem.examples);
                    else
                        savedItem.intersectRequires(currentItem.required);
                }
            }
            if(!compareSuccess)
                types.add(currentItem);
        }
        return types;
    }

    public static Set<String> getRequired(Map<String,Object> values){
        return values.entrySet().stream().filter( item -> item.getValue() != null ).map( item -> item.getKey()).collect(Collectors.toSet());
    }

    public static String stackToString(LinkedList<String> depthStack){
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        Iterator<String> iterator = depthStack.iterator();
        while(iterator.hasNext()){
            sb.append(iterator.next());
        }
        return sb.toString();
    }
}
