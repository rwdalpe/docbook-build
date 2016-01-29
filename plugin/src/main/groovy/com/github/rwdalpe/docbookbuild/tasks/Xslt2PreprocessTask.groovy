package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import org.gradle.api.file.FileCollection

public class Xslt2PreprocessTask extends MultiSourceBaseXsltTask {
    @Override
    protected String getMain() {
        return "net.sf.saxon.Transform"
    }

    @Override
    protected FileCollection getClasspath() {
        FileCollection saxon = DocbookBuildPlugin.getClasspathForModule(project, "Saxon-HE")
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
            args.push("-o:${outFile.get().absolutePath}")
        }

        args.addAll([
                "-s:${srcFile.absolutePath}",
                "-xsl:${stylesheet.absolutePath}"
        ])

        for (String param : params.keySet()) {
            args.push("${param}=${params.get(param).toString()}")
        }

        return args
    }
}
