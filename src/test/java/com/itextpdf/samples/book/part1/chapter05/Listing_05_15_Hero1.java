/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part1.chapter05;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.model.Document;
import com.itextpdf.samples.GenericTest;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_05_15_Hero1 extends GenericTest {
    public static final String DEST =
            "./target/test/resources/book/part1/chapter05/Listing_05_15_Hero1.pdf";
    public static final String RESOURCE = "./src/test/resources/book/part1/chapter05/hero.txt";

    public static void main(String args[]) throws IOException, SQLException {
        new Listing_05_15_Hero1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc, new PageSize(new Rectangle(-1192, -1685, 2 * 1192, 2 * 1685)));
        pdfDoc.addNewPage();
        PdfFormXObject template = new PdfFormXObject(new Rectangle(pdfDoc.getDefaultPageSize().getWidth(),
                pdfDoc.getDefaultPageSize().getHeight()));
        PdfCanvas canvasTemplate = new PdfCanvas(template, pdfDoc);
        createTemplate(canvasTemplate, 4);
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage()).addXObject(template, -1192, -1685);
        canvas.moveTo(-595, 0);
        canvas.lineTo(595, 0);
        canvas.moveTo(0, -842);
        canvas.lineTo(0, 842);
        canvas.stroke();
        doc.close();
    }

    public void createTemplate(PdfCanvas canvas, int factor)
            throws IOException {
        canvas.concatMatrix(factor, 0, 0, factor, 0, 0);
        FileReader reader = new FileReader(RESOURCE);
        int c;
        while ((c = reader.read()) > -1) {
            canvas.writeLiteral(new String(new char[]{(char) c}));
        }
    }
}