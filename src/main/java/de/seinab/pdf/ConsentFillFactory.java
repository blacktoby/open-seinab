package de.seinab.pdf;

import de.seinab.form.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConsentFillFactory {
    private static final Logger log = LoggerFactory.getLogger(ConsentFillFactory.class);
    private final String FEE_IDENTIFIER = "{{fee}}";
    private final String REFERENCE_IDENTIFIER = "{{reference}}";

    private PDDocument document = new PDDocument();

    private long currentInputGroupId = -1;
    private final Pattern keyValuePattern = Pattern.compile("(?<key>.*);(?<value>.*)");;

    public ConsentFillFactory(String reference, File formPdfFile) {
        this.reference = reference;
        this.formPdfFile = formPdfFile;
    }

    private String reference;
    private File formPdfFile;

    public void saveWithStream(OutputStream outputStream) throws IOException {
        document.save(outputStream);
        document.close();
    }

    public void saveAsFile(File file) throws IOException {
        document.save(file);
        document.close();
    }

    public ConsentFillFactory build(Submission submission) throws IOException, UnknownFieldException {
        document = PDDocument.load(formPdfFile);
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        List<PDField> mappedFields =
                acroForm.getFields().stream()
                        .filter(f -> StringUtils.isNoneEmpty(f.getMappingName()))
                        .collect(Collectors.toList());


        Map<String, InputValue> inputValueByName =
                submission.getInputValueList().stream().collect(Collectors.toMap(i -> i.getInput().getName(), i -> i));

        for (PDField field : mappedFields) {
            if(field instanceof PDTextField) {
                handleTextField(submission, inputValueByName, (PDTextField) field);
            } else if(field instanceof PDCheckBox) {
                handleCheckboxField(inputValueByName, (PDCheckBox) field);
            } else {
                System.out.println("Unknown Type: " + field.getFieldType());
            }
        }
        acroForm.getFields().forEach(f -> f.setReadOnly(true));
        return this;
    }

    private void handleTextField(Submission submission, Map<String, InputValue> inputValueByName, PDTextField field)
            throws IOException {
        String mappingName = field.getMappingName();

        if(StringUtils.equals(mappingName, "fee")) {
            setFeeField(submission, field);
            return;
        } else if(StringUtils.equals(mappingName, "reference")) {
            setReferenceField(field);
            return;
        }

        if(!inputValueByName.containsKey(mappingName)) return;

        InputValue inputValue = inputValueByName.get(mappingName);
        if(inputValue.getInput() instanceof SelectInput) {
            SelectInput selectInput = (SelectInput) inputValue.getInput();
            InputOption inputOption = selectInput.getInputOptionByValue(inputValue.getData());
            field.setValue(inputOption != null ? inputOption.getText() : "");
        } else {
            field.setValue(inputValue.getData());
        }

    }

    private void setReferenceField(PDTextField field) throws IOException {
        String newValue = reference;

        if(StringUtils.contains(field.getValue(), REFERENCE_IDENTIFIER)) {
            newValue = StringUtils.replace(field.getValue(), REFERENCE_IDENTIFIER, newValue);
        }
        field.setValue(newValue);
    }

    private void setFeeField(Submission submission, PDTextField field) throws IOException {
        String newValue  = submission.getForm().getFormFee().getFeeString();

        if(StringUtils.contains(field.getValue(), FEE_IDENTIFIER)) {
            newValue = StringUtils.replace(field.getValue(), FEE_IDENTIFIER, newValue);
        }
        field.setValue(newValue);
    }

    private void handleCheckboxField(Map<String, InputValue> inputValueByName, PDCheckBox field) throws IOException {
        String fieldMapping = field.getMappingName();
        InputKeyValue keyValue = extractInputKeyValue(fieldMapping);
        if(keyValue == null) {
            return;
        }

        InputValue inputValue = inputValueByName.get(keyValue.key);
        if(!StringUtils.equals(inputValue.getData(), keyValue.value)) {
            return;
        }
        field.check();
    }

    private InputKeyValue extractInputKeyValue(String fieldMapping) {
        Matcher matcher = keyValuePattern.matcher(fieldMapping);
        if(!matcher.matches()) return null;


        InputKeyValue keyValue = new InputKeyValue();
        keyValue.key = matcher.group("key");
        keyValue.value = matcher.group("value");

        if(StringUtils.isAllEmpty(keyValue.key, keyValue.value)) return null;

        return keyValue;
    }

    public static class UnknownFieldException extends Exception {
        public UnknownFieldException(String message) {
            super(message);
        }
    }

    private static class InputKeyValue {
        public String key;
        public String value;
    }
}
