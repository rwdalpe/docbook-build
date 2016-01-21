package com.github.rwdalpe.docbookbuild.tasks

import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.tasks.TaskAction

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

public class Xslt1PreprocessTask extends BaseXsltPreprocessTask {

    @Override
    protected TransformerFactory createTransformerFactory() {
        return createXslt1TransformerFactory()
    }
}
