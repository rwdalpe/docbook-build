package com.github.rwdalpe.docbookbuild.tasks
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.apache.commons.io.FilenameUtils
import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.Action
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.TaskAction
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

public class ToEpubTask extends Xslt1StylesheetsTask {

    File baseDir

    ToEpubTask() {
        super()
        initialStylesheet = project.file("${baseStylesheetsDir}/epub3/chunk.xsl")
        outputDir = project.file("${workingDir}/docbook-build-epub/")
    }

    @Override
    @TaskAction
    void transform() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
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


            t.setParameter("base.dir", getBaseDir())
        }

        SAXSource inFile = new SAXSource(reader, new InputSource(new FileInputStream(srcFile)))

        t.transform(inFile, new StreamResult(new StringWriter()))
    }

    public File getBaseDir() {
        if (baseDir == null) {
            baseDir = project.file("${outputDir}/${FilenameUtils.removeExtension(srcFile.getName())}/OEBPS")
        }

        return baseDir
    }

    public ToEpubTask zipTo(String zipFile) {
        return zipTo(project.file(zipFile))
    }

    public ToEpubTask zipTo(GString zipFile) {
        return zipTo(project.file(zipFile))
    }

    public ToEpubTask zipTo(File zipFile) {

        this.doLast({
            File zipParent = zipFile.getParentFile()
            if(!zipParent.exists()) {
                zipParent.mkdirs()
            }

            ZipFile z = new ZipFile(zipFile)
            z.createZipFileFromFolder(owner.getBaseDir().getParentFile(),
                    new ZipParameters() {{
                        setIncludeRootFolder(false)
                        setCompressionLevel(Zip4jConstants.COMP_DEFLATE)
                        setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL)
                    }},
                    false, 0)
        })

        return this
    }

}
