package message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

public class EntitiesSerializer extends JsonSerializer<Entities> {
    @Override
    public void serializeWithType(Entities entities, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSer) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeRaw(entities.entities().substring(1, entities.entities().length()-1));
        jsonGenerator.writeEndObject();
    }

    @Override
    public void serialize(Entities entities, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeRaw(entities.entities());
    }
}
