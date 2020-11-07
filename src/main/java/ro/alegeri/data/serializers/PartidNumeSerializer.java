package ro.alegeri.data.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ro.alegeri.data.Partid;

import java.io.IOException;

public class PartidNumeSerializer extends JsonSerializer<Partid> {
    @Override
    public void serialize(Partid partid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(partid.getNume());
    }
}
