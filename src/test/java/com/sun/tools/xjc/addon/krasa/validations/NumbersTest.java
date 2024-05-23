package com.sun.tools.xjc.addon.krasa.validations;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Francesco Illuminati
 */
public class NumbersTest extends RunXJC2MojoTestHelper {

    public NumbersTest() {
        super("numbers", "a");
    }

    public void test() throws ClassNotFoundException {
        withElement("Numbers")
                .assertImportSimpleName("NotNull")
                .assertImportSimpleName("DecimalMax")
                .assertImportSimpleName("DecimalMin")
                .assertImportSimpleName("Digits")
                .withField("decimalValue")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("integerValue")
                        .assertClass(BigInteger.class)
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("negativeIntegerValue")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMax")
                                .assertParam("value", -1)
                                .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("nonPositiveIntegerValue")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMax")
                                .assertParam("value", 0)
                                .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("nonNegativeIntegerValue")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMin")
                                .assertParam("value", 0)
                                .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("positiveIntegerValue")
                        .assertClass(BigInteger.class)
                        .withAnnotation("DecimalMin")
                                .assertParam("value", 1)
                                .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("valueDimension")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("valuePositiveDimension")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .withAnnotation("DecimalMin")
                                .assertParam("value", "0.00")
                                .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("valuePositiveNonZeroDimension")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 2)
                        .end()
                        .withAnnotation("DecimalMin")
                                .assertParam("value", "0.00")
                                .assertParam("inclusive", false).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("valueFourPositiveNonZeroDecimal")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("Digits")
                            .assertParam("integer", 12)
                            .assertParam("fraction", 4)
                        .end()
                        .withAnnotation("DecimalMin")
                                .assertParam("value", "0.0000")
                                .assertParam("inclusive", false).end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("valueSixDigitDecimalFractionOne")
                        .assertClass(BigDecimal.class)
                        .withAnnotation("Digits")
                            .assertParam("integer", 6)
                            .assertParam("fraction", 1)
                        .end()
                        .withAnnotation("NotNull").assertNoValues().end()

                .withField("valueFourDigitYear")
                        .assertClass(BigInteger.class)
                        .withAnnotation("Digits")
                            .assertParam("integer", 4)
                            .assertParam("fraction", 0)
                        .end()
                        .withAnnotation("DecimalMin")
                                .assertParam("value", "1")
                                .assertParam("inclusive", true).end()
                        .withAnnotation("NotNull").assertNoValues().end();
    }

}
