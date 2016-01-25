package com.github.rwdalpe.docbookbuild.tasks

import groovy.transform.PackageScope
import org.apache.commons.io.FilenameUtils
import org.apache.fop.apps.Fop
import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.FopFactoryBuilder
import org.apache.fop.apps.MimeConstants
import org.apache.fop.apps.io.InternalResourceResolver
import org.apache.xml.resolver.tools.CatalogResolver
import org.apache.xmlgraphics.io.Resource
import org.apache.xmlgraphics.io.ResourceResolver
import org.gradle.api.tasks.TaskAction
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

public class ToPdfTask extends Xslt1StylesheetsTask {

    File foFile

    ToPdfTask() {
        super()
        initialStylesheet = project.file("${baseStylesheetsDir}/fo/docbook.xsl")
        outputDir = project.file("${workingDir}/docbook-build-pdf/")
    }

    void setOutputFile(File outputFile) {
        this.doLast {
            doPdfTransform(outputFile)
        }
    }

    File getFoFile() {
        if (foFile == null) {
            setFoFile(project.file("${outputDir}/${FilenameUtils.removeExtension(srcFile.getName())}.fo"))
        }

        return foFile
    }

    void setFoFile(File foFile) {
        this.foFile = foFile
    }

    @TaskAction
    @Override
    void transform() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        if(!getFoFile().getParentFile().exists()) {
            getFoFile().getParentFile().mkdirs()
        }

        CatalogResolver resolver = createCatalogResolver()
        TransformerFactory tFactory = createTransformerFactory()
        tFactory.setURIResolver(resolver)

        XMLReader reader = createXmlReader()
        reader.setEntityResolver(resolver)

        Transformer t = tFactory.newTransformer(new StreamSource(initialStylesheet))
        t.setURIResolver(resolver)

        if (params != null) {
            for (String param : params.keySet()) {
                t.setParameter(param, params.get(param))
            }
        }

        SAXSource inFile = new SAXSource(reader, new InputSource(new FileInputStream(srcFile)))
        t.transform(inFile, new StreamResult(getFoFile()))
    }

    @PackageScope doPdfTransform(File outputFile) {
        if(!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs()
        }

        FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(outputDir.toURI())
        FopFactory fopFactory = fopFactoryBuilder.build()

        CatalogResolver resolver = createCatalogResolver()
        TransformerFactory tFactory = createTransformerFactory()
        tFactory.setURIResolver(resolver)

        XMLReader reader = createXmlReader()
        reader.setEntityResolver(resolver)

        Transformer t = tFactory.newTransformer()
        t.setURIResolver(resolver)

        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, new FileOutputStream(outputFile))

        SAXSource inFile = new SAXSource(reader, new InputSource(new FileInputStream(getFoFile())))
        SAXResult pdfFile = new SAXResult(fop.getDefaultHandler())
        t.transform(inFile, pdfFile)
    }
}
