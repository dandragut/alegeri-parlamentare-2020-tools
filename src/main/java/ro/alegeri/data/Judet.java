package ro.alegeri.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import ro.alegeri.data.serializers.FlattenAccentsConverter;

@Value
public class Judet {
    String cod;
    @JsonSerialize(converter = FlattenAccentsConverter.class)
    String nume;
}