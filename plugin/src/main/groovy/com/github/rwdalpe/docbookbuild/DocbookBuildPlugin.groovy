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

    @Override
    void apply(Project project) {
        pluginProperties = getPluginProperties()

        project.extensions.create("docbookbuild", DocbookBuildPluginExtension, project)
        project.task("prepareAssets", type: PrepareAssetsTask)
        project.task("validate", type:ValidateTask)
    }

    Properties getPluginProperties() {
        Properties prop = new Properties()
        String propFileName = "docbook-build.properties"

        InputStream propFileInputStream = getClass().getClassLoader().getResourceAsStream(propFileName)

        prop.load(propFileInputStream)

        return prop
    }
}