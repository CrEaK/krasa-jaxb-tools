package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidationsOptions {
    // sets default values in Builder not here
    private final String targetNamespace;
    private final boolean singlePattern;
    private final boolean verbose;
    private final boolean notNullAnnotations;
    private final boolean notNullCustomMessage;
    private final boolean notNullPrefixFieldName;
    private final boolean notNullPrefixClassName;
    private final String notNullCustomMessageText;
    private final boolean validationCollection;
    private final ValidationsAnnotation annotationFactory;

    public void logActualOptions() {
        if (verbose) {
            System.out.println(getActualOptionValuesAsString());
        }
    }

    /** @return a multi line string containing the value for each option. */
    private String getActualOptionValuesAsString() {
        String linePrefix = "    ";
        StringBuilder buf = new StringBuilder();
        buf
                .append("[info] ")
                .append(JaxbValidationsPlugin.PLUGIN_NAME)
                .append(" options:")
                .append(System.lineSeparator());

        for (ValidationsArgument a : ValidationsArgument.values()) {
            buf
                    .append(linePrefix)
                    .append(a.name())
                    .append(": ")
                    .append(Objects.toString(a.getValue(this)))
                    .append(System.lineSeparator());
        }

        return buf.toString();
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public boolean isSinglePattern() {
        return singlePattern;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isNotNullAnnotations() {
        return notNullAnnotations;
    }

    public boolean isNotNullCustomMessage() {
        return notNullCustomMessage;
    }

    public boolean isNotNullPrefixFieldName() {
        return notNullPrefixFieldName;
    }

    public boolean isNotNullPrefixClassName() {
        return notNullPrefixClassName;
    }

    public String getNotNullCustomMessageText() {
        return notNullCustomMessageText;
    }

    public boolean isValidationCollection() {
        return validationCollection;
    }

    public ValidationsAnnotation getAnnotationFactory() {
        return annotationFactory;
    }

    public static class Builder {
        private String targetNamespace = null;
        private boolean singlePattern = true;
        private boolean verbose = false;
        private boolean notNullAnnotations = true;
        private boolean notNullCustomMessage = false;
        private boolean notNullPrefixFieldName = false;
        private boolean notNullPrefixClassName = false;
        private String notNullCustomMessageText = null;
        private boolean validationCollection = true;
        private ValidationsAnnotation annotationFactory = ValidationsAnnotation.JAVAX;

        private Builder() {
        }

        /** @return 1 if the argument is referring to this plugin, 0 otherwise. */
        public int parseArgument(String option)
                throws BadCommandLineException {
            if (option.startsWith(JaxbValidationsPlugin.PLUGIN_OPTION_NAME)) {
                int idx = option.indexOf("=");
                if (idx != -1) {
                    final String name = option.substring(
                            JaxbValidationsPlugin.PLUGIN_OPTION_NAME_LENGHT, idx);
                    final String value = option.substring(idx + 1);
                    ValidationsArgument argument = ValidationsArgument.parse(name);
                    setValue(argument, value);
                } else if (option.length() > JaxbValidationsPlugin.PLUGIN_OPTION_NAME_LENGHT) {
                    final String name = option.substring(
                            JaxbValidationsPlugin.PLUGIN_OPTION_NAME_LENGHT);
                    ValidationsArgument argument = ValidationsArgument.parse(name);
                    setValue(argument, "true");
                }
                return 1;
            }
            return 0;
        }

        private void setValue(ValidationsArgument argument, final String value)
                throws BadCommandLineException {
            try {
                String error = argument.setValue(this, value);
                if (error != null) {
                    throw new BadCommandLineException(
                            "option " + argument.name() + ": " +
                            (error.length() > 0 ? error + ", " : "") +
                            "cannot accept '" + value + "' as a " + argument.getTypeName());
                }
            } catch (NullPointerException ex) {
                throw new BadCommandLineException(argument.errorMessage(value));
            }
        }

        public Builder targetNamespace(final String value) {
            this.targetNamespace = value;
            return this;
        }

        public Builder singlePattern(final boolean value) {
            this.singlePattern = value;
            return this;
        }

        public Builder verbose(final boolean value) {
            this.verbose = value;
            return this;
        }

        public Builder notNullAnnotations(final boolean value) {
            this.notNullAnnotations = value;
            return this;
        }

        public Builder notNullCustomMessage(final boolean value) {
            this.notNullCustomMessage = value;
            return this;
        }

        public Builder notNullPrefixFieldName(final boolean value) {
            this.notNullPrefixFieldName = value;
            return this;
        }

        public Builder notNullPrefixClassName(final boolean value) {
            this.notNullPrefixClassName = value;
            return this;
        }

        public Builder notNullCustomMessageText(final String value) {
            this.notNullCustomMessageText = value;
            return this;
        }

        public Builder validationCollection(final boolean value) {
            this.validationCollection = value;
            return this;
        }

        public Builder annotationFactory(final ValidationsAnnotation value) {
            this.annotationFactory = value;
            return this;
        }

        public ValidationsOptions build() {
            return new com.sun.tools.xjc.addon.krasa.validations.ValidationsOptions(targetNamespace, singlePattern,
                    verbose, notNullAnnotations, notNullCustomMessage, notNullPrefixFieldName,
                    notNullPrefixClassName, notNullCustomMessageText,
                    validationCollection, annotationFactory);
        }
    }

    public static ValidationsOptions.Builder builder() {
        return new ValidationsOptions.Builder();
    }

    private ValidationsOptions(final String targetNamespace,
            final boolean singlePattern,
            final boolean verbose, final boolean notNullAnnotations,
            final boolean notNullCustomMessage, final boolean notNullPrefixFieldName,
            final boolean notNullPrefixClassName, final String notNullCustomMessageText,
            final boolean validationCollection,
            final ValidationsAnnotation annotationFactory) {
        this.targetNamespace = targetNamespace;
        this.singlePattern = singlePattern;
        this.verbose = verbose;
        this.notNullAnnotations = notNullAnnotations;
        this.notNullCustomMessage = notNullCustomMessage;
        this.notNullPrefixFieldName = notNullPrefixFieldName;
        this.notNullPrefixClassName = notNullPrefixClassName;
        this.notNullCustomMessageText = notNullCustomMessageText;
        this.validationCollection = validationCollection;
        this.annotationFactory = annotationFactory;
    }

}
