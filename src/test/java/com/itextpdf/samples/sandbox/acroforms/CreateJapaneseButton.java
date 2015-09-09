/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/29016333/pushbuttonfield-with-double-byte-character
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.TrueTypeFont;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType0Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.annotations.type.SampleTest;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.samples.GenericTest;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Ignore
@Category(SampleTest.class)
public class CreateJapaneseButton extends GenericTest {
    public static final String JAPANESE = "\u3042\u304d\u3089";
    public static final String FONT = "./src/test/resources/sandbox/acroforms/FreeSans.ttf";
    public static final String DEST = "./target/test/resources/sandbox/acroforms/create_japanese_button.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new CreateJapaneseButton().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        // TODO Fix some font issues (different FontNames each run)
        PdfFont font = new PdfType0Font(
                pdfDoc,
                new TrueTypeFont(
                        "Free Sans",
                        PdfEncodings.IDENTITY_H,
                        Utilities.inputStreamToArray(new FileInputStream(FONT))),
                "Identity-H");
        PdfButtonFormField pushButton = PdfFormField.createPushButton(
                pdfDoc,
                new Rectangle(36, 780, 144 - 36, 806 - 780),
                "japanese",
                JAPANESE,
                font,
                PdfFormField.DEFAULT_FONT_SIZE);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(pushButton);
        pdfDoc.close();
    }
}