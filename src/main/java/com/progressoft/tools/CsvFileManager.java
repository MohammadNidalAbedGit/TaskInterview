package com.progressoft.tools;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvFileManager implements FileManager{

    public void writeFile(Path destPath, StringBuilder fileContentBuilder) throws IOException {
        Files.write(destPath, fileContentBuilder.toString().getBytes());
    }

    public List<String> readLines(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        return lines;
    }
}
