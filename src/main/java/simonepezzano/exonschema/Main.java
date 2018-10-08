package simonepezzano.exonschema;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Object data = ExonUtils.deserialize(new File("samples/nulls.json"));
        ExonWalker exonWalker = new ExonWalker(data,"the_id","the title");
        exonWalker.analyze();
        ExonSimplifier exonSimplifier = new ExonSimplifier(exonWalker.schema);
        exonSimplifier.analyze();
        System.out.println(ExonUtils.serialize(exonSimplifier.schema));

    }
}
