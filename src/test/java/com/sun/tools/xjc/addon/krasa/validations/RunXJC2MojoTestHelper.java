/*
 * Copyright 2021 Francesco Illuminati <fillumina@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.tools.xjc.addon.krasa.validations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.maven.project.MavenProject;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

/**
 * Testing helper for generated classes.
 *
 * We cannot use reflection here because RunXJC2Mojo acts on the generation phase and the
 * generated artifacts are not compiled.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class RunXJC2MojoTestHelper extends RunXJC2Mojo {

    private static final Set<String> executions = new HashSet<>();

    private final ValidationsAnnotation validationAnnotation;

    public RunXJC2MojoTestHelper(ValidationsAnnotation validation) {
        this.validationAnnotation = validation;
    }

    public abstract String getFolderName();

    public String getNamespace() {
        return "";
    }

    public ValidationsAnnotation getAnnotation() {
        return ValidationsAnnotation.JAVAX;
    }

    public String getAnnotationFileName() {
        return "annotation.txt";
    }

    // artifact creation happens before test executions!
    @Override
    public synchronized final void setUp() throws Exception {
        final String executionName = getExecutionName();
        if (executions.add(executionName)) {
            super.testExecute();
        }
    }

    private String getExecutionName() {
        return getFolderName() + "-" + getAnnotationFileName() + "-" + getAnnotation().name();
    }

    public final void testExecute() throws Exception {
        // override RunXJC2Mojo own method to allow tests to be executed after mojo creation
    }

    // called by the JUnit reflection test running engine (method name starts with test)
    public final synchronized void testCheckAnnotationsInResourceFile() {
        String namespace = getNamespace();
        String[] nsArray = namespace.split(",");
        for (String ns : nsArray) {
            String annotatonFilename = (nsArray.length > 1 ? ns + "-" : "") + getAnnotationFileName();
            Path filename = Paths.get(getGeneratedDirectory().getAbsolutePath() +
                    File.separator + annotatonFilename);

            writeAllElementsTo(ns, filename);

            checkAllAnnotations(filename, annotatonFilename);
        }
    }

    private void checkAllAnnotations(Path filename, String annotatonFilename) throws AssertionError {
        List<String> actual = readFile(filename);
        String annotationFilename = getBaseDir() + "/src/test/resources/" + getFolderName() + "/" +
                annotatonFilename;
        Path annotations = Paths.get(annotationFilename);
        List<String> expected = readFile(annotations);

        if (expected.size() != actual.size()) {
            throw new AssertionError("wrong number of annotations in " + getExecutionName() +
                    " expected:" + expected.size() + " actual:" + actual.size());
        }

        for (int i=0,l=expected.size(); i<l; i++) {
            String expectedLine = expected.get(i).trim();
            String actualLine = actual.get(i).trim();

            assertEquals("annotation differs in " + getExecutionName(),
                    expectedLine, actualLine);
        }
    }

    @Override
    public File getGeneratedDirectory() {
        return new File(getBaseDir(), "target/generated-sources/" + getFolderName());
    }

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources/" + getFolderName());
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setProject(new MavenProject());
        mojo.setForceRegenerate(true);
        mojo.setExtension(true);
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateStringListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

    private synchronized void writeAllElementsTo(String ns, Path filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(filename, Charset.defaultCharset(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            gatAllElementsAsString(ns, writer);
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void gatAllElementsAsString(String ns, Appendable buf) throws IOException {
        List<Path> fileList = allFilesInDirectory(getAbsolutePath(ns));
        Collections.sort(fileList);
        for (Path p : fileList) {
            String name = p.getFileName().toString();
            if (name.endsWith(".java") &&
                    !name.startsWith("package-info") &&
                    !name.startsWith("ObjectFactory")) {
                String filename = name.replace(".java", "");
                ArtifactTester artifactTester = element(ns, filename);
                buf.append(filename).append(System.lineSeparator());
                List<String> attributeList = artifactTester.getAllAttributes();
                Collections.sort(attributeList);
                for (String attribute : attributeList) {
                    buf.append("    ").append(attribute).append(System.lineSeparator());
                    List<String> annotationList = artifactTester.getAnnotations(attribute)
                            .stream()
                            .map(s -> s.trim())
                            .filter(s -> !s.startsWith("@Xml"))
                            .collect(Collectors.toList());
                    annotationList = compactArrayAnnotations(annotationList);
                    for (String a : annotationList) {
                        buf.append("        ").append(a).append(System.lineSeparator());
                    }
                }
            }
        }
    }

//    @Pattern.List({
//        @Pattern(regexp = "[0-9]"),
//        @Pattern(regexp = "[A-B]")
//    })
    private List<String> compactArrayAnnotations(List<String> annotationList) {
        List<String> reversedList = new ArrayList<>();
        for (int i=annotationList.size()-1; i>=0; i--) {
            String line = annotationList.get(i);
            if ("})".equals(line)) {
                for (int k=i-1; k>=0; k--) {
                    String startLine = annotationList.get(k);
                    if (startLine.endsWith("({")) {
                        List<String> sublist = annotationList.subList(k + 1, i);
                        String params = sublist.stream().collect(Collectors.joining(" "));
                        reversedList.add(startLine + params + "})");
                        i = k - 1;
                        break;
                    }
                }
            } else {
                reversedList.add(line);
            }
        }
        Collections.sort(reversedList);
        return reversedList;
    }


    /**
     * Read an element using the default namespace.
     * @param elementName The name of the root element created (the java class name created by JAXB).
     * @see #getNamespace()
     */
    public ArtifactTester element(String elementName) {
        final String filename = elementName + ".java";
        List<String> lines = readFile(getNamespace(), filename);
        return new ArtifactTester(filename, lines);
    }

    public ArtifactTester element(String ns, String elementName) {
        final String filename = elementName + ".java";
        List<String> lines = readFile(ns, filename);
        return new ArtifactTester(filename, lines);
    }

    private List<String> readFile(String ns, String filename) {
        String absoluteName = getAbsolutePath(ns) + filename;
        Path path = Paths.get(absoluteName);
        return readFile(path);
    }

    private List<String> readFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException ex) {
            throw new AssertionError("error loading file " + path, ex);
        }
    }

    private List<Path> allFilesInDirectory(String path) {
        try {
            return Files.list(new File(path).toPath())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new AssertionError("error loading files in " + path, ex);
        }
    }

    private String getAbsolutePath(String ns) {
        if (ns == null) {
            ns = "";
        } else {
            ns = File.separator + (ns.trim().isEmpty() ? "generated" : ns);
        }
        return getGeneratedDirectory().getAbsolutePath() + ns + File.separator;
    }

    public class ArtifactTester {

        private final String filename;
        private final List<String> lines;

        private ArtifactTester(String filename, List<String> lines) {
            this.filename = filename;
            this.lines = lines;
        }

        public ArtifactTester annotationSimpleName(String simpleName) throws ClassNotFoundException {
            String canonicalName = validationAnnotation.getCanonicalClassName(simpleName);
            return annotationCanonicalName(canonicalName);
        }

        public ArtifactTester annotationCanonicalName(String canonicalName) {
            Objects.requireNonNull(canonicalName);
            lines.stream()
                    .filter(s -> s.contains(canonicalName))
                    .findAny()
                    .orElseThrow(() -> new AssertionError("annotation not found: " + canonicalName));
            return this;
        }

        public AttributeTester classAnnotations() {
            final String clazzName = filename.replace(".java", "");
            int line = getLineForClass(clazzName);
            List<String> annotationList = getAnnotations(clazzName, line);
            String definition = lines.get(line);
            return new AttributeTester(this, filename, clazzName,
                    definition, annotationList);
        }

        public AttributeTester attribute(String attributeName) {
            int line = getLineForAttribute(attributeName);
            List<String> annotationList = getAnnotations(attributeName, line);
            String definition = lines.get(line);
            return new AttributeTester(this, filename, attributeName,
                    definition, annotationList);
        }

        public List<String> getAnnotations(String attributeName) {
            int line = getLineForAttribute(attributeName);
            return getAnnotations(attributeName, line);
        }

        private List<String> getAnnotations(String attributeName, int line) {
            int prevAttribute = prevAttributeLine(attributeName, line);
            List<String> annotationList = lines.subList(prevAttribute, line);
            return annotationList;
        }

        public RunXJC2MojoTestHelper end() {
            return RunXJC2MojoTestHelper.this;
        }

        private int getLineForClass(String className) {
            for (int i = 0, l = lines.size(); i < l; i++) {
                String line = lines.get(i).trim();
                if (line.startsWith("public class " + className)) {
                    return i;
                }
            }
            throw new AssertionError(
                    "attribute " + className + " not found in file " + filename);
        }

        private List<String> getAllAttributes() {
            List<String> list = new ArrayList<>();
            for (int i = 0, l = lines.size(); i < l; i++) {
                String line = lines.get(i).trim();
                if (line.startsWith("protected ") && line.endsWith(";")) {
                    int idx = line.lastIndexOf(' ') + 1;
                    String attrName = line.substring(idx, line.length() - 1);
                    list.add(attrName);
                }
            }
            return list;
        }

        private int getLineForAttribute(String attributeName) {
            for (int i = 0, l = lines.size(); i < l; i++) {
                String line = lines.get(i).trim();
                if (line.startsWith("protected ") && line.endsWith(attributeName + ";")) {
                    return i;
                }
            }
            throw new AssertionError(
                    "attribute " + attributeName + " not found in file " + filename);
        }

        private int prevAttributeLine(String attributeName, int attributeLine) {
            for (int i = attributeLine - 1; i >= 0; i--) {
                String line = lines.get(i).trim();
                if (line.trim().isEmpty() ||
                        line.startsWith("public ") ||
                        line.startsWith("protected ")) {
                    return i + 1;
                }
            }
            throw new AssertionError(
                    "cannot extract validatitions for " + attributeName + " in file " + filename);
        }
    }

    public static class AttributeTester {

        private final ArtifactTester parent;
        private final String filename;
        private final String attributeName;
        private final String definition;
        private final List<String> annotationList;

        public AttributeTester(ArtifactTester parent, String filename, String attributeName,
                String definition, List<String> annotationList) {
            this.parent = parent;
            this.filename = filename;
            this.attributeName = attributeName;
            this.definition = definition;
            this.annotationList = annotationList;
        }

        public ArtifactTester end() {
            return parent;
        }

        public AttributeTester assertClass(Class<?> clazz) {
            String className = clazz.getSimpleName();
            if (!definition.contains(className + " ")) {
                throw new AssertionError("attribute " + attributeName +
                            " in " + filename + " expected of class " + clazz.getName() +
                        " but is: " + definition);
            }
            return this;
        }

        public AttributeTester assertAnnotationNotPresent(String annotation) {
            long counter = annotationList.stream()
                    .filter(l -> l.trim().startsWith("@" + annotation))
                    .count();
            if (counter != 0) {
                throw new AssertionError("annotation " + annotation + " of attribute " +
                    attributeName + " in " + filename + " found");
            }
            return this;
        }

        public AnnotationTester annotation(String annotation) {
            String line = annotationList.stream()
                    .filter(l -> l.trim().startsWith("@" + annotation))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "annotation " + annotation + " of attribute " + attributeName +
                            " in " + filename + " not found "));
            return new AnnotationTester(this, line, annotation);
        }

        public AttributeTester assertNoAnnotationsPresent() {
            if (!annotationList.isEmpty()) {
                throw new AssertionError(
                        "attribute " + attributeName +
                        " in " + filename + " contains annotations: " + annotationList.toString());
            }
            return this;
        }
    }

    public static class AnnotationTester {

        private final AttributeTester parent;
        private final String line;
        private final String annotation;
        private final Map<String, String> valueMap = new HashMap<>();

        public AnnotationTester(AttributeTester parent, String line, String annotationName) {
            this.parent = parent;
            this.line = line;
            this.annotation = annotationName;

            parseAnnotationValues(line);
        }

        private void parseAnnotationValues(String line) {
            String values = "";
            if (line.contains("(")) {
                int start = line.indexOf("(");
                values = line.substring(start + 1, line.length() - 1);
            }
            if (!values.trim().isEmpty()) {
                if (line.contains("=")) {
                    String[] pairs = values.split(",");
                    for (String p : pairs) {
                        String[] kv = p.split("=");
                        valueMap.put(kv[0].trim(), kv[1].trim());
                    }
                } else {
                    valueMap.put("value", values);
                }
            }
        }

        public AttributeTester end() {
            return parent;
        }

        public AttributeTester assertNoValues() {
            if (!valueMap.isEmpty()) {
                throw new AssertionError("annotation " + annotation +
                        " of attribute " + parent.attributeName +
                        " in " + parent.filename + " not empty: " + valueMap);
            }
            return parent;
        }

        public AttributeTester assertValue(Object value) {
            assertParam("value", value);
            return parent;
        }

        public AnnotationTester assertParam(String name, Object value) {
            Objects.requireNonNull(value, "parameter " + name + " value cannot be null");
            String v = valueMap.get(name);
            if (v == null) {
                throw new AssertionError("annotation " + annotation +
                        " of attribute " + parent.attributeName +
                        " in " + parent.filename + " not found: " + valueMap);
            }
            while (v.startsWith("\"")) {
                v = v.substring(1, v.length()-1);
            }
            if (!v.equals(value.toString())) {
                throw new AssertionError("annotation " + annotation +
                        " of attribute " + parent.attributeName +
                        " in " + parent.filename + " mismatched value, expected: " + value +
                        " found " + v);
            }
            return this;
        }

    }

}
