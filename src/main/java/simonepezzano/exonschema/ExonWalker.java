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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Will generate a simple JsonSchema based on a sample data structure. The produced schema is quite simple
 * and branches in different scenarios for each difference found in non-homogeneous arrays
 */
public class ExonWalker {

    private LinkedList<String> depthStack;

    /**
     * Default constructor
     */
    public ExonWalker(){
        super();
    }

    /**
     * Analyzes a piece of data to generate a JSON schema
     * @param data a piece of data (maps and arrays)
     * @param id the ID to assign to the JSON schema
     * @param title the title of the schema
     * @return the generated JSON schema
     */
    public Schema analyze(Object data, String id, String title){

        depthStack = new LinkedList<>();

        Schema schema = new Schema(id,title,ExonUtils.determineType(data));

        analyze(data,schema);
        return schema;
    }

    /**
     * Recursive method to bring the analysis in depth
     * @param item the item of data being analyzed
     * @param currentElement the current element that will hold the analysis result
     * @return the analyzed property
     */
    protected Property analyze(Object item,Property currentElement){
        final String type = ExonUtils.determineType(item);
        switch(type){
            /*
             * If it's an object, walk its entries and make them child properties
             */
            case ExonUtils.SCHEMA_TYPE_OBJECT: {
                currentElement.setRequired(getRequired((Map<String,Object>)item));
                Iterator<Map.Entry<String, Object>> iterator = ((Map<String, Object>) item).entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> it = iterator.next();
                    depthStack.addLast("/properties/"+it.getKey());
                    final String localType = ExonUtils.determineType(it.getValue());
                    final Property prop = new Property(stackToString(depthStack), localType, ExonUtils.determineDefault(localType));
                    prop._value = it.getValue();
                    analyze(it.getValue(), prop);
                    currentElement.addChildProperty(it.getKey(), prop);
                    depthStack.removeLast();
                }
                return currentElement;
            }
            /*
             * If it's an array, walk down its items and make them properties for the "items" property
             */
            case ExonUtils.SCHEMA_TYPE_ARRAY: {
                List<Property> collectedItems = new LinkedList<>();
                Iterator iterator = ((List)item).iterator();
                int cnt = 0;
                // First, catalog all items a unique property
                while(iterator.hasNext()){
                    final Object localItem = iterator.next();
                    final String localType = ExonUtils.determineType(localItem);
                    depthStack.addLast("/items_"+cnt);
                    final Property prop = new Property(stackToString(depthStack),localType,ExonUtils.determineDefault(localType));
                    prop._value = localItem;
                    analyze(localItem,prop);
                    collectedItems.add(prop);
                    depthStack.removeLast();
                    cnt++;
                }
                // Empty array
                if(collectedItems.size()==0)
                    return currentElement;
                // Identify which properties are equivalent
                List<Property> cont = detectDifferentProps(collectedItems);

                // If only one scenario arises, set it as the "items" property
                if(cont.size()==1)
                    currentElement.setItems(cont.get(0));
                else{
                    // If multiple scenario arise, we add them to the "anyOf" property
                    final Property anyOf = new Property();
                    anyOf.setAnyOf(cont);
                    currentElement.setItems(anyOf);
                }
                return currentElement;
            }
            /*
             * Anything else is a base data time
             */
            default: {
                Set<Object> examples = new HashSet<>();
                examples.add(item);
                currentElement.setExamples(examples);
                return currentElement;
            }
        }
    }

    /**
     * Give a list of properties, detect which ones are equivalent
     * @param props a list of properties
     * @return a list of the essential properties
     */
    public static List<Property> detectDifferentProps(List<Property> props){
        List<Property> types = new LinkedList<>();
        types.add(props.get(0));
        /*
         * For each item of the props, if the current prop is not equivalent to a prop
         * previously collected, add it to the collected props
         */
        Iterator<Property> iterator1 = props.iterator();
        while(iterator1.hasNext()){
            final Property currentItem = iterator1.next();
            Iterator<Property> iterator2 = types.iterator();
            boolean compareSuccess = false;
            while(iterator2.hasNext()){
                final Property savedItem = iterator2.next();
                if(currentItem.equivalentTo(savedItem)) {
                    compareSuccess = true;
                    // if it's a base type
                    if(ExonUtils.isBaseType(currentItem.getType()))
                        // add the examples to the collected ones
                        savedItem.addExamples(currentItem.getExamples());
                    else
                        // otherwise find which requirements are shared
                        savedItem.intersectRequires(currentItem.getRequired());
                }
            } // if no similar item
            if(!compareSuccess)
                types.add(currentItem);
        }
        return types;
    }

    /**
     * Given a data object, collect all the keys to be used as a "required" field
     * @param values the data object
     * @return the keys
     */
    protected static Set<String> getRequired(Map<String,Object> values){
        return values.entrySet().stream().filter( item -> item.getValue() != null ).map( item -> item.getKey()).collect(Collectors.toSet());
    }

    /**
     * Transform a depthStack list to a string to be used as a property ID
     * @param depthStack a depthStack list
     * @return the generated ID
     */
    protected static String stackToString(LinkedList<String> depthStack){
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        Iterator<String> iterator = depthStack.iterator();
        while(iterator.hasNext()){
            sb.append(iterator.next());
        }
        return sb.toString();
    }
}
