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
import com.xmlcalabash.XMLCalabashExec
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaExecSpec

public class ToHtmlTask extends XMLCalabashExec {
    public enum HtmlMode {
        SINGLE
    }

    private
    final File baseStylesheetsDir = project.file("${DocbookBuildPlugin.getAssetsDir(project)}/${DocbookBuildPlugin.pluginProperties.getProperty("libXslt2Stylesheets")}")

    HtmlMode htmlMode

    File initialPipeline
    Map<String, Object> pipelineOptions
    Map<String, Object> stylesheetParams
    File inputFile
    File outputFile
    File outputDir
    Set<File> catalogFiles

    ToHtmlTask() {
        this.htmlMode = HtmlMode.SINGLE
        pipelineOptions = new HashMap<>()
        stylesheetParams = new HashMap<>()
        initialPipeline = project.file("${baseStylesheetsDir}/xslt/base/pipelines/db2html.xpl")
    }

    File getOutputFile() {
        if (outputFile == null) {
            outputFile = project.file("${outputDir}/index.html")
        }

        return outputFile
    }

    File getOutputDir() {
        if (outputDir == null) {
            outputDir = project.file("${DocbookBuildPlugin.getWorkingDir(project)}/docbook-build-html/${FilenameUtils.removeExtension(inputFile.getName())}")
        }

        return outputDir
    }

    void setInitialPipeline(File initialPipeline) {
        this.initialPipeline = initialPipeline
        this.setPipeline(initialPipeline.absolutePath)
    }

    void setInputFile(File inputFile) {
        this.inputFile = inputFile
    }

    void setOutputFile(File outputFile) {
        this.outputFile = outputFile
    }

    public ToHtmlTask copy(Action<? super CopySpec> action) {
        this.doLast {
            project.copy(action)
        }
        return this
    }

    public ToHtmlTask copy(Closure closure) {
        this.doLast {
            project.copy(closure)
        }
        return this
    }

    @TaskAction
    void exec() {
        if (!getOutputFile().getParentFile().exists()) {
            getOutputFile().getParentFile().mkdirs()
        }

        if (!getOutputDir().exists()) {
            getOutputDir().mkdirs()
        }

        this.setClasspath(project.files("${DocbookBuildPlugin.getAssetsDir(project)}/"))
        this.classpath(project.files("${DocbookBuildPlugin.getAssetsDir(project)}/${DocbookBuildPlugin.pluginProperties.getProperty("libXslt2StylesheetsExtensions")}/*"))

        this.classpath(DocbookBuildPlugin.getClasspathForModule(project, "xmlcalabash", ["saxon", "xml-apis", "xercesImpl", "isorelax:20030108"]))

        this.input("source", getInputFile().absolutePath)
        this.output("result", getOutputFile().absolutePath)
        this.setPipeline(initialPipeline.absolutePath)

        this.setSystemProperties([
                "xml.catalog.files": catalogFiles.collect({ it.absolutePath }).join(";")
        ])

        for (String option : pipelineOptions.keySet()) {
            this.option(option, pipelineOptions.get(option).toString())
        }

        for (String param : stylesheetParams.keySet()) {
            this.param(param, stylesheetParams.get(param).toString())
        }

        super.exec()
    }
}
