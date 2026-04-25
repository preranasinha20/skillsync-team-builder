package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import model.Student;

public class StudentExporter {

    public void export(List<Student> students) throws Exception {
        BufferedWriter w = new BufferedWriter(new FileWriter("students.csv"));
        for (Student s : students) {
            w.write(s.getName());
            w.newLine();
        }
        w.close();
    }

    public void exportNames(List<String> names) throws Exception {
        BufferedWriter w = new BufferedWriter(new FileWriter("event_matches.csv"));
        for (String n : names) {
            w.write(n);
            w.newLine();
        }
        w.close();
    }
}
