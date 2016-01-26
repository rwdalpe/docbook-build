package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import com.xmlcalabash.XMLCalabashExec
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaExecSpec

public class ToHtmlTask
//        extends DefaultTask {
extends XMLCalabashExec {
    public enum HtmlMode {
        SINGLE
        //, CHUNKED
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
//        println(this.getClasspath().collect({ it.absolutePath }))
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

    //@Override
    @TaskAction
    void exec() {
        if (!getOutputFile().getParentFile().exists()) {
            getOutputFile().getParentFile().mkdirs()
        }

        if (!getOutputDir().exists()) {
            getOutputDir().mkdirs()
        }

//        def classpathFiles = []

        this.setClasspath(project.files("${DocbookBuildPlugin.getAssetsDir(project)}/"))
//        classpathFiles.push("${DocbookBuildPlugin.getAssetsDir(project)}/")
        this.classpath(project.files("${DocbookBuildPlugin.getAssetsDir(project)}/${DocbookBuildPlugin.pluginProperties.getProperty("libXslt2StylesheetsExtensions")}/*"))
//        classpathFiles.push("${DocbookBuildPlugin.getAssetsDir(project)}/${DocbookBuildPlugin.pluginProperties.getProperty("libXslt2StylesheetsExtensions")}/*")
        project.buildscript.configurations.classpath.resolvedConfiguration.firstLevelModuleDependencies.each({ dep ->
            if (dep.moduleName.equals("docbook-build")) {
                dep.children.each { child ->
                    if (child.moduleName.equals("xmlcalabash")) {
                        Set<String> forbiddenArtifactIdentifiers = ["saxon", "xml-apis", "xercesImpl", "isorelax:20030108"]
                        def calabashClasspaths = []
                        child.allModuleArtifacts.each { artifact ->
                            if (!forbiddenArtifactIdentifiers.contains(artifact.getName())) {
                                if (!forbiddenArtifactIdentifiers.contains("${artifact.getName()}:${artifact.getModuleVersion().getId().getVersion()}".toString())) {
                                    calabashClasspaths.push(artifact.getFile())
                                }
                            }
                        }
                        calabashClasspaths = calabashClasspaths.reverse()
                        this.classpath(project.files(calabashClasspaths))
//                        classpathFiles.addAll(calabashClasspaths)
                    }

                }
            }
        })

        this.input("source", getInputFile().absolutePath)
        this.output("result", getOutputFile().absolutePath)
        this.setPipeline(initialPipeline.absolutePath)
//        this.setEntityResolver("org.xmlresolver.Resolver")
//        this.setUriResolver("org.xmlresolver.Resolver")

        this.setSystemProperties([
                "xml.catalog.files": catalogFiles.collect({ it.absolutePath }).join(";")
        ])

//        def sysProps = [
//            "xml.catalog.files": catalogFiles.collect({ it.absolutePath }).join(";")
//        ]

        if (!pipelineOptions.containsKey("preprocess-params-file")) {
            pipelineOptions.put("preprocess-params-file", project.file("${baseStylesheetsDir}/xslt/base/preprocess/preprocess-defaultparams.xsl"))
        }

//        def options = []
//        def params = []

        for (String option : pipelineOptions.keySet()) {
            this.option(option, pipelineOptions.get(option).toString())
//            options.push("${option}=${pipelineOptions.get(option).toString()}")
        }

        for (String param : stylesheetParams.keySet()) {
            this.param(param, stylesheetParams.get(param).toString())
//            params.push("-p${param}=${stylesheetParams.get(param).toString()}")
        }

//        def args = [
//                "--entity-resolver=org.xmlresolver.Resolver",
//                "--uri-resolver=org.xmlresolver.Resolver",
//                "-oresult=${getOutputFile().absolutePath}",
//                "-isource=${getInputFile().absolutePath}",
//        ]
//
//        args.addAll(params)
//        args.push(getInitialPipeline().absolutePath)
//        args.addAll(options)
//
//        project.javaexec(new Action<JavaExecSpec>() {
//            @Override
//            void execute(JavaExecSpec javaExecSpec) {
//                javaExecSpec.setClasspath(project.files(classpathFiles))
//                javaExecSpec.setMain("com.xmlcalabash.drivers.Main")
//                javaExecSpec.setSystemProperties(sysProps)
//                javaExecSpec.setArgs(args)
//            }
//        })

        super.exec()
    }
}
