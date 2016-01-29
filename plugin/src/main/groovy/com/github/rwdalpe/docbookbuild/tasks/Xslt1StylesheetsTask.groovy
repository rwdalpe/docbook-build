package com.github.rwdalpe.docbookbuild.tasks

import org.gradle.api.tasks.TaskAction

public abstract class Xslt1StylesheetsTask extends SingleSourceBaseXsltTask {
    protected final File baseStylesheetsDir

    Xslt1StylesheetsTask() {
        super()
        params = new HashMap<>()
        baseStylesheetsDir = project.file("${assetsDir}/docbook-xsl-ns-1.78.1/")
    }
}
