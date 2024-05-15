package com.sun.tools.xjc.addon.krasa.validations;

/**
 *
 * @author Francesco Illuminati 
 */
enum NotNullAnnotationCustomMessageType {
    Fieldname("fieldname"), Classname("classname");

    private String value;

    private NotNullAnnotationCustomMessageType(String value) {
        this.value = value;
    }

    public boolean equalsIgnoreCase(String v) {
        return value.equalsIgnoreCase(v);
    }

    public String value() {
        return value;
    }
}
