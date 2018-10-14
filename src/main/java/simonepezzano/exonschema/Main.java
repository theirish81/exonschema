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

import java.io.File;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {

        ExonWalker exonWalker = new ExonWalker();
        Schema schema = exonWalker.analyze(ExonUtils.deserializeJsonPayload(new File("samples/mixed_keys.json")),"the_id","the title");
        //System.out.println(ExonUtils.serializeJsonPayload(schema));
        System.out.println("------");
        ExonSimplifier exonSimplifier = new ExonSimplifier();
        System.out.println(ExonUtils.serializeJsonPayload(exonSimplifier.analyze(schema)));
    }
}
