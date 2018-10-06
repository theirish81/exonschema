package simonepezzano.exonschema;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Object data = ExonUtils.deserialize(new File("samples/simple.json"));
        ExonWalker exonWalker = new ExonWalker(data,"the_id","the title");
        exonWalker.analyze();
        System.out.println(ExonUtils.serialize(exonWalker.schema));
    }
}
