package com.epam.wklab;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.AntlibDefinition;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sten on 26.04.17.
 */
public class RenameFiles extends AntlibDefinition {

    private static final String DEF_DESTDIR = ".";
    private static final String DEF_TIMESTAMP_PATTERN = "yyyyMMddHHmmss";

    private String destDir;
    private String jobId;
    private FileSet fileSet;
    private String timeStampPattern = DEF_TIMESTAMP_PATTERN;

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Changing the pattern of timestamp.
     * @param timeStampPattern new pattern to use when generating timestamp as a job id.
     */
    public void setDefTimestampPattern(String timeStampPattern) {
        this.timeStampPattern = timeStampPattern;
    }

    public void add(FileSet fileSet) {
        this.fileSet = fileSet;
    }

    @Override
    public void execute() throws BuildException {
        if(null == fileSet) {
            throw new BuildException("Error here: " + getLocation() + ": missing <fileSet>");
        }
        if(null == destDir) {
            destDir = DEF_DESTDIR;
        }
        if(null == jobId) {
            try {
                jobId = (new SimpleDateFormat(timeStampPattern)).format(new Date());
            } catch(IllegalArgumentException e) {
                // bad date format
                throw new BuildException("Error here: " + getLocation() +
                        ": bad date format for timestamp generation: " + timeStampPattern);
            }
        }

        renameFiles();
    }

    private void renameFiles() {
        // sample.xyz --> sample-${jobId}.xyz
        final String fileNameReplacement = "-" + jobId + ".";
        String[] includedFiles = fileSet.getDirectoryScanner(getProject()).getIncludedFiles();
        for(final String fileName : includedFiles) {
            String newFileName = replaceLast(fileName, ".", fileNameReplacement);
            File from = new File (fileName), to = new File(newFileName);
            try {
                from.renameTo(to);
            } catch(SecurityException e) {
                throw new BuildException("Error here: " + getLocation() +
                        ": insufficient right access to rename " + fileName +
                        " to " + newFileName + ". Details: " + e);
            }
        }
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
