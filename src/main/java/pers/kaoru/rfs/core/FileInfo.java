package pers.kaoru.rfs.core;

import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {

    private final String name;
    private final Boolean isDirectory;
    private final Long size;
    private final Long last;

    public FileInfo(File file) {
        name = file.getName();
        isDirectory = file.isDirectory();
        size = file.length();
        last = file.lastModified();
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", size=" + size +
                ", last=" + last +
                '}';
    }
}
