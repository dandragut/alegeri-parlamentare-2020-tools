package ro.alegeri.data;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Functie {
    SENAT ("SENAT"),
    CDEP  ("CAMERA DEPUTAȚILOR");

    String functie;

    public static Functie fromExcel(String functieExcel) {
        return Arrays.stream(values())
                .filter(functie -> functie.functie.equalsIgnoreCase(functieExcel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Functie negasita dupa titlu: %s", functieExcel)));
    }
}