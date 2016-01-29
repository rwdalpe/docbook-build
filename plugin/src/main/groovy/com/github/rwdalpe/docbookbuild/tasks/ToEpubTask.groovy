package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.apache.commons.io.FilenameUtils
import org.gradle.api.file.FileCollection

public class ToEpubTask extends Xslt1StylesheetsTask {

    File baseDir

    ToEpubTask() {
        super()
        initialStylesheet = project.file("${baseStylesheetsDir}/epub3/chunk.xsl")
        outputDir = project.file("${workingDir}/docbook-build-epub/")
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
            if (!zipParent.exists()) {
                zipParent.mkdirs()
            }

            ZipFile z = new ZipFile(zipFile)
            z.createZipFileFromFolder(owner.getBaseDir().getParentFile(),
                    new ZipParameters() {
                        {
                            setIncludeRootFolder(false)
                            setCompressionLevel(Zip4jConstants.COMP_DEFLATE)
                            setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL)
                        }
                    },
                    false, 0)
        })

        return this
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
            if(!param.equals("base.dir")) {
                args.push("${param}=${params.get(param).toString()}")
            }
        }

        args.push("base.dir=${getBaseDir().absolutePath}")

        return args
    }

    @Override
    protected Optional<File> getOutputFile() {
        return Optional.empty()
    }

}
