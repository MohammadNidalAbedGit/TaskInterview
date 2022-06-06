package com.progressoft.tools;

import com.progressoft.tools.constant.NormalizationMethod;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NormalizerImpl implements Normalizer {

    List<BigDecimal> numbers = new ArrayList<>();
    FileManager fileManager;

    NormalizerImpl(FileManager fileManager){
        this.fileManager=fileManager;
    }


    @Override
    public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize) {
        return calculate(csvPath, destPath, colToStandardize, NormalizationMethod.ZSCORE);
    }

    @Override
    public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize) {
        return calculate(csvPath, destPath, colToNormalize, NormalizationMethod.MIN_MAX);
    }

    private ScoringSummaryImpl calculate(Path csvPath, Path destPath, String colToNormalize, NormalizationMethod normalizationMethod) {
        // Validate input CSV file path
        if (Files.notExists(csvPath)) {
            throw new IllegalArgumentException("source file not found");
        }

        try {
            List<String> lines = fileManager.readLines(csvPath);
            // Check if the column exists and return the column index, otherwise throw exception
            int index = checkIfColumnExists(colToNormalize, lines);

            // Extract the number from the line and add it to the list
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                addNumberToList(line, index);
            }

            // Instantiate ScoringSummary object with the passing the numbers list
            ScoringSummaryImpl scoringSummary = new ScoringSummaryImpl(numbers);
            StringBuilder fileContentBuilder = new StringBuilder();

            // Adding the output file columns header and it's data
            if (normalizationMethod.equals(NormalizationMethod.ZSCORE)) {
                writeDestColumns(lines, index, fileContentBuilder, colToNormalize + "_z");
                writeDestZScoreData(scoringSummary, lines.subList(1, lines.size()), index, fileContentBuilder);

            } else if (normalizationMethod.equals(NormalizationMethod.MIN_MAX)) {
                writeDestColumns(lines, index, fileContentBuilder, colToNormalize + "_mm");
                writeDestMinMaxData(scoringSummary, lines.subList(1, lines.size()), index, fileContentBuilder);
            }

            // Flushing data to the output file
            fileManager.writeFile(destPath, fileContentBuilder);
            return scoringSummary;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void writeDestColumns(List<String> lines, int destIndex, StringBuilder fileContent, String colToAppend) {
        // Get the first line and split to get the columns
        String[] columns = lines.get(0).split(",");

        // Iterate over the columns
        for (int colIndex = 0; colIndex < columns.length; colIndex++) {
            if (colIndex - 1 == destIndex) {
                fileContent.append(colToAppend).append(",");
            }
            fileContent.append(columns[colIndex]).append(",");
        }
        if (destIndex >= columns.length - 1) {
            fileContent.append(colToAppend);
        }

        // Make sure to remove the last comma from the result
        int commaIndex = fileContent.lastIndexOf(",");
        if (commaIndex == fileContent.toString().length() - 1) {
            fileContent.replace(commaIndex, commaIndex + 1, "");
        }

        // Add new line to the result
        fileContent.append(System.getProperty("line.separator"));
    }


    public void writeDestZScoreData(ScoringSummary scoringSummary, List<String> lines, int index, StringBuilder fileContent) {

        BigDecimal mean = scoringSummary.mean();
        BigDecimal sd = scoringSummary.standardDeviation();

        for (String line : lines) {
            // Split each line to get line data
            String[] data = line.split(",");
            BigDecimal zScore = (new BigDecimal(data[index]).subtract(mean)).divide(sd, 2, RoundingMode.HALF_EVEN);

            addNewLine(index, fileContent, data, zScore);
        }
    }


    public void addNewLine(int destIndex, StringBuilder fileContent, String[] data, BigDecimal result) {
        // Iterate over the line data
        for (int colIndex = 0; colIndex < data.length; colIndex++) {
            if (colIndex - 1 == destIndex) {
                fileContent.append(result).append(",");
            }
            fileContent.append(data[colIndex]).append(",");
        }
        if (destIndex >= data.length - 1) {
            fileContent.append(result);
        }

        int commaIndex = fileContent.lastIndexOf(",");
        if (commaIndex == fileContent.toString().length() - 1) {
            fileContent.replace(commaIndex, commaIndex + 1, "");
        }

        fileContent.append(System.getProperty("line.separator"));
    }





    private void writeDestMinMaxData(ScoringSummary scoringSummary,List<String> lines, int index, StringBuilder fileContent) {
        BigDecimal max = scoringSummary.max();
        BigDecimal min = scoringSummary.min();
        BigDecimal newMax = BigDecimal.valueOf(1);
        BigDecimal newMin = BigDecimal.valueOf(0);

        for (String line : lines) {
            // Split each line to get line data
            String[] data = line.split(",");
            BigDecimal minMax = ((new BigDecimal(data[index]).subtract(min)).divide(max.subtract(min), 2, RoundingMode.HALF_EVEN)).multiply(newMax.subtract(newMin)).add(newMin);

            addNewLine(index, fileContent, data, minMax);
        }
    }

    private int checkIfColumnExists(String colToNormalize, List<String> lines) {
        String[] columns = lines.get(0).split(",");

        boolean isColumnFound = false;

        int colIndex = -1;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(colToNormalize)) {
                isColumnFound = true;
                colIndex = i;
                break;
            }
        }

        if (!isColumnFound) {
            throw new IllegalArgumentException("column " + colToNormalize + " not found");
        } else {
            return colIndex;
        }
    }

    private void addNumberToList(String line, int index) {
        String[] info = line.split(",");
        numbers.add(new BigDecimal(info[index]));
    }


}
