package ro.alegeri.data.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import ro.alegeri.data.Judet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@UtilityClass
public class Judete {
    /*
     * Constante
     */
    public final String JUDETE_JSON = "src/main/resources/judete.json";

    /**
     * Membri (interni)
     */
    public List<Judet> judete;

    /**
     * Lista judetelor
     * @return Lista judetelor
     */
    public List<Judet> judete() throws IOException {
        if (judete == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new ParanamerModule());
            judete = objectMapper.readValue(Paths.get(JUDETE_JSON).toFile(), new TypeReference<>() {});
        }
        return judete;
    }

    /**
     * Cauta un judet dupa nume
     * @return Judetul cautat sau null in caz in care nu a fost gasit.
     */
    public Judet judet(final String nume) throws IOException {
        return judete()
                .stream()
                .filter(judet -> StringUtils.equals(judet.getNume(), nume))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("\n~~~~~~~~~~~~~\n'%s' nu a fost gasit dupa nume in lista judetelor '%s'.\nCautarea este case-sensitive si diacritic-sensitive.\n~~~~~~~~~~~~~", nume, JUDETE_JSON)));
    }
}
