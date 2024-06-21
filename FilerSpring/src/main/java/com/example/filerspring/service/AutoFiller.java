package com.example.filerspring.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class AutoFiller {

    private final XWPFDocument doc;

    private final Map<String, String> dictValues;

    @Value("${mongo.file.tmp.prefix}")
    private String prefix;

    @Value("${mongo.file.tmp.suffix}")
    private String suffix;

    public AutoFiller(String docxFile, Map<String, String> dictValues) throws IOException {
        FileInputStream fis = new FileInputStream(docxFile);
        this.doc = new XWPFDocument(fis);
        this.dictValues = dictValues;
        fis.close();
    }

    public AutoFiller(FileInputStream fis) throws IOException {
        this.doc = new XWPFDocument(fis);
        this.dictValues = new HashMap<>();
        fis.close();
    }

    public AutoFiller(File file, Map<String, String> dictValues) throws IOException {
        var fis = new FileInputStream(file);
        this.doc = new XWPFDocument(fis);
        this.dictValues = dictValues;
        fis.close();
    }

    public void dateToMap(String tag, String dateStr) {
        // Convert date to specific format
        // Implement date conversion logic here
    }

    public void addValueToMap(String key, String value) {
        dictValues.put(key, value);
    }

    public void replaceAllInDocx() {
        for (String key : dictValues.keySet()) {
            System.out.println(key + " : " + dictValues.get(key));
            replaceTextInDocx(key, dictValues.get(key));
        }
    }

    private void replaceTextInDocx(String oldText, String newText) {
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            String text = paragraph.getText();
            if (text.contains(oldText)) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String runText = run.getText(0);
                    if (Objects.isNull(runText)) {
                        continue;
                    }
                    if (runText.contains(oldText)) {
                        runText = runText.replace(oldText, newText);
                        run.setText(runText, 0);
                    }
                }
            }
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        String text = paragraph.getText();
                        if (text.contains(oldText)) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                String runText = run.getText(0);
                                if (runText.contains(oldText)) {
                                    runText = runText.replace(oldText, newText);
                                    run.setText(runText, 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        XWPFHeaderFooterPolicy headerFooterPolicy = doc.getHeaderFooterPolicy();
        if (headerFooterPolicy != null) {
            XWPFFooter footer = headerFooterPolicy.getDefaultFooter();
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                String text = paragraph.getText();
                if (text.contains(oldText)) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String runText = run.getText(0);
                        if (runText.contains(oldText)) {
                            runText = runText.replace(oldText, newText);
                            run.setText(runText, 0);
                        }
                    }
                }
            }
        }

    }

    public void saveDocToFile(String newPath) {
        try (FileOutputStream fos = new FileOutputStream(newPath)) {
            doc.write(fos);
        } catch (IOException e) {
            log.error("Could not create file {}", newPath);
        }
    }

    public File getTempFile()  {
        File tempFile;
        try {
            tempFile = File.createTempFile(prefix, suffix);
        } catch (IOException e) {
            log.warn("Could not create temp file {}", prefix + suffix);
            throw new RuntimeException(e);
        }
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            doc.write(fos);
        } catch (IOException e) {
            log.error("Could not create file {}", tempFile.getAbsolutePath());
        }
        return tempFile;
    }
}