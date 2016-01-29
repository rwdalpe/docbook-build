package com.github.rwdalpe.docbookbuild

import com.github.rwdalpe.docbookbuild.tasks.PrepareAssetsTask
import groovy.transform.PackageScope
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

public class DocbookBuildPlugin implements Plugin<Project> {
    @PackageScope
    static Properties pluginProperties = null
    @PackageScope
    static String assetsDirName = "docbook-build-assets"

    @PackageScope
    static File getAssetsDir(Project project) {
        return project.file("${project.docbookbuild.assetsExtractionDir}/${DocbookBuildPlugin.assetsDirName}")
    }

    @PackageScope
    static File getWorkingDir(Project project) {
        return project.docbookbuild.workingDir
    }

    @PackageScope
    static FileCollection combineFileCollection(Project project, FileCollection ... collections) {
        def files = []
        for(FileCollection collection : collections) {
            files.addAll(collection.files)
        }

        return project.files(files)
    }

    @PackageScope
    static FileCollection getClasspathForModule(Project project, String moduleName) {
        return getClasspathForModule(project, moduleName, [])
    }

    @PackageScope
    static FileCollection getClasspathForModule(Project project, String moduleName, List<String> excludedModules) {
        def classpath = []

        project.buildscript.configurations.classpath.resolvedConfiguration.firstLevelModuleDependencies.each({ dep ->
            if (dep.moduleName.equals("docbook-build")) {
                dep.children.each { child ->
                    if (child.moduleName.equals(moduleName)) {
                        Set<String> forbiddenArtifactIdentifiers = excludedModules
                        child.allModuleArtifacts.each { artifact ->
                            if (!forbiddenArtifactIdentifiers.contains(artifact.getName())) {
                                if (!forbiddenArtifactIdentifiers.contains("${artifact.getName()}:${artifact.getModuleVersion().getId().getVersion()}".toString())) {
                                    classpath.push(artifact.getFile())
                                }
                            }
                        }
                        classpath = classpath.reverse()
                    }
                }
            }
        })

        return project.files(classpath)
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