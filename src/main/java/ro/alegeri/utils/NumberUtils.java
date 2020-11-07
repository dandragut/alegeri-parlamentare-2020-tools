package ro.alegeri.utils;

import ro.alegeri.utils.StringUtils;

public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {
    public static Integer createInteger(final String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        // decode() handles 0xAABD and 0777 (hex and octal) as well.
        return Integer.decode(str);
    }
}
