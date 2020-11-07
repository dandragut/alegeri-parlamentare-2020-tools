package ro.alegeri.utils;

import org.apache.commons.text.WordUtils;

import java.text.Normalizer;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * Flatten accented characters to their base characters
     * e.g. Primăria Brașov -> Primaria Brasov
     * @param string
     */
    public static String flattenAccents(final String string) {
        if (StringUtils.isBlank(string)) {
            return string;
        }
        return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }

    /**
     * Capitalizeaza numele unei institutii intr-o forma citibila.
     * @param nume e.g. "NEDELCU DARIUS-SEBASTIAN"
     * @return e.g. "Nedelcu Darius-Sebastian"
     */
    public static String capitalizeFully(String nume) {
        return WordUtils.capitalizeFully(trim(nume), ' ', '.', '-');
    }
}
