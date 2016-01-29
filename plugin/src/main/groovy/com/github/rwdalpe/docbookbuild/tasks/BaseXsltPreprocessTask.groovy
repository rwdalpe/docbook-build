package com.github.rwdalpe.docbookbuild.tasks

import org.gradle.api.tasks.TaskAction

public abstract class BaseXsltPreprocessTask extends MultiSourceBaseXsltTask {
    File outputDir
    String suffix
    Map<File, Map<String, Object>> stylesheetChain
    Closure forEachSrc

    BaseXsltPreprocessTask() {
        super()
        stylesheetChain = new HashMap<>()
        suffix = ".preprocessed"
    }

    public File getOutputDir() {
        if (outputDir == null) {
            outputDir = project.file("${workingDir}/${this.name}-working")
        }

        return outputDir
    }

    @TaskAction
    public void preprocess() {
        if (!getOutputDir().exists()) {
            getOutputDir().mkdirs()
        }

        for (File srcFile : srcFiles) {
            File outFile = project.file("${getOutputDir()}/${srcFile.getName()}${suffix}")

            boolean usePreprocessed = false
            for (File stylesheet : stylesheetChain.keySet()) {
                Map<String, Object> params = stylesheetChain.get(stylesheet)

                File useFile = null

                if (usePreprocessed) {
                    useFile = outFile
                } else {
                    useFile = srcFile
                }

                super.doTransform(getMain(),
                        getClasspath(),
                        getSysprops(),
                        getArgs(srcFile, Optional.of(useFile), stylesheet, params)
                )
            }

            if (forEachSrc != null) {
                forEachSrc(srcFile, outFile)
            }
        }
    }
}
