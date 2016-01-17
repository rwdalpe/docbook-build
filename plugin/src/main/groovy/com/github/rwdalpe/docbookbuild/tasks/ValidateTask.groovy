package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import com.icl.saxon.StyleSheet
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

public class ValidateTask extends DefaultTask {
    private File assetsDir = project.file("${project.docbookbuild.assetsExtractionDir}/${DocbookBuildPlugin.assetsDirName}")
    private File workingDir = project.docbookbuild.workingDir
    private File validationDir = project.file("${workingDir}/validation")

    @TaskAction
    public void validate() {
        createSchematronValidationSheet()
    }

    private void createSchematronValidationSheet() {
        if (!validationDir.exists()) {
            validationDir.mkdirs()
        }

        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

        TransformerFactory tFactory = TransformerFactory.newInstance()
        Transformer t = tFactory.newTransformer(new StreamSource(new File("${assetsDir}/schematron1.5/schematron-basic.xsl".toString())))
        t.transform(new StreamSource(new File("${assetsDir}/docbook-5.0/sch/docbook.sch".toString())),
                new StreamResult(new File("${validationDir}/schematronValidation.xsl".toString())))
    }
}
