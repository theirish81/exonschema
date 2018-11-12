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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.IOException;

/**
 * The JSON schema
 */
public class Schema extends Property {

    private String title;

    /**
     * We ideally always conform to draft-07
     */
    @JsonProperty("$schema")
    private final String schema = "http://json-schema.org/draft-07/schema#";

    /**
     * Base constructor. Mostly used by deserializers
     */
    public Schema(){
        super();
    }

    /**
     * Creates a schema from a JSON string
     * @param schemaString a JSON string representing a JsonSchema
     * @return a Schema
     * @throws IOException
     */
    public static Schema create(String schemaString) throws IOException {
        return ExonUtils.deserializeSchema(schemaString);
    }

    /**
     * Creates a schema froma JSON file
     * @param schema a file containing JSON representing a JsonSchema
     * @return a Schema
     * @throws IOException
     */
    public static Schema create(File schema) throws IOException {
        return ExonUtils.deserializeSchema(schema);
    }

    /**
     * Default constructor
     * @param id the id of the schema
     * @param title the title of the schema
     * @param type the type of the root object
     */
    public Schema(String id, String title,String type){
        this.setId(id);
        this.title = title;
        this.setType(type);
    }

    public String getTitle(){
        return title;
    }

    public String getSchema(){
        return schema;
    }

    public Schema clone(){
        return  super.clone().asSchema("schema");
    }

}
