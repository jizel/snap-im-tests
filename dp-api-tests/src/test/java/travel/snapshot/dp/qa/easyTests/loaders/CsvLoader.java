package travel.snapshot.dp.qa.easyTests.loaders;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Log
@Slf4j
public class CsvLoader extends BasicLoader {

    /**
     * Construct a new YamlLoader
     */
    public CsvLoader() {
    }

    public static List<Map<String, String>> loadExampleCsv(String filePath) {
        /*
        Expects the path to the file fith ONE example table (one file per one scenario outline)
         */

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<CSVRecord> records = new ArrayList<CSVRecord>();
        try {
            Reader in = new FileReader(filePath);
            try {
                for (CSVRecord record : CSVFormat.EXCEL.parse(in)) {
                    records.add(record);
                }
            } catch (IOException e) {
//                log.error(e.getMessage());
            }
            List<String> header = new ArrayList<String>();
            for (String value : records.get(0)) {
                header.add(value);
            }
            records.remove(0); // remove header
            for (CSVRecord record : records) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < header.size(); i++) {
                    map.put(header.get(i), record.get(i));
                }
                result.add(map);
            }
        } catch (FileNotFoundException e) {
//            log.error(String.format(FILEDONTEXIST_MSG, filePath));
        }
        return result;
    }
}
