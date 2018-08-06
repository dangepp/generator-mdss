package <%= config.packages.restFile %>;

import <%= config.packages.restModel %>.DiseaseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Disease mapper for .xls files.
 */
@Slf4j
public class DiseaseMapper extends DiseaseBaseMapper {


    /**
     * Converts a given .xls file to disases models.
     * @param excelfile - the excel file being mapped
     * @return - the mapped diseas models
     * @throws MapperException - if the file could not be mapped
     */
    public List<DiseaseModel> convertFile(MultipartFile excelfile) throws MapperException {
        try {
            HSSFWorkbook wb = new HSSFWorkbook(excelfile.getInputStream());
            Map<String, Map<String, String>> retList = new HashMap<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                HSSFSheet sheet = wb.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() < 2) {
                    log.warn("Sheet with number={} is empty", i);
                } else {
                    try {
                        HSSFRow header = sheet.getRow(0);
                        List<String> categoryHeaders = validateHeaderAndGetCategories(header);
                        retList = addSheet(retList, categoryHeaders, sheet, i==0);
                    } catch (MapperExecutionException e) {
                        throw new MapperException(String.format("Could not map XLS file at sheet=%d", i), e);
                    }
                }
            }
            wb.close();
            return this.convertData(retList);
        }  catch (IOException e) {
            throw new MapperException("Error Opening the xls file", e);
        }
    }

    private List<String> validateHeaderAndGetCategories(HSSFRow header) throws HeaderException {
        if (header.getPhysicalNumberOfCells() < 2) {
            return null;
        }
        List<String> headers = new ArrayList<>();
        if (!Attributes.ID_HEADER.equalsIgnoreCase(header.getCell(0).getStringCellValue())) {
            throw new HeaderException("First Cell is not an ID cell");
        }
        for (int i = 1; i < header.getPhysicalNumberOfCells(); i++) {
            String currentHeader = header.getCell(i).getStringCellValue().toUpperCase();
            if (!Attributes.getAllCategories().contains(currentHeader)) {
                throw new HeaderException(String.format("Unrecognized Category='%s' found in header", currentHeader));
            }
            headers.add(currentHeader);
        }
        return headers;
    }

    private Map<String, Map<String, String>> addSheet(Map<String, Map<String, String>> data, List<String> headers, HSSFSheet sheet, boolean firstSheet) throws MapperExecutionException {
        Set<String> alreadyProcessedIds = new HashSet<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row.getPhysicalNumberOfCells() > headers.size() + 1) {
                throw new SheetException(String.format("The number of Cells in row number=%d exceed the number of headers", i));
            }
            String id = row.getCell(0).getStringCellValue();
            Map<String, String> dataOfId = data.get(id);
            if (dataOfId == null) {
                if (firstSheet) {
                    dataOfId = new HashMap<>();
                } else {
                    throw new SheetException(String.format("The disease with id=%s is not present previous sheets", id));
                }
            }
            if (alreadyProcessedIds.contains(id)) {
                throw new SheetException(String.format("The disease with id=%s is defined multiple times", id));
            }
            alreadyProcessedIds.add(id);

            data.put(id, addRow(dataOfId, headers, row));
        }
        Set<String> notProcessedIds = new HashSet<>(data.keySet());
        notProcessedIds.removeAll(alreadyProcessedIds);
        if (!notProcessedIds.isEmpty()) {
            throw new SheetException(String.format("The diseases with ids=%s are not present", notProcessedIds));
        }
        return data;
    }

    private Map<String, String> addRow(Map<String, String> data, List<String> headers, HSSFRow row) throws MapperExecutionException {
        for (int i = 1; i <= headers.size(); i++) {
            if (data.get(headers.get(i - 1)) != null) {
                throw new HeaderException(String.format("Data of category='%s' is defined multiple times", headers.get(i - 1)));
            }
            //row does have less entries than the header
            if (row.getPhysicalNumberOfCells() <= i) {
                data.put(headers.get(i - 1), "");

            } else {
                data.put(headers.get(i - 1), row.getCell(i).getStringCellValue());
            }
        }
        return data;
    }
}
