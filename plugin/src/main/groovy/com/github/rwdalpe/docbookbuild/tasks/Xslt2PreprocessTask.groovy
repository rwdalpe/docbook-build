package com.github.rwdalpe.docbookbuild.tasks

import javax.xml.transform.TransformerFactory

public class Xslt2PreprocessTask extends BaseXsltPreprocessTask {
    @Override
    protected TransformerFactory createTransformerFactory() {
        return createXslt2TransformerFactory()
    }
}
