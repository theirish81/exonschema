package simonepezzano.exonschema;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Object data = ExonUtils.deserialize(new File("samples/mixed_arrays.json"));
        ExonWalker exonWalker = new ExonWalker(data,"the_id","the title");
        exonWalker.analyze();
        //System.out.println(ExonUtils.serialize(exonWalker.schema));
        ExonSimplifier exonRefinier = new ExonSimplifier(exonWalker.schema);
        exonRefinier.analyze();
        System.out.println(ExonUtils.serialize(exonRefinier.schema));

    }
}
