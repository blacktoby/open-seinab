package de.seinab.pdf;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import de.seinab.finance.entities.BankData;
import de.seinab.form.InputUtils;
import de.seinab.form.entities.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ConsentFactory {
    private static final Logger log = LoggerFactory.getLogger(ConsentFactory.class);

    private PDDocument document = new PDDocument();
    private BaseTable table;

    private float defaultFontSize = 12;

    private long currentInputGroupId = -1;

    public ConsentFactory(String reference) {
        this.reference = reference;
    }

    private String reference;

    public void saveWithStream(OutputStream outputStream) throws IOException {
        document.save(outputStream);
        document.close();
    }

    public void saveAsFile(File file) throws IOException {
        document.save(file);
        document.close();
    }

    public ConsentFactory build(Submission submission) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        drawPageTitle(page, submission.getForm().getDisplayName(), contentStream);

        drawContentTable(page, submission);

        contentStream.close();

        document.addPage(page);

        if(document.getNumberOfPages() > 1) {
            RevertPages();
        }
        return this;
    }

    private void RevertPages() {
        PDPageTree pages = document.getPages();
        PDDocument rearrangedDocument = new PDDocument();
        for(int i = document.getNumberOfPages()-1; i >= 0; i--) {
            rearrangedDocument.getPages().add(pages.get(i));
        }
        document = rearrangedDocument;
    }

    private void drawContentTable(PDPage page, Submission submission) throws IOException {
        Form form = submission.getForm();
        String consentText = form.getConsent().getText();

        buildTable(page);
        drawInputValues(submission);

        BankData bankData = submission.getForm().getEventGroup().getBankData();
        if(bankData != null) {
            drawBankData(bankData);
        }

        if(form.getConsent() != null && !form.getConsent().getText().isEmpty()) {
            drawConsentText(consentText);
            drawSignature();
        }

        table.draw();
    }

    private void drawPageTitle(PDPage page, String title, PDPageContentStream contentStream) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.newLineAtOffset(50, page.getMediaBox().getHeight()-50);
        contentStream.showText(title);
        contentStream.endText();
    }

    private void buildTable(PDPage page) throws IOException {
        boolean drawContent = true;
        float bottomMargin = 70;
        float yPosition = page.getMediaBox().getHeight() - 60;

        // starting y position is whole page height subtracted by top and bottom margin
        //Initializing table
        float margin = 45;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin ofcourse)
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, document, page, false, drawContent);
    }

    private void drawInputValues(Submission submission) {
        createHeader("Angaben");
        List<InputValue> inputValues = submission.getInputValueList();
        inputValues = InputUtils.sortInputValuesByPositionAndGroup(inputValues);
        inputValues.stream().filter(iv -> iv.getInput().getType() != InputType.textarea).forEach(this::drawInputValue);
    }

    private void drawInputValue(InputValue inputValue) {
        Input input = inputValue.getInput();
        if(input.getInputGroup() != null && input.getInputGroup().getId() != currentInputGroupId) {
            currentInputGroupId = input.getInputGroup().getId();
            createInputGroupHeader(input.getInputGroup().getTitle());
        }
        String name = input.getName();
        String data = InputUtils.beautifyInputValueData(inputValue);
        createNameValueRow(name, data);
    }

    private void drawBankData(BankData bankData) {
        createHeader("Überweisungsdetails");
        createNameValueRow("Name", bankData.getName());
        createNameValueRow("IBAN", bankData.getIban());
        createNameValueRow("BIC", bankData.getBic());
        createNameValueRow("Verwendungszweck", reference);
        Row<PDPage> descriptonRow = createNewRow();
        createCell(descriptonRow, 100, "Bitte nur eine Überweisung per Anmeldung. Geben Sie den genauen Verwendungszweck an, " +
                "sonst wird Ihre Überweisung unter Umständen übersehen! Den Betrag können Sie der Anmeldung entnehmen.")
            .setLineSpacing(1.5F);
    }

    private void drawSignature() {
        Row<PDPage> row = createNewRow(70);
        createCellBold(row, 40, "Ort, Datum", 9)
                .setValign(VerticalAlignment.BOTTOM);
        createCellBold(row, 60, "Unterschrift des Erziehungsberechtigten", 9)
                .setValign(VerticalAlignment.BOTTOM);
    }

    private void drawConsentText(String consentText) {
        createHeader("Einverständnisserklärung");
        Row<PDPage> row = createNewRow();
        createCell(row, 100, consentText)
                .setLineSpacing(1.5F);;
    }

    private void createHeader(String text) {
        float subtitleHeight = 50;
        float subtitleFontSize = 16;

        Row<PDPage> row = createNewRow(subtitleHeight);
        createCellBold(row, 100, text, subtitleFontSize)
                .setValign(VerticalAlignment.BOTTOM);
    }

    private void createInputGroupHeader(String text) {
        float subtitleHeight = 30;
        float subtitleFontSize = 14;

        Row<PDPage> row = createNewRow(subtitleHeight);
        createCellBold(row, 100, text, subtitleFontSize)
                .setValign(VerticalAlignment.BOTTOM);
    }

    private Cell<PDPage> createCellBold(Row<PDPage> row, float width, String text, float fontSize) {
        return createCell(row, width, text, fontSize, PDType1Font.HELVETICA_BOLD);
    }

    private Cell<PDPage> createCellBold(Row<PDPage> row, float width, String text) {
        return createCell(row, width, text, defaultFontSize, PDType1Font.HELVETICA_BOLD);
    }

    private Cell<PDPage> createCell(Row<PDPage> row, float width, String text) {
        return createCell(row, width, text, defaultFontSize, PDType1Font.HELVETICA);
    }

    private Cell<PDPage> createCell(Row<PDPage> row, float width, String text, float fontSize, PDType1Font fontType) {
        Cell<PDPage> cell = row.createCell(width, text);
        cell.setFontSize(fontSize);
        cell.setFont(fontType);
        return cell;
    }

    private Row<PDPage> createNameValueRow(String name, String value) {
        Row<PDPage> nameValueRow = createNewRow();
        drawNameCell(nameValueRow, name);
        drawValueCell(nameValueRow, value);
        return nameValueRow;
    }

    private void drawNameCell(Row<PDPage> row, String name) {
        float questionLength = 30;
        createCellBold(row, questionLength, name);
    }

    private void drawValueCell(Row<PDPage> row, String value) {
        float answerLength = 70;
        createCell(row, answerLength, value);
    }

    private Row<PDPage> createNewRow() {
        float rowHeight = 12;
        return table.createRow(rowHeight);
    }

    private Row<PDPage> createNewRow(float rowHeight) {
        return table.createRow(rowHeight);
    }
}
