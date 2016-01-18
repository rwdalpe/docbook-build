package com.github.rwdalpe.docbookbuild.tasks
import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory
import org.apache.xerces.util.XMLCatalogResolver
import org.gradle.api.DefaultTask
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

public class ValidateTask extends DefaultTask {
    private File assetsDir = project.file("${project.docbookbuild.assetsExtractionDir}/${DocbookBuildPlugin.assetsDirName}")
    private File workingDir = project.docbookbuild.workingDir
    private File validationDir = project.file("${workingDir}/validation")
    private final File schematronErrorsFile = project.file("${validationDir}/schematronErrors.txt")

    Set<File> srcFiles
    Set<File> catalogFiles

    Set<File> getCatalogFiles() {
        return catalogFiles
    }

    void setCatalogFiles(Set<File> catalogFiles) {
        this.catalogFiles = catalogFiles
    }

    Set<File> getSrcFiles() {
        return srcFiles
    }

    void setSrcFiles(Set<File> srcFiles) {
        this.srcFiles = srcFiles
    }

    @TaskAction
    public void validate() {
        File validationStylesheet = createSchematronValidationSheet()
        validateFiles(srcFiles, validationStylesheet)
    }

    private void validateFiles(Set<File> toValidate, File validationStylesheet) {
        rncValidateFiles(toValidate)
    }

    private void rncValidateFiles(Set<File> toValidate) {
        File validationRncFile = project.file("${assetsDir}/docbook-5.0-extension_rwdalpe-rpg/rng/docbook-5.0-extension_rwdalpe-rpg.rnc")

        XMLCatalogResolver resolver = new XMLCatalogResolver((String[])(catalogFiles.collect { it.absolutePath }))

        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

        SchemaFactory sFactory = CompactSyntaxSchemaFactory.newInstance()
        Schema validatingSchema = sFactory.newSchema(validationRncFile)

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

        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

        def validationOutput = new File("${assetsDir}/schematron1.5/schematron-basic.xsl".toString())

        TransformerFactory tFactory = TransformerFactory.newInstance()
        Transformer t = tFactory.newTransformer(new StreamSource(validationOutput))
        t.transform(new StreamSource(new File("${assetsDir}/docbook-5.0/sch/docbook.sch".toString())),
                new StreamResult(new File("${validationDir}/schematronValidation.xsl".toString())))

        return validationOutput
    }
}
