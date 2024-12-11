package message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class EntitiesDeserializer extends JsonDeserializer<Entities> {
    @Override
    public Entities deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectNode node = jsonParser.readValueAsTree();
        node.put("type", "Entities");

        return new Entities(node.toString());
    }
}
