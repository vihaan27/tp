package filehandlers;

import students.Student;
import students.StudentList;

import java.util.ArrayList;
import java.util.List;

public class StudentListFileLoader implements FileLoader<StudentList> {
    private final StudentFileLoader studentLoader = new StudentFileLoader();

    @Override
    public StudentList loadFromFile(String path) {
        throw new UnsupportedOperationException("Use loadFromLines(List<String>, tutorialName) instead.");
    }

    public StudentList loadFromLines(List<String> lines) {
        ArrayList<Student> students = new ArrayList<>();
        for (String line : lines) {
            try {
                Student s = studentLoader.loadFromLine(line);
                if (s != null) {
                    students.add(s);
                }
            } catch (Exception e) {
                System.out.println("Warning: Skipping malformed student line: \"" + line + "\"");
            }
        }
        return new StudentList(students);
    }
}
