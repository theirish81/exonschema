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

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExonUtilsTests {

    @Test
    public void simplePropertyMergeTest(){
        Property p1 = new Property("#/p1","string","foobar");
        Property p2 = new Property("#/p1","string","dog");
        Property p3 = ExonUtils.merge(p1,p2);
        assertEquals("#/p1",p3.getId());
        assertEquals("string",p3.getType());
        assertTrue(p3.getExamples().contains("dog"));
        assertTrue(p3.getExamples().contains("foobar"));
    }

    @Test
    public void differentTypesPropertyMergeTest(){
        Property p1 = new Property("#/p1","string","foobar");
        Property p2 = new Property("#/p1","integer",22);
        Property p3 = ExonUtils.merge(p1,p2);
        assertEquals("#/p1",p3.getId());
        assertTrue(p3.typeEquals("string"));
        assertTrue(p3.typeEquals("integer"));
        assertTrue(p3.getExamples().contains("foobar"));
        assertTrue(p3.getExamples().contains(22));
    }

    @Test
    public void deeperPropertyMergeTestSymmetric(){
        Property p1 = new Property("#/p1","object",null);
        Property p2 = new Property("#/p1","object",null);
        p1.addChildProperty("p2",new Property("#/p1/properties/p2","string","foobar2"));
        p2.addChildProperty("p2",new Property("#/p1/properties/p2","string","dog2"));
        Property p3 = ExonUtils.merge(p1,p2);
        assertEquals("#/p1",p3.getId());
        assertEquals("object",p3.getType());
        assertEquals(1,p3.getProperties().size());
        assertEquals("#/p1/properties/p2",p3.getProperty("p2").getId());
        assertTrue(p3.getProperty("p2").getExamples().contains("foobar2"));
        assertTrue(p3.getProperty("p2").getExamples().contains("dog2"));
    }

    @Test
    public void deeperPropertyMergeTestAsymmetric1(){
        Property p1 = new Property("#/p1","object",null);
        Property p2 = new Property("#/p1","object",null);
        p1.addChildProperty("p2",new Property("#/p1/properties/p2","string","foobar2"));
        Property p3 = ExonUtils.merge(p1,p2);
        assertEquals("#/p1",p3.getId());
        assertEquals("object",p3.getType());
        assertEquals(1,p3.getProperties().size());
        assertEquals("#/p1/properties/p2",p3.getProperty("p2").getId());
    }

    @Test
    public void deeperPropertyMergeTestAsymmetric2(){
        Property p1 = new Property("#/p1","object",null);
        Property p2 = new Property("#/p1","object",null);
        p1.addChildProperty("p2",new Property("#/p1/properties/p2","string","foobar2"));
        p2.addChildProperty("p2",new Property("#/p1/properties/p2","integer",22));
        Property p3 = ExonUtils.merge(p1,p2);
        assertEquals("#/p1",p3.getId());
        assertEquals("object",p3.getType());
        assertEquals(1,p3.getProperties().size());
        assertEquals("#/p1/properties/p2",p3.getProperty("p2").getId());
        assertTrue(p3.getProperty("p2").typeEquals("integer"));
        assertTrue(p3.getProperty("p2").typeEquals("string"));
        assertTrue(p3.getProperty("p2").getExamples().contains("foobar2"));
        assertTrue(p3.getProperty("p2").getExamples().contains(22));
    }


}
