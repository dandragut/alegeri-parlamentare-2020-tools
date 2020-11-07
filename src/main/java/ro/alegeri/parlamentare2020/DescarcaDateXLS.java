package ro.alegeri.parlamentare2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ro.alegeri.data.Candidat;
import ro.alegeri.data.Functie;
import ro.alegeri.data.Judet;
import ro.alegeri.data.Partid;
import ro.alegeri.data.utils.Judete;
import ro.alegeri.utils.ExcelUtils;
import ro.alegeri.utils.HttpClient;
import ro.alegeri.utils.NumberUtils;
import ro.alegeri.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class DescarcaDateXLS {
    private static final String       LOCATION_XLS_SENAT = "https://parlamentare2020.bec.ro/wp-content/uploads/2020/11/Senat_%s-2020-11-04.xlsx";
    private static final File         OUTPUT_DIR         = FileUtils.getFile("..", "alegeri-parlamentare-2020-web", "src", "json");
    private static final ObjectWriter JSON_WRITER_PRETTY = new ObjectMapper().writerWithDefaultPrettyPrinter();


    public static void main(String[] args) throws Exception {
        /*
         * Proceseaza fisiere pe judet...
         */
        for (Judet judet : Judete.judete()) {
            if (judet.getCod().equals("AG")) {
                proceseazaFisier(judet);
            }
        }

        // judete.json
        final File judeteFile = Paths.get(OUTPUT_DIR.toString(), "judete.json").toFile();
        log.info("+ " + judeteFile.getCanonicalPath());
        JSON_WRITER_PRETTY.writeValue(judeteFile, Judete.judete);

        // partide.json
        final File partideFile = Paths.get(OUTPUT_DIR.toString(), "partide.json").toFile();
        log.info("+ " + partideFile.getCanonicalPath());
        JSON_WRITER_PRETTY.writeValue(partideFile, Partid.values());
    }

    private static void proceseazaFisier(Judet judet) throws Exception {
        /*
         * Initializare...
         */
        final Map<String, List<Candidat>> candidatiSenat = new LinkedHashMap<>();          // Partid (cod) -> Lista candidati

        /*
         * Descarca fisier...
         */
        File  xls = descarcaFisierSenat(judet);

        /*
         * Proceseaza fisier...
         */
        final Workbook workbook  = WorkbookFactory.create(new FileInputStream(xls));
        final Sheet    worksheet = workbook.getSheetAt(0);

        for (Row row : worksheet) {
            // Header...
            if (row.getRowNum() == 0) {
                continue;
            }

            log.info(row.getRowNum() + " / " + worksheet.getLastRowNum());

            // Candidat...
            Candidat candidat = Candidat.builder()
                    .functie (Functie.fromExcel (ExcelUtils.getCellValue(row, 7)))
                    .partid  (Partid.fromExcel  (ExcelUtils.getCellValue(row, 3)))
                    .nume    (StringUtils.capitalizeFully(ExcelUtils.getCellValue(row, 5)) + " " + StringUtils.capitalizeFully(ExcelUtils.getCellValue(row, 6)))
                    .pozitie (NumberUtils.createInteger(ExcelUtils.getCellValue(row, 8)))
                    .build();

            switch (candidat.getFunctie()) {
                case SENAT -> {
                    candidatiSenat.computeIfAbsent(candidat.getPartid().getCod(), partid -> new LinkedList<>())
                            .add(candidat);
                }
                case CDEP -> {
                }
            }
        }

        /*
         * Scrie fisiere...
         */
        final File dirJudet = FileUtils.getFile(OUTPUT_DIR, judet.getCod());
        FileUtils.forceMkdir(dirJudet);

        // partide.json
        final File fileSenat = Paths.get(dirJudet.toString(), "senat.json").toFile();
        log.info("+ " + fileSenat.getCanonicalPath());
        FileUtils.writeStringToFile(fileSenat, JSON_WRITER_PRETTY.writeValueAsString(candidatiSenat), StandardCharsets.UTF_8);
    }

    /**
     * Descarca fisier date pentru Senat
     * @param judet
     * @return
     * @throws Exception
     */
    private static File descarcaFisierSenat(Judet judet) throws Exception {
        File file = FileUtils.getFile("build", "data", judet.getCod() + ".xls");
        if (!file.exists()) {
            HttpClient.descarca(String.format(Locale.ENGLISH, LOCATION_XLS_SENAT, (new URLCodec()).encode(judet.getNume().toUpperCase(), "UTF-8")), file);
        }
        return file;
    }
}
