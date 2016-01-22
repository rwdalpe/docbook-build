package com.github.rwdalpe.docbookbuild.tasks

import javax.xml.transform.TransformerFactory

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

    public abstract void transform()

    protected TransformerFactory createTransformerFactory() {
        return createXslt1TransformerFactory()
    }
}
