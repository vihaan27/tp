import Attendance.AttendanceFile;
import Attendance.AttendanceList;
import Tutorial.TutorialClassList;
import Util.DataManager;
import Util.UI;
import commandHandler.CommandHandler;
import commandHandler.CommandParser;
import students.StudentList;
import task.TaskList;

import java.io.File;

public class TASync {
    public static void main(String[] args) {
        DataManager dataManager = new DataManager();

        // Load tutorials
        TutorialClassList tutorialList = dataManager.loadTutorials();
        if (tutorialList == null || tutorialList.getTutorialClasses().isEmpty()) {
            System.out.println("No tutorials loaded or file is empty.");
            tutorialList = new TutorialClassList();
        }else {
            System.out.println("Tutorial classes loaded from: " + new File(dataManager.getTutorialFilePath()).getPath() + "\n");
        }

        AttendanceFile attendanceFile = dataManager.loadAttendanceFiles(tutorialList);
        if (attendanceFile == null) {
            System.out.println("No attendance file loaded.");
            attendanceFile= new AttendanceFile(null);
        }else{
            System.out.println("Tutorial classes loaded from: " + new File(dataManager.getAttendanceFilePath()).getPath() + "\n");
        }

        /*
        for (AttendanceList attendanceList : attendanceFile.getAttendanceList()) {
            System.out.println(attendanceList);
        } // just to check if attendanceFile imported correctly
        */

        TaskList taskList = new TaskList();
        StudentList studentlist = new StudentList();
        UI ui = new UI();
        ui.printWelcome();

        boolean isRunning = true;
        while (isRunning){
            String input = ui.getUserCommand();
            CommandParser commandParser = new CommandParser(input);
            String[] parts = commandParser.getParts();
            if (parts.length < 2){
                System.out.println("Invalid command format. Please use: /add -[type] [task details]");
                break;
            }
            String listType = parts[1];
            String command = parts[0].substring(1).toUpperCase();
            CommandHandler commandHandler;
            if ("ADD".equals(command) || listType.equalsIgnoreCase("-p")) {
                commandHandler = new CommandHandler(taskList, parts);
            } else if (listType.equalsIgnoreCase("-s")) {
                commandHandler = new CommandHandler(studentlist, parts);
            } else if (listType.equalsIgnoreCase("-t")) {
                commandHandler = new CommandHandler(tutorialList, parts);
            } else {
                commandHandler = new CommandHandler(null, parts);
                System.out.println("Invalid command");
            }

            isRunning = commandHandler.runCommand();
        }

        ui.printGoodbye();
        ui.close();
        dataManager.saveTutorials(tutorialList);
        dataManager.saveAttendanceFile(attendanceFile);

        System.out.println("All data saved successfully!");
    }
}