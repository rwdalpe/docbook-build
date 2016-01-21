package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import org.apache.xerces.util.XMLCatalogResolver
import org.apache.xml.resolver.CatalogManager
import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.DefaultTask

import javax.xml.transform.TransformerFactory

public abstract class BaseXsltTask extends DefaultTask {
    protected final File assetsDir;
    protected final File workingDir

    Set<File> srcFiles
    Set<File> catalogFiles

    BaseXsltTask() {
        super()
        assetsDir = DocbookBuildPlugin.getAssetsDir(project)
        workingDir = DocbookBuildPlugin.getWorkingDir(project)
        catalogFiles = new HashSet<>()
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
        return createXsltTransformerFactory("com.icl.saxon.TransformerFactoryImpl")
    }

    protected static TransformerFactory createXslt2TransformerFactory() {
        return createXslt1TransformerFactory("net.sf.saxon.TransformerFactoryImpl")
    }

    private static TransformerFactory createXsltTransformerFactory(String tFactoryImpl) {
        System.setProperty("javax.xml.transform.TransformerFactory", tFactoryImpl)
        System.setProperty('javax.xml.parsers.SAXParserFactory', 'org.apache.xerces.jaxp.SAXParserFactoryImpl')
        System.setProperty('org.apache.xerces.xni.parser.XMLParserConfiguration', 'org.apache.xerces.parsers.XIncludeParserConfiguration')

        TransformerFactory tFactory = TransformerFactory.newInstance()
        return tFactory
    }
}
