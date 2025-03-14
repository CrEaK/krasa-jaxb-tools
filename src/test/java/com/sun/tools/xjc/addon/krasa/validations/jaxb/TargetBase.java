package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.RunXJC2MojoTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class TargetBase extends RunXJC2MojoTestHelper {
    private final String targetNamespace;

    public TargetBase(String targetNamespace) {
        super("target", "a,b");
        this.targetNamespace = targetNamespace;
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, false)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, targetNamespace)
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }
}
