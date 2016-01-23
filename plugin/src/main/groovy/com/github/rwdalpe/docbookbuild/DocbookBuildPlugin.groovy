package com.github.rwdalpe.docbookbuild

import com.github.rwdalpe.docbookbuild.tasks.PrepareAssetsTask
import com.github.rwdalpe.docbookbuild.tasks.ValidateTask
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class DocbookBuildPlugin implements Plugin<Project> {
    @PackageScope static Properties pluginProperties = null
    @PackageScope static String assetsDirName = "docbook-build-assets"

    @PackageScope static File getAssetsDir(Project project) {
        return project.file("${project.docbookbuild.assetsExtractionDir}/${DocbookBuildPlugin.assetsDirName}")
    }

    @PackageScope static File getWorkingDir(Project project) {
        return project.docbookbuild.workingDir
    }

    @Override
    void apply(Project project) {
        pluginProperties = getPluginProperties()

        project.extensions.create("docbookbuild", DocbookBuildPluginExtension, project)
        project.task("prepareAssets", type: PrepareAssetsTask)
    }

    Properties getPluginProperties() {
        Properties prop = new Properties()
        String propFileName = "docbook-build.properties"

        InputStream propFileInputStream = getClass().getClassLoader().getResourceAsStream(propFileName)

        prop.load(propFileInputStream)

        return prop
    }
}