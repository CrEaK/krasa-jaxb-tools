package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

public class RunChoicesPluginTest extends RunXJC2Mojo {

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/choices");
    }

    @Override
    public List<String> getArgs() {
        final List<String> args = new ArrayList<String>(super.getArgs());
        args.add("-XJsr303Annotations");
        args.add("-XJsr303Annotations:targetNamespace=a");
        // args.add("-XJsr303Annotations:JSR_349=true");
        return args;
    }

    @Override
    protected void configureMojo(final AbstractXJC2Mojo mojo) {
        mojo.setProject(new MavenProject());
        mojo.setForceRegenerate(true);
        mojo.setExtension(true);
        mojo.setSchemaDirectory(getSchemaDirectory());
        mojo.setGenerateDirectory(getGeneratedDirectory());
        mojo.setGeneratePackage("choices");
        mojo.setArgs(getArgs());
        mojo.setVerbose(true);
        mojo.setDebug(false);
        mojo.setWriteCode(isWriteCode());
    }

}
