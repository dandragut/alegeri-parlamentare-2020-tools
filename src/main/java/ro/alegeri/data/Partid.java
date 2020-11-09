package ro.alegeri.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum Partid {
    PARTIDUL_ALIANTA_PENTRU_UNIREA_ROMANILOR("AUR",      "ALIANȚA PENTRU UNIREA ROMÂNILOR",                 "Partidul Alianța pentru Unirea Românilor",             "https://partidulaur.ro",               "https://www.facebook.com/partidulAUR/"),
    ALIANTA_USR_PLUS                        ("USR-PLUS", "ALIANȚA USR PLUS",                                "Alianța USR PLUS",                                     "https://usrplus.ro",                   "https://www.facebook.com/alianta2020usrplus/"),
    PARTIDUL_ECOLOGIST_ROMAN                ("PER",      "PARTIDUL ECOLOGIST ROMÂN",                        "Partidul Ecologist Român",                             "https://per.ro",                       "https://www.facebook.com/PartidulEcologistRoman/"),
    PARTIDUL_MISCAREA_POPULARA              ("PMP",      "PARTIDUL MIȘCAREA POPULARĂ",                      "Partidul Mișcarea Populară",                           "https://pmponline.ro",                 "https://www.facebook.com/PartidulMiscareaPopulara.RO/"),
    PARTIDUL_NATIONAL_LIBERAL               ("PNL",      "PARTIDUL NAȚIONAL LIBERAL",                       "Partidul Național Liberal",                            "https://pnl.ro",                       "https://www.facebook.com/pnl.ro"),
    PARTIDUL_NATIUNEA_ROMANA                ("PNR",      "PARTIDUL NAȚIUNEA ROMÂNĂ",                        "Partidul Națiunea Română",                             "https://www.pnro.ro",                  null),
    PARTIDUL_NOUA_DREAPTA                   ("PND",      "PARTIDUL NOUA DREAPTĂ",                           "Partidul Noua Dreaptă",                                "https://nouadreapta.org",              null),
    PARTIDUL_NOUA_ROMANIE                   ("PNR",      "PARTIDUL NOUA ROMÂNIE",                           "Partidul Noua Românie",                                "https://partidulnouaromanie.ro",       "https://www.facebook.com/nouaromaniePNR/"),
    PARTIDUL_PRO_ROMANIA                    ("PRO",      "PARTIDUL PRO ROMÂNIA",                            "Partidul PRO Romania",                                 "https://proromania.ro",                "https://www.facebook.com/ProRomaniaOficial/"),
    PARTIDUL_PUTERII_UMANISTE               ("PPU",      "PARTIDUL PUTERII UMANISTE (SOCIAL-LIBERAL)",      "Partidul Puterii Umaniste (social - liberal)",         "https://ppusl.ro",                     "https://www.facebook.com/partidulputeriiumaniste/"),
    PARTIDUL_ROMANIA_MARE                   ("PAM",      "PARTIDUL ROMÂNIA MARE",                           "Partidul România Mare",                                null,                                   "https://www.facebook.com/PartidulRomaniaMare"),
    PARTIDUL_SOCIAL_DEMOCRAT                ("PSD",      "PARTIDUL SOCIAL DEMOCRAT",                        "Partidul Social Democrat",                             "https://psd.ro",                       "https://www.facebook.com/PartidulSocialDemocrat"),
    PARTIDUL_SOCIAL_DEMOCRAT_AL_MUNCITORILOR("PSDM",     "PARTIDUL SOCIAL DEMOCRAT AL MUNCITORILOR",        "Partidul Social Democrat al Muncitorilor",             "http://psdm.ro",                       "https://www.facebook.com/PSDM-Partidul-Social-Democrat-al-Muncitorilor-487955861284956/"),
    PARTIDUL_SOCIALIST_ROMAN                ("PSR",      "PARTIDUL SOCIALIST ROMÂN",                        "Partidul Socialist Român",                             "http://psr.org.ro",                    "https://www.facebook.com/OrganizatiaMunicipiuluiBucuresti/"),
    PARTIDUL_VERDE                          ("PV",       "PARTIDUL VERDE",                                  "Partidul Verde",                                       "https://partidulverde.ro",             "https://www.facebook.com/Partidul.Verde/"),
    UNIUNEA_DEMOCRATA_MAGHIARA              ("UDMR",     "UNIUNEA DEMOCRATĂ MAGHIARĂ DIN ROMÂNIA",          "Uniunea Democrată Maghiară din România",               "http://udmr.ro/",                      null)
    ;

    @Getter
    String cod;
    String numeExcel;
    @Getter
    String nume;
    @Getter
    String website;
    @Getter
    String facebook;

    public static Partid fromExcel(String numeExcel) {
        return Arrays.stream(values())
                .filter(partid -> partid.numeExcel.equalsIgnoreCase(numeExcel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Partid negasit dupa nume: %s", numeExcel)));
    }
}
