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
