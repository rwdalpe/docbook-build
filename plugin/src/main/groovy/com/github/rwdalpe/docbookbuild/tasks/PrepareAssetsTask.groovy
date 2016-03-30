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

import net.lingala.zip4j.core.ZipFile
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class PrepareAssetsTask extends DefaultTask {
    private File assetsResourceDirFile

    @TaskAction
    public void prepareAssets() {
        createAssetsDirectory()
        unpackAssets("docbook-build-assets.zip")
    }

    void unpackAssets(String assetsZipFileName) {

        InputStream is = getClass().getClassLoader().getResourceAsStream(assetsZipFileName)

        File outZip = new File("${assetsResourceDirFile}/${assetsZipFileName}".toString())

        FileUtils.copyInputStreamToFile(is, outZip)

        ZipFile zip = new ZipFile(outZip)
        zip.extractAll(assetsResourceDirFile.absolutePath)
    }

    private void createAssetsDirectory() {
        assetsResourceDirFile = project.docbookbuild.assetsExtractionDir

        if (assetsResourceDirFile.exists()) {
            assetsResourceDirFile.delete()
        }

        assetsResourceDirFile.mkdirs()
    }
}
