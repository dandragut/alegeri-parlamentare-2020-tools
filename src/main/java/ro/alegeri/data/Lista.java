package ro.alegeri.data;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class Lista {
    Partid partid;
    List<Candidat> candidati;
}
