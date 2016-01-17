package com.github.rwdalpe.docbookbuild

import com.github.rwdalpe.docbookbuild.tasks.PrepareAssetsTask
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project

public class DocbookBuildPlugin implements Plugin<Project> {
    @PackageScope static Properties pluginProperties = null

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