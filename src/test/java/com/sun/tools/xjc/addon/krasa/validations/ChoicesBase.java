package com.sun.tools.xjc.addon.krasa.validations;

public class ChoicesBase extends AnnotationsMojoTestHelper {

    public ChoicesBase(ValidationsAnnotation annotation) {
        super("choices", annotation);
    }

    public void test() {
        element("Choices")
                .attribute("tea")
                        .annotation("XmlElement")
                                .assertParam("name", "Tea")
                        .end()
                .end()
                .attribute("coffee")
                        .annotation("XmlElement")
                                .assertParam("name", "Coffee")
                        .end()
                .end();
    }

}
