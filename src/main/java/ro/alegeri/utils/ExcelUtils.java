package ro.alegeri.utils;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import ro.alegeri.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

@UtilityClass
public class ExcelUtils {
    public static String getCellValue(Row row, int cellnum) {
        // Cell value...
        final Cell cell = row.getCell(cellnum - 1);

        if (Objects.isNull(cell)) {
            return StringUtils.EMPTY;
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
            case STRING  -> cell.getRichStringCellValue().getString();
            case BLANK   -> null;
            default      -> throw new RuntimeException("THIS SHOULD NOT HAPPEN");
        };
    }
}
