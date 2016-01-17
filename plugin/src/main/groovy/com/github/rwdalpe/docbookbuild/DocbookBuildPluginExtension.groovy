package com.github.rwdalpe.docbookbuild

import org.gradle.api.Project

public class DocbookBuildPluginExtension {
    File assetsExtractionDir
    File workingDir

    DocbookBuildPluginExtension(Project project) {
        assetsExtractionDir = project.file("${project.buildDir}/${DocbookBuildPlugin.assetsDirName}")
        workingDir = project.file("${project.buildDir}/docbook-build-working")
    }
}
