package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import model.Student;

public class StudentExporter {

    /**
     * Legacy method — writes to current working directory.
     * Kept for backward compatibility.
     */
    public void export(List<Student> students) throws Exception {
        exportToFile(students, new File("students.csv"));
    }

    /**
     * Export names list to a specific file (used by old PostEventScreen).
     */
    public void exportNames(List<String> names) throws Exception {
        File file = new File("event_matches.csv");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("Name");
            w.newLine();
            for (String n : names) {
                w.write(escapeCsv(n));
                w.newLine();
            }
        }
    }

    /**
     * New method — exports to a user-chosen File (FileChooser).
     * Writes: Name, Email, Batch, Branch, Bio
     */
    public void exportToFile(List<Student> students, File file) throws Exception {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            // Header row
            w.write("Name,Email,Batch,Branch,Bio");
            w.newLine();

            for (Student s : students) {
                String line = String.join(",",
                    escapeCsv(s.getName()),
                    escapeCsv(s.getEmail()),
                    String.valueOf(s.getBatch()),
                    escapeCsv(s.getBranch() != null ? s.getBranch() : ""),
                    escapeCsv(s.getBio()    != null ? s.getBio()    : "")
                );
                w.write(line);
                w.newLine();
            }
        }
    }

    /** Wraps a CSV field in quotes if it contains commas, quotes, or newlines. */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
