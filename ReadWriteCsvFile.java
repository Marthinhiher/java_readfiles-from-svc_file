
package readwritecsvfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ReadWriteCsvFile {


    public static void main(String[] args) {
        // path to your input CSV file  module information
        String inputCSVFile = "C:\\Users\\MarthinHiher\\Documents\\modules.csv";

        //  path to the output CSV file for module statistics
        String outputCSVFile = "C:\\Users\\MarthinHiher\\Documents\\modulestatistics.csv";

        // Reading student and module information from the input CSV file
        Map<String, Map<String, Integer>> studentModuleMarks = readStudentModuleData(inputCSVFile);

        // Calculating module statistics
        Map<String, ModuleStatistics> moduleStatisticsMap = calculateModuleStatistics(studentModuleMarks);

        // module statistics to the output CSV file
        writeModuleStatisticsToCSV(moduleStatisticsMap, outputCSVFile);
    }

    private static Map<String, Map<String, Integer>> readStudentModuleData(String filename) {
        Map<String, Map<String, Integer>> studentModuleMarks = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String studentId = data[0].trim();
                String moduleCode = data[1].trim();
                int mark = Integer.parseInt(data[2].trim());

                // Adding student to the map if not present
                studentModuleMarks.putIfAbsent(studentId, new HashMap<>());

                // Adding module and mark to the student
                studentModuleMarks.get(studentId).put(moduleCode, mark);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return studentModuleMarks;
    }

    private static Map<String, ModuleStatistics> calculateModuleStatistics(Map<String, Map<String, Integer>> studentModuleMarks) {
        Map<String, ModuleStatistics> moduleStatisticsMap = new HashMap<>();

        // Iterating over students and their module marks
        for (Map.Entry<String, Map<String, Integer>> entry : studentModuleMarks.entrySet()) {
            String studentId = entry.getKey();
            Map<String, Integer> moduleMarks = entry.getValue();

            // Iterate over modules and calculate statistics
            for (Map.Entry<String, Integer> moduleEntry : moduleMarks.entrySet()) {
                String moduleCode = moduleEntry.getKey();
                int mark = moduleEntry.getValue();

                // Adding module to the map if not present
                moduleStatisticsMap.putIfAbsent(moduleCode, new ModuleStatistics(moduleCode));

                // Update statistics for the module
                ModuleStatistics moduleStatistics = moduleStatisticsMap.get(moduleCode);
                moduleStatistics.updateStatistics(mark);
            }
        }

        return moduleStatisticsMap;
    }

    private static void writeModuleStatisticsToCSV(Map<String, ModuleStatistics> moduleStatisticsMap, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write CSV header
            writer.append("ModuleCode,HighestMark,LowestMark,AverageMark,PassCount,FailCount\n");

            // Write module statistics
            for (ModuleStatistics moduleStatistics : moduleStatisticsMap.values()) {
                writer.append(moduleStatistics.toCSVString()).append("\n");
            }

            System.out.println("Module statistics have been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ModuleStatistics {
    private String moduleCode;
    private int highestMark = Integer.MIN_VALUE;
    private int lowestMark = Integer.MAX_VALUE;
    private double totalMark = 0;
    private int passCount = 0;
    private int failCount = 0;
    private int totalCount = 0;

    public ModuleStatistics(String moduleCode) {
        this.moduleCode = moduleCode;
    }
//method for update statistics
    public void updateStatistics(int mark) {
        highestMark = Math.max(highestMark, mark);
        lowestMark = Math.min(lowestMark, mark);
        totalMark += mark;
        totalCount++;

        if (mark >= 40) {
            passCount++;
        } else {
            failCount++;
        }
    }

    public String toCSVString() {
        double averageMark = totalCount > 0 ? totalMark / totalCount : 0;
        return moduleCode + "," + highestMark + "," + lowestMark + "," + averageMark + "," + passCount + "," + failCount;
    }
}

    
    

