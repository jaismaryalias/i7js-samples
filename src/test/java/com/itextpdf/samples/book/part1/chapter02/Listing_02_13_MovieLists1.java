package com.itextpdf.samples.book.part1.chapter02;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.model.Document;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.List;
import com.itextpdf.model.element.ListItem;
import com.itextpdf.samples.GenericTest;
import com.lowagie.database.DatabaseConnection;
import com.lowagie.database.HsqldbConnection;
import com.lowagie.filmfestival.Director;
import com.lowagie.filmfestival.Movie;
import com.lowagie.filmfestival.PojoFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_02_13_MovieLists1 extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part1/chapter02/Listing_02_13_MovieLists1.pdf";

    protected PdfFont normal;
    protected PdfFont boldItalic;

    public static void main(String args[]) throws IOException, SQLException {
        new Listing_02_13_MovieLists1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        //Initialize writer
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        boldItalic = PdfFontFactory.createStandardFont(FontConstants.HELVETICA_BOLDOBLIQUE);
        normal = PdfFontFactory.createStandardFont(FontConstants.HELVETICA);

        // Make the connection to the database
        DatabaseConnection connection = new HsqldbConnection("filmfestival");
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery(
                "SELECT DISTINCT mc.country_id, c.country, count(*) AS c "
                        + "FROM film_country c, film_movie_country mc "
                        + "WHERE c.id = mc.country_id "
                        + "GROUP BY mc.country_id, country ORDER BY c DESC");
        // Create a new list
        List list = new List(Property.ListNumberingType.DECIMAL);
        // loop over the countries
        while (rs.next()) {
            // create a list item for the country
            ListItem item = new ListItem(String.format("%s: %d movies", rs.getString("country"), rs.getInt("c")));
            item.setFont(boldItalic);
            // create a movie list for each country
            List movielist = new List(Property.ListNumberingType.ENGLISH_LOWER);
            for(Movie movie : PojoFactory.getMovies(connection, rs.getString("country_id"))) {
                ListItem movieitem = new ListItem(movie.getMovieTitle());
                List directorlist = new List();
                for (Director director : movie.getDirectors()) {
                    directorlist.add(String.format("%s, %s", director.getName(), director.getGivenName()));
                }
                movieitem.add(directorlist.setFont(normal));
                movielist.add(movieitem);
            }
            item.add(movielist.setFont(normal));
            list.add(item);
        }
        doc.add(list);

        doc.close();
        stm.close();
        connection.close();
    }
}
