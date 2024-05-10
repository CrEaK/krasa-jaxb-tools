package com.sun.tools.xjc.addon.krasa.validations;

import java.util.regex.Pattern;

/**
 * Created on 15.02.16.
 */
public class EnumerationBase extends AnnotationsMojoTestHelper {

    public EnumerationBase(ValidationsAnnotation annotation) {
        super("enumeration", annotation);
    }

    public void test() throws ClassNotFoundException {
        element("NaturalPerson")
                .annotationSimpleName("Pattern")
                .attribute("sex")
                        .annotation("Pattern")
                                .assertParam("regexp", "(\\\\Qf\\\\E)|(\\\\Qm\\\\E)")
                        .end()
                .end()
                .attribute("age")
                        .annotation("Pattern")
                                .assertParam("regexp",
                                        "(\\\\Q0 (toddler)\\\\E)|(\\\\Q1-5\\\\E)|" +
                                        "(\\\\Q5-12\\\\E)|(\\\\Q12-18\\\\E)|(\\\\Q18+\\\\E)");
    }

    public void testRegexpValidity() {
        String regexp = "(\\Q0 (toddler)\\E)|(\\Q1-5\\E)|(\\Q5-12\\E)|(\\Q12-18\\E)|(\\Q18+\\E)";
        Pattern pattern = Pattern.compile(regexp);

        assertTrue(pattern.matcher("0 (toddler)").matches());
        assertTrue(pattern.matcher("5-12").matches());
        assertTrue(pattern.matcher("18+").matches());
    }
}
