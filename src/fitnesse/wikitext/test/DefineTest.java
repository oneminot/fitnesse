package fitnesse.wikitext.test;

import fitnesse.html.HtmlElement;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.parser.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefineTest {
    @Test public void scansDefine() {
        ParserTestHelper.assertScansTokenType("!define x {y}", "Define", true);
        ParserTestHelper.assertScansTokenType("|!define x {y}|/n", "Define", true);
    }

    @Test public void translatesDefines() throws Exception {
        assertTranslatesDefine("!define x {y}", "x=y");
        assertTranslatesDefine("!define BoBo {y}", "BoBo=y");
        assertTranslatesDefine("!define BoBo  {y}", "BoBo=y");
        assertTranslatesDefine("!define x {}", "x=");
        assertTranslatesDefine("!define x_x {y}", "x_x=y");
        assertTranslatesDefine("!define x.x {y}", "x.x=y");
        assertTranslatesDefine("!define x (y)", "x=y");
        assertTranslatesDefine("!define x [y]", "x=y");
        assertTranslatesDefine("!define x {''y''}", "x=''y''");
    }

    @Test public void definesValues() throws Exception {
        assertDefinesValue("!define x {y}", "x", "y");
        assertDefinesValue("|!define x {y}|\n", "x", "y");
        //todo: move to variableTest?
        //assertDefinesValue("!define x {''y''}", "x", "<i>y</i>");
        //assertDefinesValue("!define x {!note y\n}", "x", "<span class=\"note\">y</span><br/>");
        //assertDefinesValue("!define z {y}\n!define x {${z}}", "x", "y");
        //assertDefinesValue("!define z {''y''}\n!define x {${z}}", "x", "<i>y</i>");
        //assertDefinesValue("!define z {y}\n!define x {''${z}''}", "x", "<i>y</i>");
    }

    @Test public void definesTable() throws Exception {
        assertTranslatesDefine("!define x {|a|b|c|}", "x=|a|b|c|");
    }

    @Test public void definesTwoTables() throws Exception {
        WikiPage pageOne = new TestRoot().makePage("PageOne");
        ParserTestHelper.assertTranslatesTo(pageOne,
                "!define x {|a|b|c|}\n!define y {|d|e|f|}",
                MakeDefinition("x=|a|b|c|") + HtmlElement.endl + "<br/>"
                + MakeDefinition("y=|d|e|f|")+ HtmlElement.endl);
    }

    private void assertDefinesValue(String input, String name, String definedValue) throws Exception {
        WikiPage pageOne = new TestRoot().makePage("PageOne", input);
        ParsingPage page = new ParsingPage(new WikiSourcePage(pageOne));
        Parser.make(page, input).parse();
        assertEquals(definedValue, page.findVariable(name).getValue());
    }

    private void assertTranslatesDefine(String input, String definition) throws Exception {
        WikiPage pageOne = new TestRoot().makePage("PageOne");
        ParserTestHelper.assertTranslatesTo(pageOne, input, MakeDefinition(definition) + HtmlElement.endl);
    }

    private String MakeDefinition(String definition) {
        return "<span class=\"meta\">variable defined: " + definition + "</span>";
    }

}
