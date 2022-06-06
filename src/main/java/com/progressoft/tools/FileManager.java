package com.progressoft.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileManager {

     void writeFile(Path destPath, StringBuilder fileContentBuilder) throws IOException;

     List<String> readLines(Path csvPath) throws IOException;
}
