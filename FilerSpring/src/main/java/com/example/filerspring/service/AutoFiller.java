package com.example.filerspring.service;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AutoFiller {
    private XWPFDocument doc;
    private Map<String, String> dictValues;

    public AutoFiller(String docxFile, Map<String, String> dictValues) throws IOException {
        FileInputStream fis = new FileInputStream(docxFile);
        this.doc = new XWPFDocument(fis);
        this.dictValues = dictValues;
        fis.close();
    }

    public AutoFiller(FileInputStream fis, Map<String, String> dictValues) throws IOException {
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
        this.dictValues.put(key, value);
    }

    public void replaceAllInDocx() {
        for (String key : this.dictValues.keySet()) {
            System.out.println(key + " : " + this.dictValues.get(key));
            replaceTextInDocx(key, this.dictValues.get(key));
        }
    }

    public void replaceTextInDocx(String oldText, String newText) {
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            String text = paragraph.getText();
            if (text.contains(oldText)) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String runText = run.getText(0);
                    if (runText == null) continue;
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

    public void saveDocToFile(String newPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(newPath);
        doc.write(fos);
        fos.close();
    }

    public File getTempFile() throws IOException {
        File tempFile = File.createTempFile("/Users/svetislavdobromirov/Documents/temp", ".docx");


        FileOutputStream fos = new FileOutputStream(tempFile);
        doc.write(fos);
        fos.close();
        System.out.println(tempFile.getAbsolutePath());
        return tempFile;
    }
}