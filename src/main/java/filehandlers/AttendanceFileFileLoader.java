package filehandlers;

import attendance.AttendanceFile;
import attendance.AttendanceList;
import students.Student;
import tutorial.TutorialClass;
import tutorial.TutorialClassList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceFileFileLoader implements FileLoader<AttendanceFile> {
    private TutorialClassList classList;

    public AttendanceFileFileLoader(TutorialClassList classList) {
        this.classList = classList;
    }

    @Override
    public AttendanceFile loadFromFile(String filePath) {
        ArrayList<AttendanceList> attendanceLists = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            String currentTutorial = null;
            int currentWeek = 0;
            TutorialClass currentClass = null;
            Map<Student, String> attendanceMap = new HashMap<>();
            Map<Student, ArrayList<String>> commentMap = new HashMap<>();
            boolean startComments = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("#")) {
                    // Start of a new AttendanceList
                    String[] header = line.substring(1).trim().split(",", 2);
                    currentTutorial = header[0].trim();
                    currentWeek = Integer.parseInt(header[1].trim());

                    currentClass = classList.getByName(currentTutorial);
                    if (currentClass == null) {
                        System.err.println("Tutorial class not found: " + currentTutorial);
                        continue;
                    }

                    // Reset for new list
                    attendanceMap = new HashMap<>();
                    commentMap = new HashMap<>();
                    startComments = false;
                } else if (line.equalsIgnoreCase("//comments")) {
                    startComments = true;
                } else if (line.equalsIgnoreCase("//commentEnd")) {

                    if (currentClass != null) {
                        AttendanceList list = new AttendanceList(currentClass, currentWeek);
                        list.getAttendanceMap().clear();
                        list.getCommentList().clear();
                        list.getAttendanceMap().putAll(attendanceMap);

                        for (Map.Entry<Student, ArrayList<String>> entry : commentMap.entrySet()) {
                            list.addComments(entry.getKey(), entry.getValue());
                        }

                        attendanceLists.add(list);
                    }

                    currentTutorial = null;
                    currentClass = null;
                    attendanceMap = new HashMap<>();
                    commentMap = new HashMap<>();
                    startComments = false;
                } else if (startComments) {
                    // Comment line
                    String[] parts = line.split(",", 3);
                    if (parts.length < 2) {
                        continue ;
                    }

                    String matric = parts[1].trim();
                    Student student = currentClass.getStudentList().getStudentByMatricNumber(matric);
                    if (student == null) {
                        System.err.println("Comment student not found: " + matric);
                        continue;
                    }

                    ArrayList<String> comments = new ArrayList<>();
                    if (parts.length == 3) {
                        String[] commentArray = parts[2].split(";", -1);
                        for (String comment : commentArray) {
                            comments.add(comment.trim());
                        }
                    }

                    commentMap.put(student, comments);
                } else {
                    // Attendance line
                    String[] parts = line.split(",", 3);
                    if (parts.length < 3) {
                        continue;
                    }

                    String matric = parts[1].trim();
                    String status = parts[2].trim();
                    Student student = currentClass.getStudentList().getStudentByMatricNumber(matric);
                    if (student == null) {
                        System.err.println("Attendance student not found: " + matric);
                        continue;
                    }

                    attendanceMap.put(student, status);
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading AttendanceFile: " + e.getMessage());
        }

        return new AttendanceFile(attendanceLists);
    }
}
