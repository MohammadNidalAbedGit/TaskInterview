package com.progressoft.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class JsonFileManager implements FileManager{
    @Override
    public void writeFile(Path destPath, StringBuilder fileContentBuilder) throws IOException {
    }

    @Override
    public List<String> readLines(Path csvPath) throws IOException {
        return null;
    }
}
