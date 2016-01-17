package com.github.rwdalpe.docbookbuild

import org.gradle.api.Project

public class DocbookBuildPluginExtension {
    GString assetsExtractionDir

    DocbookBuildPluginExtension(Project project) {
        assetsExtractionDir = "${project.buildDir}/docbook-build-assets"
    }
}
