package engine.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import message.Entities;
import message.EntitiesDeserializer;
import message.EntitiesSerializer;

public class ConditionalMapper extends ObjectMapper {
    public ConditionalMapper() {
        super();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Entities.class, new EntitiesDeserializer());
        module.addSerializer(Entities.class, new EntitiesSerializer());
        registerModule(module);
    }
}
