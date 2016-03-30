/*
	Copyright (C) 2016 Robert Winslow Dalpe

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import groovy.transform.PackageScope
import org.apache.commons.io.FilenameUtils
import org.apache.fop.apps.Fop
import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.FopFactoryBuilder
import org.apache.fop.apps.MimeConstants
import org.gradle.api.file.FileCollection
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.sax.SAXSource

public class ToPdfTask extends Xslt1StylesheetsTask {

    String foFileName

    ToPdfTask() {
        super()
        initialStylesheet = project.file("${baseStylesheetsDir}/fo/docbook.xsl")
    }

    String getFoFileName() {
        if (foFileName == null) {
            foFileName = "${FilenameUtils.removeExtension(srcFile.getName())}.fo"
        }
        return foFileName
    }

    @Override
    File getOutputDir() {
        if (super.getOutputDir() == null) {
            this.outputDir = project.file("${workingDir}/docbook-build-pdf/${FilenameUtils.removeExtension(srcFile.getName())}")
        }

        return super.getOutputDir()
    }

    void setPdfFile(File pdfFile) {
        this.doLast {
            doPdfTransform(pdfFile)
        }
    }

    @PackageScope
    doPdfTransform(File outputFile) {
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs()
        }

        FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(getFoFile().getParentFile().toURI())
        FopFactory fopFactory = fopFactoryBuilder.build()

        URIResolver resolver = createURIResolver()
        TransformerFactory tFactory = TransformerFactory.newInstance()
        tFactory.setURIResolver(resolver)

        XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader()

        Transformer t = tFactory.newTransformer()
        t.setURIResolver(resolver)

        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, new FileOutputStream(outputFile))

        SAXSource inFile = new SAXSource(reader, new InputSource(new FileInputStream(getFoFile())))
        SAXResult pdfFile = new SAXResult(fop.getDefaultHandler())
        t.transform(inFile, pdfFile)
    }

    @Override
    protected Optional<File> getOutputFile() {
        return Optional.of(getFoFile())
    }

    @Override
    protected String getMain() {
        return "com.icl.saxon.StyleSheet"
    }

    @Override
    protected FileCollection getClasspath() {
        FileCollection saxon = DocbookBuildPlugin.getClasspathForModule(project, "saxon")
        FileCollection resolver = DocbookBuildPlugin.getClasspathForModule(project, "xml-resolver")
        FileCollection xerces = DocbookBuildPlugin.getClasspathForModule(project, "xerces")

        return saxon.plus(resolver).plus(xerces)
    }

    @Override
    protected Map<String, String> getSysprops() {
        return [
                "javax.xml.parsers.SAXParserFactory"                 : "org.apache.xerces.jaxp.SAXParserFactoryImpl",
                "org.apache.xerces.xni.parser.XMLParserConfiguration": "org.apache.xerces.parsers.XIncludeParserConfiguration",
                "xml.catalog.files"                                  : catalogFiles.collect({
                    it.absolutePath
                }).join(";")
        ]
    }

    @Override
    protected List<String> getArgs(File srcFile, Optional<File> outFile, File stylesheet, Map<String, Object> params) {
        def args = [
                "-r",
                "org.apache.xml.resolver.tools.CatalogResolver",
                "-x",
                "org.apache.xml.resolver.tools.ResolvingXMLReader",
                "-y",
                "org.apache.xml.resolver.tools.ResolvingXMLReader"
        ]

        if (outFile.isPresent()) {
            args.push("-o")
            args.push(outFile.get().absolutePath)
        }

        args.addAll([
                srcFile.absolutePath,
                stylesheet.absolutePath
        ])

        for (String param : params.keySet()) {
            args.push("${param}=${params.get(param).toString()}")
        }

        return args
    }

    private File getFoFile() {
        return project.file("${outputDir}/${getFoFileName()}")
    }
}
