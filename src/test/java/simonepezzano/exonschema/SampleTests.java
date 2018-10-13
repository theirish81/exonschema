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
        ExonWalker walker = new ExonWalker(data,"foo","bar");
        walker.analyze();
        org.everit.json.schema.Schema schema = SchemaLoader.load(new JSONObject(ExonUtils.serializeJsonPayload(walker.schema)));
        String text = ExonUtils.load(f);
        System.out.println(f.getName());
        if(text.startsWith("{"))
            schema.validate(new JSONObject(text));
        else
            schema.validate(new JSONArray(text));
        ExonSimplifier simplifier = new ExonSimplifier(walker.schema);
        simplifier.analyze();
        schema = SchemaLoader.load(new JSONObject(ExonUtils.serializeJsonPayload(simplifier.schema)));
        if(text.startsWith("{"))
            schema.validate(new JSONObject(text));
        else
            schema.validate(new JSONArray(text));
    }
}
