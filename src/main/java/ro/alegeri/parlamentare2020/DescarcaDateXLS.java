package ro.alegeri.parlamentare2020;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ro.alegeri.data.*;
import ro.alegeri.data.utils.Judete;
import ro.alegeri.utils.ExcelUtils;
import ro.alegeri.utils.HttpClient;
import ro.alegeri.utils.NumberUtils;
import ro.alegeri.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DescarcaDateXLS {
    private static final String       LOCATION_XLS_SENAT = "https://parlamentare2020.bec.ro/wp-content/uploads/2020/11/Senat_%s-2020-11-10.xlsx";
    private static final String       LOCATION_XLS_CDEP  = "https://parlamentare2020.bec.ro/wp-content/uploads/2020/11/Camera-Deputatilor_%s-2020-11-10.xlsx";
    private static final File         OUTPUT_DIR         = FileUtils.getFile("..", "alegeri-parlamentare-2020-web", "src", "json");
    private static final ObjectWriter JSON_WRITER_PRETTY = new ObjectMapper().writerWithDefaultPrettyPrinter();


    public static void main(String[] args) throws Exception {
        /*
         * Proceseaza fisiere pe judet...
         */
        for (Judet judet : Judete.judete()) {
            proceseazaFisier(judet);
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
         * Proceseaza XLS...
         */
        final Map<Partid, List<Candidat>> candidatiSenat = proceseazaFisierSenat(descarcaFisierSenat(judet));
        final Map<Partid, List<Candidat>> candidatiCDEP  = proceseazaFisierCDEP (descarcaFisierCDEP (judet));

        /*
         * Scrie JSON...
         */
        final File dirJudet = FileUtils.getFile(OUTPUT_DIR, judet.getCod());
        FileUtils.forceMkdir(dirJudet);

        // senat.json
        final File fileSenat = Paths.get(dirJudet.toString(), "senat.json").toFile();
        log.info("+ " + fileSenat.getCanonicalPath());
        JSON_WRITER_PRETTY.writeValue(fileSenat, candidatiSenat.entrySet().stream()
                .map(entry -> Lista.builder().partid(entry.getKey()).candidati(entry.getValue()).build())
                .collect(Collectors.toList())
        );

        // cdep.json
        final File fileCDEP = Paths.get(dirJudet.toString(), "cdep.json").toFile();
        log.info("+ " + fileCDEP.getCanonicalPath());
        JSON_WRITER_PRETTY.writeValue(fileCDEP, candidatiCDEP.entrySet().stream()
                .map(entry -> Lista.builder().partid(entry.getKey()).candidati(entry.getValue()).build())
                .collect(Collectors.toList())
        );
    }

    /**
     * Proceseaza fisier - Senat...
     * @param xls Fisier de date.
     * @return Lista candidati per partid.
     * @throws IOException Daca fisierul nu exista sa nu poate fi deschis.
     */
    private static Map<Partid, List<Candidat>> proceseazaFisierSenat(File xls) throws IOException {
        /*
         * Initializare
         */
        final Map<Partid, List<Candidat>> candidati = new LinkedHashMap<>();          // Partid (cod) -> Lista candidati

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

//            log.info(row.getRowNum() + " / " + worksheet.getLastRowNum());

            // Candidat...
            Candidat candidat = Candidat.builder()
                    .functie (Functie.fromExcel (ExcelUtils.getCellValue(row, 8)))
                    .partid  (Partid.fromExcel  (ExcelUtils.getCellValue(row, 3)))
                    .nume    (StringUtils.capitalizeFully(ExcelUtils.getCellValue(row, 6)) + " " + StringUtils.capitalizeFully(ExcelUtils.getCellValue(row, 7)))
                    .pozitie (NumberUtils.createInteger(ExcelUtils.getCellValue(row, 9)))
                    .build();

            candidati.computeIfAbsent(candidat.getPartid(), partid -> new LinkedList<>())
                            .add(candidat);
        }

        /*
         * Retur...
         */
        return candidati;
    }

    /**
     * Descarca fisier date pentru Senat
     */
    private static File descarcaFisierSenat(Judet judet) throws Exception {
        String nume = StringUtils.equals(judet.getCod(), "DIA") ? "Diaspora" : judet.getNume().toUpperCase().replace(' ', '-');
        String url  = String.format(Locale.ENGLISH, LOCATION_XLS_SENAT, (new URLCodec()).encode(nume, "UTF-8"));
        File   file = FileUtils.getFile("build", "data", FilenameUtils.getName(url));
        if (!file.exists()) {
            HttpClient.descarca(url, file);
        }
        return file;
    }

    /**
     * Proceseaza fisier - Camera Deputatilor...
     * @param xls Fisier de date.
     * @return Lista candidati per partid.
     * @throws IOException Daca fisierul nu exista sa nu poate fi deschis.
     */
    private static Map<Partid, List<Candidat>> proceseazaFisierCDEP(File xls) throws IOException {
        /*
         * Initializare
         */
        final Map<Partid, List<Candidat>> candidati = new LinkedHashMap<>();          // Partid (cod) -> Lista candidati

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

//            log.info(row.getRowNum() + " / " + worksheet.getLastRowNum());

            // Candidat...
            Candidat candidat = Candidat.builder()
                    .functie (Functie.fromExcel (ExcelUtils.getCellValue(row, 8)))
                    .partid  (Partid.fromExcel  (ExcelUtils.getCellValue(row, 3)))
                    .nume    (StringUtils.capitalizeFully(ExcelUtils.getCellValue(row, 6)) + " " + StringUtils.capitalizeFully(ExcelUtils.getCellValue(row, 7)))
                    .pozitie (NumberUtils.createInteger(ExcelUtils.getCellValue(row, 9)))
                    .build();

            candidati.computeIfAbsent(candidat.getPartid(), partid -> new LinkedList<>())
                    .add(candidat);
        }

        /*
         * Retur...
         */
        return candidati;
    }

    /**
     * Descarca fisier date pentru Camera Deputatilor
     */
    private static File descarcaFisierCDEP(Judet judet) throws Exception {
        String nume = StringUtils.equals(judet.getCod(), "DIA") ? "Diaspora" : judet.getNume().toUpperCase().replace(' ', '-');
        String url  = String.format(Locale.ENGLISH, LOCATION_XLS_CDEP, (new URLCodec()).encode(nume, "UTF-8"));
        File   file = FileUtils.getFile("build", "data", FilenameUtils.getName(url));
        if (!file.exists()) {
            HttpClient.descarca(url, file);
        }
        return file;
    }
}
