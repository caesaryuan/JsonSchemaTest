package info.xiaoc.json.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonMetaSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.uri.ValidationContextAwareClasspathUriFactory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Objects;

public class SchemaLoadTest {

    @Test
    public void test() throws Exception {
        JsonNode node = getJsonNodeFromClasspath("schema/transaction.json");
        System.out.println(node);
    }

    @Test
    public void testSchema() throws Exception {
        JsonSchema schema = getJsonSchemaFromClasspath("schema/transaction.json");
        JsonNode node = getJsonNodeFromClasspath("invalid.json");
        System.out.println(schema.validate(node));
    }

    protected JsonNode getJsonNodeFromClasspath(String name) throws Exception {
        InputStream is1 = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(Objects.requireNonNull(is1));
    }

    protected JsonSchema getJsonSchemaFromClasspath(String name) {
        JsonSchemaFactory factory = new JsonSchemaFactory.Builder()
                .defaultMetaSchemaURI(JsonMetaSchema.getV4().getUri())
                .addMetaSchema(JsonMetaSchema.getV4())
                .uriFactory(new ValidationContextAwareClasspathUriFactory("new"), ValidationContextAwareClasspathUriFactory.SUPPORTED_SCHEMES)
                .build();
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name);
        return factory.getSchema(is);
    }

}
