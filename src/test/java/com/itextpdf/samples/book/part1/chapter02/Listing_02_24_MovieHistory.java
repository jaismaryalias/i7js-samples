package com.itextpdf.samples.book.part1.chapter02;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.model.Document;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.samples.GenericTest;
import com.lowagie.database.DatabaseConnection;
import com.lowagie.database.HsqldbConnection;
import com.lowagie.filmfestival.Movie;
import com.lowagie.filmfestival.MovieComparator;
import com.lowagie.filmfestival.PojoFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

@Ignore
@Category(SampleTest.class)
public class Listing_02_24_MovieHistory extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part1/chapter02/Listing_02_24_MovieHistory.pdf";

    public static final String[] EPOCH =
            {"Forties", "Fifties", "Sixties", "Seventies", "Eighties",
                    "Nineties", "Twenty-first Century"};

    public static void main(String args[]) throws IOException, SQLException {
        new Listing_02_24_MovieHistory().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        //Initialize writer
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        PdfFont[] fonts = new PdfFont[4];
        fonts[0] = PdfFontFactory.createStandardFont(FontConstants.HELVETICA); // 24
        fonts[1] = PdfFontFactory.createStandardFont(FontConstants.HELVETICA); // 18
        fonts[2] = PdfFontFactory.createStandardFont(FontConstants.HELVETICA); // 14
        fonts[3] = PdfFontFactory.createStandardFont(FontConstants.HELVETICA_BOLD); // 12

        // Make the connection to the database
        DatabaseConnection connection = new HsqldbConnection("filmfestival");
        Set<Movie> movies =
                new TreeSet<>(new MovieComparator(MovieComparator.BY_YEAR));
        movies.addAll(PojoFactory.getMovies(connection));
        int epoch = -1;
        int currentYear = 0;
        Paragraph title = null;
        // TODO No Chapter in itext6
        // Chapter chapter = null;
        // TODO No Section in itext6
        // Section section = null;
        // Section subsection = null;
        // loop over the movies
        for (Movie movie : movies) {
            // add the chapter if we're in a new epoch
            if (epoch < (movie.getYear() - 1940) / 10) {
                epoch = (movie.getYear() - 1940) / 10;
//                if (chapter != null) {
//                    document.add(chapter);
//                }
                title = new Paragraph(EPOCH[epoch]).setFont(fonts[0]);
                // chapter = new Chapter(title, epoch + 1);
            }
            // switch to a new year
            if (currentYear < movie.getYear()) {
                currentYear = movie.getYear();
                title = new Paragraph(
                        String.format("The year %d", movie.getYear())).setFont(fonts[1]);
                // section = chapter.addSection(title);
                // section.setBookmarkTitle(String.valueOf(movie.getYear()));
                // section.setIndentation(30);
                // section.setBookmarkOpen(false);
                // section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
                // section.add(new Paragraph(
                //         String.format("Movies from the year %d:", movie.getYear())));
            }
            title = new Paragraph(movie.getMovieTitle()).setFont(fonts[2]);
            // subsection = section.addSection(title);
            // subsection.setIndentationLeft(20);
            // subsection.setNumberDepth(1);
            // subsection.add(new Paragraph("Duration: " + movie.getDuration(), FONT[3]));
            // subsection.add(new Paragraph("Director(s):", FONT[3]));
            // subsection.add(PojoToElementFactory.getDirectorList(movie));
            // subsection.add(new Paragraph("Countries:", FONT[3]));
            // subsection.add(PojoToElementFactory.getCountryList(movie));
        }
        // doc.add(chapter);
        doc.close();
        connection.close();
    }
}
