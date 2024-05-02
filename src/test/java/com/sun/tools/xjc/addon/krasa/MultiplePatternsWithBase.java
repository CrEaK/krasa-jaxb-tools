package com.sun.tools.xjc.addon.krasa;

import java.util.List;
import java.util.stream.Collectors;

public class MultiplePatternsWithBase extends AnnotationsMojoTestHelper {

    public MultiplePatternsWithBase(JaxbValidationsAnnotation annotation) {
        super("multiplePatternsWithBase", annotation);
    }

    public void test() throws ClassNotFoundException {
        List<String> annotations = element("MultiPatternWithBase")
                .annotationSimpleName("Pattern")
                .getAnnotations("multiplePatternsWithBase");

        assertFalse(annotations.isEmpty());

        String text = annotations.stream()
                .map(s -> s.trim())
                .collect(Collectors.joining("\n"));

        String expected = "@Pattern.List({\n" +
            "@Pattern(regexp = \"[Y-Z]\"),\n" +
            "@Pattern(regexp = \"([0-9])|([A-B])\")\n" +
            "})";

        assertEquals(expected, text);
    }

}