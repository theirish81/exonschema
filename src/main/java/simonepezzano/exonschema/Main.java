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

import org.apache.commons.cli.*;
import org.checkerframework.checker.nullness.Opt;

import java.io.File;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("f").desc("Input file").required().hasArg().build());
        options.addOption(Option.builder("s").desc("Perform simplification").build());
        options.addOption(Option.builder("r").desc("Similarity rate").hasArg().build());
        CommandLineParser cmdParser = new DefaultParser();
        try {
            CommandLine commandLine = cmdParser.parse(options, args);
            ExonWalker exonWalker = new ExonWalker();
            File file = new File(commandLine.getOptionValue("f"));
            Schema schema = exonWalker.analyze(ExonUtils.deserializeJsonPayload(file), file.getPath(), file.getName());
            if(commandLine.hasOption("s")){
                int rate = 3;
                if(commandLine.hasOption("r"))
                    rate = Integer.valueOf(commandLine.getOptionValue("r"));
                ExonSimplifier exonSimplifier = new ExonSimplifier(rate);
                schema = exonSimplifier.analyze(schema);
            }
            System.out.println(ExonUtils.serializeJsonPayload(schema));
        }catch(ParseException e){
            new HelpFormatter().printHelp("exonschema",options);
        }
    }
}
