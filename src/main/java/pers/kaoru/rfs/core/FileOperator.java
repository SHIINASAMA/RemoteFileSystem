package pers.kaoru.rfs.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOperator implements ImplFileOperator {

    @Override
    public File[] listShow(File source) {
        if (source.exists() && source.isDirectory()) {
            return source.listFiles();
        }
        return null;
    }

    @Override
    public boolean remove(File source) {
        if (source.exists()) {
            if (source.isFile()) {
                return source.delete();
            } else {
                return removeDirectory(source);
            }
        }
        return false;
    }

    private boolean removeDirectory(File source) {
        File[] files = source.listFiles();
        assert files != null;
        for (var file : files) {
            if (file.isFile()) {
                if (!file.delete()) {
                    return false;
                }
            } else {
                if (!removeDirectory(file)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean copy(File source, File destination) {
        if (source.exists() && !destination.exists()) {
            var srcPath = Path.of(source.getPath());
            var desPath = Path.of(destination.getPath());
            try {
                Files.copy(srcPath, desPath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean move(File source, File destination) {
        if (source.exists() && !destination.exists()) {
            var srcPath = Path.of(source.getPath());
            var desPath = Path.of(destination.getPath());
            try {
                Files.move(srcPath, desPath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean makeDirectory(File source) {
        if(source.exists()){
            return false;
        }
        return source.mkdirs();
    }
}
