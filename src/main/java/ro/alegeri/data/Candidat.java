package ro.alegeri.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Candidat {
    @JsonIgnore
    Functie functie;
    @JsonIgnore
    Partid  partid;
    String  nume;
    Integer pozitie;
}