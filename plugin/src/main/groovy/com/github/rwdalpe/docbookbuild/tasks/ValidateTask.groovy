package com.github.rwdalpe.docbookbuild.tasks

import com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory
import org.apache.xerces.util.XMLCatalogResolver
import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.xml.sax.SAXParseException

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

public class ValidateTask extends MultiSourceBaseXsltTask {
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

    @TaskAction
    public void validate() {
        File validationStylesheet = createSchematronValidationSheet()
        validateFiles(srcFiles, validationStylesheet)
    }

    private void validateFiles(Set<File> toValidate, File validationStylesheet) {
        rncValidateFiles(toValidate)
        schematronValidateFiles(toValidate, validationStylesheet)
    }

    private void schematronValidateFiles(Set<File> toValidate, File schematronValidationStylesheet) {
        CatalogResolver resolver = createCatalogResolver()

        TransformerFactory tFactory = createXslt1TransformerFactory()
        Transformer t = tFactory.newTransformer(new StreamSource(schematronValidationStylesheet))
        t.setURIResolver(resolver)

        Set<File> errorsFiles = new HashSet<File>()

        toValidate.each { f ->
            def errorFile = new File("${getValidationDir().absolutePath}/${f.getName()}.schematronerrors".toString())
            t.transform(new StreamSource(f),
                    new StreamResult(errorFile))
            errorsFiles.add(errorFile)
        }

        if(errorsFiles.any {it.length() > 0}) {
            println("Schematron errors:")
            errorsFiles.each { f ->
                if(f.length() > 0) {
                    println("\nErrors in ${f}: ")
                    println(String.join("\n", f.readLines()))
                }
            }
            throw new TaskExecutionException(this, new IllegalStateException("Schematron errors"))
        }
    }

    private void rncValidateFiles(Set<File> toValidate) {

        XMLCatalogResolver resolver = createXmlCatalogResolver()

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

        TransformerFactory tFactory = createXslt1TransformerFactory()
        Transformer t = tFactory.newTransformer(new StreamSource(schematronCreationStylesheet))
        t.transform(new StreamSource(docbookSchematron),
                new StreamResult(finalValidationStylesheet))

        return finalValidationStylesheet
    }
}
