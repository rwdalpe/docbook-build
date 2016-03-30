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

package com.github.rwdalpe.docbookbuild.tasks

import com.github.rwdalpe.docbookbuild.DocbookBuildPlugin
import org.apache.xml.resolver.CatalogManager
import org.apache.xml.resolver.tools.CatalogResolver
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.process.JavaExecSpec

import javax.xml.transform.URIResolver

public abstract class BaseXsltTask extends DefaultTask {
    protected final File assetsDir;
    protected final File workingDir

    Set<File> catalogFiles

    BaseXsltTask() {
        super()
        assetsDir = DocbookBuildPlugin.getAssetsDir(project)
        workingDir = DocbookBuildPlugin.getWorkingDir(project)
        catalogFiles = new HashSet<>()
    }

    public BaseXsltTask copy(Action<? super CopySpec> action) {
        this.doLast {
            project.copy(action)
        }
        return this
    }

    public BaseXsltTask copy(Closure closure) {
        this.doLast {
            project.copy(closure)
        }
        return this
    }

    protected void doTransform(String main, FileCollection classpath, Map<String, String> sysProps, List<String> args) {
        project.javaexec {
            it.main = main
            it.classpath = classpath
            it.systemProperties = sysProps
            it.args = args
        }
    }

    protected URIResolver createURIResolver() {
        CatalogManager manager = new CatalogManager()
        manager.setUseStaticCatalog(false)
        manager.setRelativeCatalogs(true)
        manager.setCatalogFiles(catalogFiles.collect({ it.absolutePath }).join(";"))
        URIResolver resolver = new CatalogResolver(manager)
        return resolver
    }

    protected abstract String getMain()

    protected abstract FileCollection getClasspath()

    protected abstract Map<String, String> getSysprops()

    protected abstract List<String> getArgs(File srcFile, Optional<File> outFile, File stylesheet, Map<String, Object> params)
}
