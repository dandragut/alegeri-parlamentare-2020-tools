package ro.alegeri.data.serializers;

import com.fasterxml.jackson.databind.util.StdConverter;
import ro.alegeri.utils.StringUtils;

/**
 * JSON serializer pentru nume cu conversia accentelor in litere de baza.
 */
public final class FlattenAccentsConverter extends StdConverter<String, String> {
    @Override
    public String convert(String string) {
        return StringUtils.flattenAccents(string);
    }
}