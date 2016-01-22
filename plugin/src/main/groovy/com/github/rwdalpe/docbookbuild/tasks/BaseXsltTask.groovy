package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import org.apache.xerces.jaxp.SAXParserFactoryImpl
import org.apache.xerces.util.XMLCatalogResolver
import org.apache.xml.resolver.CatalogManager
import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.DefaultTask
import org.xml.sax.XMLReader

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.TransformerFactory

public abstract class BaseXsltTask extends DefaultTask {
    protected final File assetsDir;
    protected final File workingDir

    Set<File> catalogFiles

    BaseXsltTask() {
        super()
        assetsDir = DocbookBuildPlugin.getAssetsDir(project)
        workingDir = DocbookBuildPlugin.getWorkingDir(project)
        catalogFiles = new HashSet<>()
    }

    protected CatalogResolver createCatalogResolver() {
        CatalogManager manager = new CatalogManager()
        manager.setCatalogFiles(catalogFiles.join(";"))
        manager.setIgnoreMissingProperties(true)

        return new CatalogResolver(manager)
    }

    protected XMLCatalogResolver createXmlCatalogResolver() {
        return new XMLCatalogResolver((String[]) (catalogFiles.collect { it.absolutePath }))
    }

    protected static TransformerFactory createXslt1TransformerFactory() {
        return new com.icl.saxon.TransformerFactoryImpl()
    }

    protected static TransformerFactory createXslt2TransformerFactory() {
        return new net.sf.saxon.TransformerFactoryImpl()
    }

    protected static SAXParserFactory createXmlParserFactory() {
        return new SAXParserFactoryImpl()
    }

    protected static XMLReader createXmlReader(SAXParser parser) {
        return parser.getXMLReader()
    }

    protected static XMLReader createXmlReader() {
        return createXmlParserFactory()
                .newSAXParser()
                .getXMLReader()
    }

}
