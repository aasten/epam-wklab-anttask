package com.epam.wklab;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.AntlibDefinition;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

import static org.apache.tools.ant.Project.MSG_ERR;

/**
 * Created by sten on 26.04.17.
 */
public class RenameFiles extends AntlibDefinition {

    private static final String DEF_DESTDIR = ".";

    private String destDir;
    private String jobId;
    private FileSet fileSet;

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void add(FileSet fileSet) {
        this.fileSet = fileSet;
    }

    @Override
    public void execute() throws BuildException {
        if(null == fileSet) {
            throw new BuildException("Error here: " + getLocation() + ": missing <fileSet>");
        }
        if(null == jobId) {
            throw new BuildException("Error here: " + getLocation() +
                    ": missing required \"jobId\" attribute.");
        }
        if(null == destDir) {
            destDir = DEF_DESTDIR;
        }

        renameFiles();
    }

    private void renameFiles() {
        // sample.xyz --> sample-${jobId}.xyz
        final String fileNameReplacement = "-" + jobId + ".";
        final File baseDir = fileSet.getDir();
        log("Started in directory " + baseDir.getPath());
        String[] includedFiles = fileSet.getDirectoryScanner(getProject()).getIncludedFiles();
        for(final String fileName : includedFiles) {
            String newFileName = replaceLast(fileName, ".", fileNameReplacement);
            File from = new File (baseDir, fileName), to = new File(baseDir, newFileName);
            if(!from.exists()) {
                log("File " + from.getPath() + " does not exist! ", MSG_ERR);
                continue;
            }
            try {
                log("Renaming " + from.getName() + " to " + to.getName());
                from.renameTo(to);
            } catch(SecurityException e) {
                throw new BuildException("Error here: " + getLocation() +
                        ": insufficient right access to rename " + from.getPath() +
                        " to " + to.getPath() + ". Details: " + e);
            }
        }
        log("Finished in directory " + baseDir.getPath());
    }

    // taken here: https://stackoverflow.com/a/2282982/1023544
    private static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }
}
