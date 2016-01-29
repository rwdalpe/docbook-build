package com.github.rwdalpe.docbookbuild.tasks

import org.gradle.api.tasks.TaskAction

public abstract class Xslt1StylesheetsTask extends SingleSourceBaseXsltTask {
    protected final File baseStylesheetsDir

    File initialStylesheet
    Map<String, Object> params
    File outputDir

    Xslt1StylesheetsTask() {
        super()
        params = new HashMap<>()
        baseStylesheetsDir = project.file("${assetsDir}/docbook-xsl-ns-1.78.1/")
    }

    @TaskAction
    public void transform() {
        if (!getOutputDir().exists()) {
            getOutputDir().mkdirs()
        }

        super.doTransform(getMain(),
                getClasspath(),
                getSysprops(),
                getArgs(srcFile, getOutputFile(), initialStylesheet, params)
        )
    }

    protected abstract Optional<File> getOutputFile()
}
