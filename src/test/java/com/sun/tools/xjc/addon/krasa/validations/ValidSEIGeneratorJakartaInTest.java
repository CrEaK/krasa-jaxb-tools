package com.sun.tools.xjc.addon.krasa.validations;

import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ValidSEIGeneratorJakartaInTest extends ValidSEIGeneratorTestHelper {

    public ValidSEIGeneratorJakartaInTest() {
        super(ValidationsAnnotation.JAKARTA, "In");
    }

    @Test
    public void shouldInBePresent() {
        withGeneratedInterface()
                .assertImport(ValidationsAnnotation.JAKARTA.getValidClass())
                .withMethod("getWeather")
                    .assertAnnotationNotPresent("Valid").end()
                    .withParameter("city")
                        .withAnnotation("Valid").assertNoParameters();
    }
}
