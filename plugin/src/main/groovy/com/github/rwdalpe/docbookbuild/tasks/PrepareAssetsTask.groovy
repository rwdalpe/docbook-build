package com.github.rwdalpe.docbookbuild.tasks

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import net.lingala.zip4j.core.ZipFile
import sun.misc.IOUtils;

public class PrepareAssetsTask extends DefaultTask {
    private File assetsResourceDirFile

    @TaskAction
    public void prepareAssets() {
        createAssetsDirectory()
        unpackAssets()
    }

    void unpackAssets() {
        String assetsZipFileName = "docbook-build-assets.zip"

        InputStream is = getClass().getClassLoader().getResourceAsStream(assetsZipFileName)

        File outZip = new File("${assetsResourceDirFile}/${assetsZipFileName}".toString())

        FileUtils.copyInputStreamToFile(is, outZip)

        ZipFile zip = new ZipFile(outZip)
        zip.extractAll(assetsResourceDirFile.absolutePath)
    }

    private void createAssetsDirectory() {
        def assetsResourceDir = project.docbookbuild.assetsExtractionDir
        assetsResourceDirFile = project.file(assetsResourceDir)

        if (assetsResourceDirFile.exists()) {
            assetsResourceDirFile.delete()
        }

        assetsResourceDirFile.mkdirs()
    }
}
