package com.sun.tools.xjc.addon.krasa.validations;

/**
 *
 * @see https://github.com/jirutka/validator-collection
 * @author Francesco Illuminati
 */
public class ListsTest extends RunXJC2MojoTestHelper {

    public ListsTest() {
        super("lists", "a");
    }

    public void testContainer() throws ClassNotFoundException {
        withElement("Container")
                .assertImportSimpleName("Valid")
                .assertImportSimpleName("Size")
                .assertImportSimpleName("NotNull")
                .withField("listOfString")
                        .withAnnotation("Valid").assertNoValues()
                        .withAnnotation("Size")
                            .assertParam("min", 0)
                            .assertParam("max", 5).end()
                        .withAnnotation("EachSize")
                            .assertParam("min", 1)
                            .assertParam("max", 500).end()
                        .end()
                .withField("listOfAddress")
                        .withAnnotation("Size")
                            .assertParam("min", 3)
                            .assertParam("max", 7).end()
                        .withAnnotation("NotNull").assertNoValues()
                        .withAnnotation("Valid").assertNoValues()
                        .end()
                .withField("listOfPercentage")
                        .withAnnotation("Valid").assertNoValues()
                        .withAnnotation("Size")
                            .assertParam("min", 2)
                            .assertParam("max", 4).end()
                        .withAnnotation("EachDigits")
                            .assertParam("integer", 3)
                            .assertParam("fraction", 2).end()
                        .withAnnotation("EachDecimalMin")
                            .assertParam("value", "0.00")
                            .assertParam("inclusive", false).end()
                        .withAnnotation("EachDecimalMax")
                            .assertParam("value", "100.00")
                            .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues();
    }

    public void testAddressType() {
        withElement("AddressType")
                .withField("name")
                        .withAnnotation("NotNull").assertNoValues()
                        .end()
                .withField("formalTitle")
                        .withAnnotation("Size")
                            .assertParam("max", 10);
    }
}
