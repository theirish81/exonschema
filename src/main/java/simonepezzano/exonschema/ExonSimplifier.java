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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ExonSimplifier {

    private int similarityRate;

    public ExonSimplifier(int similarityRate){
        super();
        this.similarityRate = similarityRate;
    }

    public ExonSimplifier(){
        this(3);
    }


    public Schema analyze(Schema schema){
        analyzeProperty(schema);
        return schema;
    }

    private void analyzeProperty(Property property){
        /*
         * If the property's "items" field has an anyOf combinatory field.
         * This means multiple scenarios apply, and we want to check whether some of these can be merged
         */
        if(property.hasItems() && property.getItems().hasAnyOf()) {
            // For each scenario...
            Iterator<Property> iterator = property.getItems().getAnyOf().iterator();
            List<Property> toRemove = new LinkedList<>();
            HashSet<Property> toAdd = new HashSet<>();
            // We first take one sample property...
            Property currentProp = iterator.next();
            // ... and iterate through the others
            while(iterator.hasNext()){
                Property prop2 = iterator.next();
                // If both properties sub-properties that look "similar"
                if(currentProp.hasProperties() && prop2.hasProperties() && ExonUtils.haveSimilarProps(currentProp,prop2,similarityRate)){
                    // We mark both for removal
                    toRemove.add(currentProp);
                    toRemove.add(prop2);
                    // And create a merged version of them to be added
                    currentProp = ExonUtils.merge(currentProp,prop2,similarityRate);
                    toAdd.add(currentProp);
                }
            }
            // Remove the items marked for removal
            property.getItems().getAnyOf().removeAll(toRemove);
            /*
             * If the items to add are one, and the anyOf has been left empty, it means we are left with one
             * scenario and the anyOf is not useful anymore, so we can set the one scenario to "items"
             */
            if(toAdd.size()==1 && property.getItems().getAnyOf().size()==0) {
                property.setItems(toAdd.iterator().next());
            }
            else
                // Otherwise we can add all the sceanrios to anyOf
                property.getItems().getAnyOf().addAll(toAdd);
          // If this had no anyOf, it means this property has, at best, regular child properties
        } else{
            // If it has regular properties
            if(property.hasProperties()) {
                // We can dig into them in a recursive fashion
                Iterator<Property> iterator = property.getProperties().values().iterator();
                while (iterator.hasNext()) {
                    analyzeProperty(iterator.next());
                }
            }
        }

    }
}
