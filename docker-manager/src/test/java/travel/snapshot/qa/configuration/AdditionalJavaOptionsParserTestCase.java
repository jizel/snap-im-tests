package travel.snapshot.qa.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.tomcat.util.AdditionalJavaOptionsParser;

import java.util.List;

@Category(UnitTest.class)
public class AdditionalJavaOptionsParserTestCase {

    private AdditionalJavaOptionsParser additionalJavaOptionsParser = new AdditionalJavaOptionsParser();

    @Test
    public void nullInputParserTest() {
        assertTrue(additionalJavaOptionsParser.parse(null).isEmpty());
    }

    @Test
    public void parserTest() {
        String options = "opt0 opt1=val1 \"opt2=val2 with space\"";
        List<String> parsedOptions = additionalJavaOptionsParser.parse(options);

        assertEquals(3, parsedOptions.size());
        assertEquals("opt0", parsedOptions.get(0));
        assertEquals("opt1=val1", parsedOptions.get(1));
        assertEquals("opt2=val2 with space", parsedOptions.get(2));
    }
}
