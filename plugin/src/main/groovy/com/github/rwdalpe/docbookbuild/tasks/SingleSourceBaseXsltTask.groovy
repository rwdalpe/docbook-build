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
