package com.github.rwdalpe.docbookbuild.tasks

import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.tasks.TaskAction

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

public abstract class BaseXsltPreprocessTask extends BaseXsltTask {
    File outputDir
    String suffix
    Map<File, Map<String,String>> stylesheetChain

    BaseXsltPreprocessTask() {
        super()
        outputDir = project.file("${workingDir}/${this.name}-working")
        stylesheetChain = new HashMap<>()
        suffix = "preprocessed"
    }

    File getOutputDir() {
        return outputDir
    }

    void setOutputDir(File outputDir) {
        this.outputDir = outputDir
    }

    List<File> getStylesheetChain() {
        return stylesheetChain
    }

    void setStylesheetChain(Map<File, Map<String,String>> stylesheetChain) {
        this.stylesheetChain = stylesheetChain
    }

    String getSuffix() {
        return suffix
    }

    void setSuffix(String suffix) {
        this.suffix = suffix
    }

    @TaskAction
    public void preprocess() {
        if(!outputDir.exists()) {
            outputDir.mkdirs()
        }

        CatalogResolver resolver = createCatalogResolver()
        TransformerFactory tFactory = createXslt1TransformerFactory()

        for(File stylesheet : stylesheetChain.keySet()) {
            Map<String, String> params = stylesheetChain.get(stylesheet)

            Transformer t = tFactory.newTransformer(new StreamSource(stylesheet))
            t.setURIResolver(resolver)

            if(params != null) {
                for(String param : params.keySet()) {
                    t.setParameter(param, params.get(param))
                }
            }

            for(File srcFile : srcFiles) {
                File outFile = project.file("${outputDir}/${srcFile.getName()}.${suffix}")

                t.transform(new StreamSource(srcFile), new StreamResult(outFile))
            }
        }
    }

    protected abstract TransformerFactory createTransformerFactory();

}
