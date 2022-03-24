package de.seinab;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsentFormApp {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String formPdf = "mapped_bundescamp2022_anmeldung.pdf";

        try (PDDocument pdfDocument = PDDocument.load(new File(formPdf))) {
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            for (PDField field : acroForm.getFields()) {
                System.out.println(field.toString());
                System.out.println("Mapping name: " + field.getMappingName());
                String mapppingName = br.readLine();

                if(StringUtils.isEmpty(mapppingName)) {
                    System.out.println("Skipping...");
                    continue;
                }
                field.setMappingName(mapppingName);
            }
            pdfDocument.save("mapped_"+formPdf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
