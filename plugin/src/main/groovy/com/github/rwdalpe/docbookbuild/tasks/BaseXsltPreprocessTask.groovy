package com.github.rwdalpe.docbookbuild.tasks

import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.tasks.TaskAction
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

public abstract class BaseXsltPreprocessTask extends MultiSourceBaseXsltTask {
    File outputDir
    String suffix
    Map<File, Map<String, Object>> stylesheetChain
    Closure forEachSrc

    BaseXsltPreprocessTask() {
        super()
        outputDir = project.file("${workingDir}/${this.name}-working")
        stylesheetChain = new HashMap<>()
        suffix = ".preprocessed"
    }

    @TaskAction
    public void preprocess() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        CatalogResolver resolver = createCatalogResolver()
        TransformerFactory tFactory = createTransformerFactory()

        for (File srcFile : srcFiles) {
            File outFile = project.file("${outputDir}/${srcFile.getName()}${suffix}")

            boolean usePreprocessed = false
            for (File stylesheet : stylesheetChain.keySet()) {
                Map<String, Object> params = stylesheetChain.get(stylesheet)

                XMLReader reader = createXmlReader()
                reader.setEntityResolver(resolver)

                Transformer t = tFactory.newTransformer(new StreamSource(stylesheet))
                t.setURIResolver(resolver)

                if (params != null) {
                    for (String param : params.keySet()) {
                        t.setParameter(param, params.get(param))
                    }
                }

                SAXSource inFile = null

                if(usePreprocessed) {
                    inFile = new SAXSource(reader, new InputSource(new FileInputStream(outFile)))
                } else {
                    inFile = new SAXSource(reader, new InputSource(new FileInputStream(srcFile)))
                }

                t.transform(inFile, new StreamResult(outFile))
            }

            if(forEachSrc != null) {
                forEachSrc(srcFile, outFile)
            }
        }
    }

    protected abstract TransformerFactory createTransformerFactory();

}
