package com.codepoetics.octarine.json.serialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class ReflectiveRecordSerialiser extends JsonSerializer<Record> {

    private static final ObjectMapper DEFAULT_MAPPER = mapperWith();

    public static ObjectMapper mapperWith(JsonSerializer<?>...extraSerialisers) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule", Version.unknownVersion());
        simpleModule.addSerializer(new ReflectiveRecordSerialiser());
        Stream.of(extraSerialisers).forEach(simpleModule::addSerializer);

        mapper.registerModules(simpleModule);

        return mapper;
    }

    public static String toJson(Record record) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(record);
    }

    public static String toJson(Record record, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(record);
    }

    public static String toJson(Collection<Record> records) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(records);
    }

    public static String toJson(Collection<Record> records, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(records);
    }

    public static String toJson(Map<String, Record> records) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(records);
    }

    public static String toJson(Map<String, Record> records, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(records);
    }

    @Override
    public Class<Record> handledType() { return Record.class; }

    @Override
    public void serialize(Record o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<Key<?>, Object> entry : o.values().entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().name());
            serializerProvider.defaultSerializeValue(entry.getValue(), jsonGenerator);
        }
        jsonGenerator.writeEndObject();
    }
}
