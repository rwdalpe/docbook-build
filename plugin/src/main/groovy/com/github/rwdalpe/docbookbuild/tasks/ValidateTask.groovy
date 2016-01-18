package com.github.rwdalpe.docbookbuild.tasks
import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory
import groovy.transform.PackageScope
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

    File initialRncFile = project.file("${assetsDir}/docbook-5.0-extension_rwdalpe-rpg/rng/docbook-5.0-extension_rwdalpe-rpg.rnc")
    Set<File> srcFiles
    Set<File> catalogFiles

    File getValidationDir() {
        return validationDir
    }

    File getInitialRncFile() {
        return initialRncFile
    }

    void setInitialRncFile(File initialRncFile) {
        this.initialRncFile = initialRncFile
    }

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
        schematronValidateFiles(toValidate, validationStylesheet)
    }

    private void schematronValidateFiles(Set<File> toValidate, File schematronValidationStylesheet) {
        XMLCatalogResolver resolver = new XMLCatalogResolver((String[])(catalogFiles.collect { it.absolutePath }))

        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

        TransformerFactory tFactory = TransformerFactory.newInstance()
        Transformer t = tFactory.newTransformer(new StreamSource(schematronValidationStylesheet))

        Set<File> errorsFiles = new HashSet<File>()

        toValidate.each { f ->
            def errorFile = new File("${getValidationDir().absolutePath}/${f}.schematronerrors".toString())
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

        XMLCatalogResolver resolver = new XMLCatalogResolver((String[])(catalogFiles.collect { it.absolutePath }))

        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

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


        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

        def schematronCreationStylesheet = new File("${assetsDir}/schematron1.5/schematron-basic.xsl".toString())
        def docbookSchematron = new File("${assetsDir}/docbook-5.0/sch/docbook.sch".toString())
        def finalValidationStylesheet = new File("${validationDir}/schematronValidation.xsl".toString())

        TransformerFactory tFactory = TransformerFactory.newInstance()
        Transformer t = tFactory.newTransformer(new StreamSource(schematronCreationStylesheet))
        t.transform(new StreamSource(docbookSchematron),
                new StreamResult(finalValidationStylesheet))

        return finalValidationStylesheet
    }
}
