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
import org.gradle.api.file.FileCollection

public class Xslt1PreprocessTask extends Xslt1StylesheetsTask {

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
                "javax.xml.parsers.SAXParserFactory": "org.apache.xerces.jaxp.SAXParserFactoryImpl",
                "org.apache.xerces.xni.parser.XMLParserConfiguration": "org.apache.xerces.parsers.XIncludeParserConfiguration",
                "xml.catalog.files": catalogFiles.collect({
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
}
