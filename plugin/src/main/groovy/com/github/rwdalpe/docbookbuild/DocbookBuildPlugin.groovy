/*
	Copyright (C) 2016 Robert Winslow Dalpe

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

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