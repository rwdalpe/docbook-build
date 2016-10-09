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
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaExecSpec

public class ToHtmlTask extends DefaultTask {
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

        def classpathFiles = []

        classpathFiles.push("${DocbookBuildPlugin.getAssetsDir(project)}/")
        classpathFiles.push("${DocbookBuildPlugin.getAssetsDir(project)}/${DocbookBuildPlugin.pluginProperties.getProperty("libXslt2StylesheetsExtensions")}/*")

        def sysProps = [
            "xml.catalog.files": catalogFiles.collect({ it.absolutePath }).join(";")
        ]

        def options = []
        def params = []

        for (String option : pipelineOptions.keySet()) {
            options.push("${option}=${pipelineOptions.get(option).toString()}")
        }

        for (String param : stylesheetParams.keySet()) {
            params.push("-p${param}=${stylesheetParams.get(param).toString()}")
        }

        def args = [
                "--entity-resolver=org.xmlresolver.Resolver",
                "--uri-resolver=org.xmlresolver.Resolver",
                "-oresult=${getOutputFile().absolutePath}",
                "-isource=${getInputFile().absolutePath}",
        ]

        args.addAll(params)
        args.push(getInitialPipeline().absolutePath)
        args.addAll(options)

        def execClassPath = DocbookBuildPlugin.getClasspathForModule(project, "xmlcalabash").plus(project.files(classpathFiles))

        project.javaexec(new Action<JavaExecSpec>() {
            @Override
            void execute(JavaExecSpec javaExecSpec) {
                javaExecSpec.setClasspath(project.files(execClassPath))
                javaExecSpec.setMain("com.xmlcalabash.drivers.Main")
                javaExecSpec.setSystemProperties(sysProps)
                javaExecSpec.setArgs(args)
            }
        })
    }
}
