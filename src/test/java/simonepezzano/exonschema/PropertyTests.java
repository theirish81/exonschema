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

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PropertyTests {

    @Test
    public void testCloneObject(){
        Property p1 = new Property("#/p1","object",null);
        p1.addChildProperty("p2",new Property("#/p1/properties/p2","integer",22));

        Property clonedP1 = p1.clone();
        assertTrue(clonedP1.equivalentTo(p1));
        assertFalse(clonedP1==p1);
        assertFalse(clonedP1.equals(p1));

        Property p2 = p1.getProperty("p2");
        Property clonedP2 = clonedP1.getProperty("p2");
        assertTrue(p2.equivalentTo(clonedP2));
        assertFalse(p2.equals(clonedP2));
        assertFalse(p2.equals(clonedP2));
    }

    @Test
    public void testTypeEquals(){
        Property p1 = new Property("#/p1","integer",1);
        assertTrue(p1.typeEquals("integer"));
        p1 = new Property("#/p1",Sets.newHashSet("integer","boolean"),true);
        assertTrue(p1.typeEquals("boolean"));
        assertTrue(p1.typeEquals("integer"));
    }
}
