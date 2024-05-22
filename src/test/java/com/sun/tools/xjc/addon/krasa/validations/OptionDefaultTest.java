package com.sun.tools.xjc.addon.krasa.validations;

import java.util.List;

/**
 * Test default parameters
 *
 * @author Francesco Illuminati
 */
public class OptionDefaultTest extends RunXJC2MojoTestHelper {

    public OptionDefaultTest() {
        super("options", "a");
    }

    @Override
    public List<String> getArgs() {
        // overwrite whatever options set by RunXJC2MojoTestHelper so to keep default configuration
        return ArgumentBuilder.builder().getOptionList();
    }

}