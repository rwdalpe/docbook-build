package com.github.rwdalpe.docbookbuild.tasks

import com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory
import org.apache.xerces.util.XMLCatalogResolver
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.xml.sax.SAXParseException

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

public class ValidateTask extends SingleSourceBaseXsltTask {
    private final File validationDir

    File initialRncFile

    ValidateTask() {
        super()
        validationDir = project.file("${workingDir}/validation")
        initialRncFile = project.file("${assetsDir}/docbook-5.0-extension_rwdalpe-rpg/rng/docbook-5.0-extension_rwdalpe-rpg.rnc")
    }

    File getValidationDir() {
        return validationDir
    }

    @Override
    @TaskAction
    public void transform() {
        File validationStylesheet = createSchematronValidationSheet()
        validateFiles(new HashSet<File>([srcFile]), validationStylesheet)
    }

    private void validateFiles(Set<File> toValidate, File validationStylesheet) {
        rncValidateFiles(toValidate)
        schematronValidateFiles(toValidate, validationStylesheet)
    }

    private void schematronValidateFiles(Set<File> toValidate, File schematronValidationStylesheet) {
        URIResolver resolver = createURIResolver()

        TransformerFactory tFactory = TransformerFactory.newInstance()
        Transformer t = tFactory.newTransformer(new StreamSource(schematronValidationStylesheet))
        t.setURIResolver(resolver)

        Set<File> errorsFiles = new HashSet<File>()

        toValidate.each { f ->
            def errorFile = new File("${getValidationDir().absolutePath}/${f.getName()}.schematronerrors".toString())

            t.transform(new StreamSource(f),
                    new StreamResult(f))
            errorsFiles.add(errorFile)
        }

        if (errorsFiles.any { it.length() > 0 }) {
            println("Schematron errors:")
            errorsFiles.each { f ->
                if (f.length() > 0) {
                    println("\nErrors in ${f}: ")
                    println(String.join("\n", f.readLines()))
                }
            }
            throw new TaskExecutionException(this, new IllegalStateException("Schematron errors"))
        }
    }

    private void rncValidateFiles(Set<File> toValidate) {

        XMLCatalogResolver resolver = new XMLCatalogResolver((String[]) (catalogFiles.collect({
            it.absolutePath
        }).toArray()))

        SchemaFactory sFactory = CompactSyntaxSchemaFactory.newInstance()
        Schema validatingSchema = sFactory.newSchema(initialRncFile)

        toValidate.each { f ->
            println "Validating ${f}"

            Validator validator = validatingSchema.newValidator()
            validator.setResourceResolver(resolver)

            try {
                validator.validate(new StreamSource(f))
            } catch (SAXParseException ex) {
                println "Validation error on line ${ex.getLineNumber()}."
                throw new TaskExecutionException(this, ex)
            } catch (Exception ex) {
                throw new TaskExecutionException(this, ex)
            }
        }
    }

    private File createSchematronValidationSheet() {
        if (!validationDir.exists()) {
            validationDir.mkdirs()
        }

        def schematronCreationStylesheet = new File("${assetsDir}/schematron1.5/schematron-basic.xsl".toString())
        def docbookSchematron = new File("${assetsDir}/docbook-5.0/sch/docbook.sch".toString())
        def finalValidationStylesheet = new File("${validationDir}/schematronValidation.xsl".toString())

        TransformerFactory tFactory = TransformerFactory.newInstance()
        tFactory.setURIResolver(createURIResolver())
        Transformer t = tFactory.newTransformer(new StreamSource(schematronCreationStylesheet))
        t.transform(new StreamSource(docbookSchematron),
                new StreamResult(finalValidationStylesheet))
        t.setURIResolver(createURIResolver())


        return finalValidationStylesheet
    }

    @Override
    protected String getMain() {
        throw new IllegalStateException()
    }

    @Override
    protected FileCollection getClasspath() {
        throw new IllegalStateException()
    }

    @Override
    protected Map<String, String> getSysprops() {
        throw new IllegalStateException()
    }

    @Override
    protected List<String> getArgs(File srcFile, Optional<File> outFile, File stylesheet, Map<String, Object> params) {
        throw new IllegalStateException()
    }
}
