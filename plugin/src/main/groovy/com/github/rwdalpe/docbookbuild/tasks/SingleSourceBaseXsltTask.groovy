package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import org.apache.commons.io.FilenameUtils
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskAction

public abstract class SingleSourceBaseXsltTask extends BaseXsltTask {
    File srcFile
    File outputDir
    String outputFileName
    File initialStylesheet
    Map<String, Object> params

    SingleSourceBaseXsltTask() {
        super()
        outputDir = project.file("${workingDir}/docbook-build-${name}/")
        this.doLast {
            FileTree f = project.fileTree(getOutputDir().absolutePath).include("**/*.tmpPlaceholderDocbookBuild")
            project.delete(f.getFiles())
        }
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

    protected Optional<File> getOutputFile() {
        if(outputFileName != null) {
            return Optional.of(project.file("${getOutputDir().absolutePath}/${outputFileName}"))
        } else {
            return Optional.of(project.file("${getOutputDir().absolutePath}/${FilenameUtils.removeExtension(srcFile.getName())}.tmpPlaceholderDocbookBuild"))
        }
    }
}
