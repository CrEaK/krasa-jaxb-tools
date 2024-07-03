package com.sun.tools.xjc.addon.krasa.validations;

/**
 * The example documents a kind of type extension which is not handled by XJC that simply
 * ignores it.
 * Refer to <a href='https://github.com/fillumina/krasa-jaxb-tools/issues/16'>issue #16</a>.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExtendTest extends RunXJC2MojoTestHelper {

    public ExtendTest() {
        super("extend", "a");
    }

}
