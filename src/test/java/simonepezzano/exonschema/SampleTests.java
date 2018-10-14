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

import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class SampleTests {

    @Test
    public void testValidationSamples() throws Exception {
        File[] files = new File("samples").listFiles((dir, name) -> name.endsWith(".json"));
        for(File f : files) {
            runTest(f);
        }
    }

    @Test
    public void testRealWorld() throws Exception {
        File[] files = new File("samples"+File.separator+"real_world").listFiles((dir, name) -> name.endsWith(".json"));
        for(File f : files) {
            runTest(f);
        }
    }

    private void runTest(File f) throws Exception {
        Object data = ExonUtils.deserializeJsonPayload(f);
        ExonWalker walker = new ExonWalker();
        Schema generatedSchema = walker.analyze(data,"foo","bar");
        org.everit.json.schema.Schema schema = SchemaLoader.load(new JSONObject(ExonUtils.serializeJsonPayload(generatedSchema)));
        String text = ExonUtils.load(f);
        System.out.println(f.getName());
        if(text.startsWith("{"))
            schema.validate(new JSONObject(text));
        else
            schema.validate(new JSONArray(text));
        ExonSimplifier simplifier = new ExonSimplifier();
        generatedSchema = simplifier.analyze(generatedSchema);
        schema = SchemaLoader.load(new JSONObject(ExonUtils.serializeJsonPayload(generatedSchema)));
        if(text.startsWith("{"))
            schema.validate(new JSONObject(text));
        else
            schema.validate(new JSONArray(text));
    }
}
