package uj.wmii.pwj.gvt;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


enum COMMANDS{

    init{
        public void action(ExitHandler exitHandler, Gvt gvt, String ... arg){
            if (gvt.directory.exists()) {
                exitHandler.exit(10, "Current directory is already initialized.");
            } else {
                gvt.init();
            }
        }
    },

    add{
        public void action(ExitHandler exitHandler, Gvt gvt, String... arg){
            if(arg.length < 2){
                exitHandler.exit(20, "Please specify file to add.");
            } else if (new File(arg[1]).exists()){
                gvt.add(arg);
            } else {
                exitHandler.exit(21, "File not found. File: " + arg[1]);
            }
        }
    },

    detach{
        public void action(ExitHandler exitHandler, Gvt gvt, String ... arg){
            if(arg.length < 2){
                exitHandler.exit(30, "Please specify file to detach.");
            } else {
                gvt.detach(arg);
            }
        }
    },

    checkout{
        public void action(ExitHandler exitHandler, Gvt gvt, String ... arg){
            if(arg.length < 2){
                exitHandler.exit(60, "Invalid version number: ");
            } else if(!new File(".gvt/v" + arg[1]).exists()){
                exitHandler.exit(60, "Invalid version number: " + arg[1]);
            } else {
                gvt.checkout(arg);
            }
        }
    },

    commit{
        public void action(ExitHandler exitHandler, Gvt gvt, String ... arg){
            if(arg.length < 2){
                exitHandler.exit(50, "Please specify file to commit.");
            } else if (!new File(arg[1]).exists()){
                exitHandler.exit(51, "File not found. File: " + arg[1]);
            } else {
                gvt.commit(arg);
            }
        }
    },

    history{
        public void action(ExitHandler exitHandler, Gvt gvt, String ... arg){
            gvt.history(arg);
        }
    },

    version{
        public void action(ExitHandler exitHandler, Gvt gvt, String ... arg){
            gvt.version(arg);
        }
    };

    public abstract void action(ExitHandler exitHandler, Gvt gvt, String... args);
}

public class Gvt {

    private final ExitHandler exitHandler;

    static File directory = new File(".gvt");

    public Gvt(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    boolean withoutArg(String ... arg) {
        if(arg.length == 0){
            this.exitHandler.exit(1, "Please specify command.");
            return false;
        } else {
            return true;
        }
    }

    boolean unknownCommand(String c) {
        try{
            COMMANDS.valueOf(c);
        } catch(IllegalArgumentException e){
            this.exitHandler.exit(1, "Unknown command " + c);
            return false;
        }
        return true;
    }

    public int lastVersion(){
        int version = 0;
        while (new File(".gvt/v" + version).exists()){
            version++;
        }
        return version - 1;
    }

    public static void copyDirectory(String oldDir, String newDir) throws IOException {
        Files.walk(Paths.get(oldDir)).forEach(source -> {
            Path destination = Paths.get(newDir, source.toString()
                    .substring(oldDir.length()));
            try{
                if(!oldDir.equals(source.toString())) {
                    Files.copy(source, destination, REPLACE_EXISTING);
                }} catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    void copyFile(String oldPath, String newPath) throws IOException {
        FileInputStream oldStream = new FileInputStream(oldPath);
        FileOutputStream newStream = new FileOutputStream(newPath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = oldStream.read(buffer)) > 0) {
            newStream.write(buffer, 0, length);
        }
        oldStream.close();
        newStream.close();
    }

    void init(){
        try{
            directory.mkdir();
            new File(".gvt/v0").mkdir();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v0/comment"));
            bufferedWriter.write("GVT initialized.");
            bufferedWriter.close();
            exitHandler.exit(0, "Current directory initialized successfully.");
        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        }
    }

    void add(String... args){
        try {
            int v = lastVersion();

            if (new File(".gvt/v" + v + "/" + args[1]).exists()) {
                exitHandler.exit(0, "File already added. File: " + args[1]);
                return;
            }

            new File(".gvt/v" + (v + 1)).mkdir();
            if (!new File(".gvt/v" + (v + 1)).exists()) {
                exitHandler.exit(2, "Failed to create directory: .gvt/v" + (v + 1));
                return;
            }

            copyDirectory(".gvt/v" + v, ".gvt/v" + (v + 1));
            copyFile(args[1], ".gvt/v" + (v + 1) + "/" + args[1]);

            String comment = null;
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("-m"))
                    comment = args[i + 1];
            }
            if (comment == null)
                comment = "File added successfully. File: " + args[1];

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v" + (v + 1) + "/comment"));
            bufferedWriter.write(comment);
            bufferedWriter.close();

            exitHandler.exit(0, "File added successfully. File: " + args[1]);

        } catch (IOException e) {
            exitHandler.exit(3, "IOException occurred: " + e.getMessage());
        } catch (SecurityException e) {
            exitHandler.exit(4, "Security exception: " + e.getMessage());
        } catch (Exception e) {
            exitHandler.exit(5, "Unexpected error: " + e.getMessage());
        }
    }

    void detach(String... args){
        try {
            int last = lastVersion();

            if (!new File(".gvt/v" + last + "/" + args[1]).exists()) {
                exitHandler.exit(0, "File is not added to gvt. File: " + args[1]);
                return;
            }
            boolean dirCreated = new File(".gvt/v" + (last + 1)).mkdir();
            if (!dirCreated) {
                exitHandler.exit(2, "Failed to create directory: .gvt/v" + (last + 1));
                return;
            }
            try {
                copyDirectory(".gvt/v" + last, ".gvt/v" + (last + 1));
            } catch (IOException e) {
                exitHandler.exit(3, "Failed to copy directory. Error: " + e.getMessage());
                return;
            }
            File fileToDelete = new File(".gvt/v" + (last + 1) + "/" + args[1]);
            boolean fileDeleted = fileToDelete.delete();
            if (!fileDeleted) {
                exitHandler.exit(4, "Failed to delete file: " + args[1]);
                return;
            }
            String comment = null;
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("-m")) {
                    comment = args[i + 1];
                }
            }
            if (comment == null) {
                comment = "File detached successfully. File: " + args[1];
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v" + (last + 1) + "/comment"))) {
                bufferedWriter.write(comment);
            } catch (IOException e) {
                exitHandler.exit(5, "Failed to write comment to file. Error: " + e.getMessage());
                return;
            }
            exitHandler.exit(0, "File detached successfully. File: " + args[1]);

        } catch (Exception e) {
            exitHandler.exit(32, "Unexpected error occurred. File: " + args[1]);
        }
    }

    void commit(String... args){
        try {
            int last = lastVersion();

            if (!new File(".gvt/v" + last + "/" + args[1]).exists()) {
                exitHandler.exit(0, "File is not added to gvt. File: " + args[1]);
                return;
            }

            boolean dirCreated = new File(".gvt/v" + (last + 1)).mkdir();
            if (!dirCreated) {
                exitHandler.exit(2, "Failed to create directory: .gvt/v" + (last + 1));
                return;
            }

            try {
                copyFile(args[1], ".gvt/v" + (last + 1) + "/" + args[1]);
            } catch (IOException e) {
                exitHandler.exit(3, "Failed to copy file. Error: " + e.getMessage());
                return;
            }

            String comment = null;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-m")) {
                    comment = args[i + 1];
                }
            }
            if (comment == null) {
                comment = "File committed successfully. File: " + args[1];
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".gvt/v" + (last + 1) + "/comment"))) {
                bufferedWriter.write(comment);
            } catch (IOException e) {
                exitHandler.exit(4, "Failed to write comment to file. Error: " + e.getMessage());
                return;
            }
            exitHandler.exit(0, "File committed successfully. File: " + args[1]);

        } catch (Exception e) {
            exitHandler.exit(52, "File cannot be committed. See ERR for details. File: " + args[1]);
        }

    }

    void checkout(String...args) {
        try {
            copyDirectory(".gvt/v" + args[1], ".");
            exitHandler.exit(0, "Checkout successful for version: " + args[1]);
        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        }
    }

    void history(String... args) {
        String howMany = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-last")) {
                howMany = args[i + 1];
                break;
            }
        }
        int n;
        try {
            n = Integer.parseInt(howMany) - 1;
        } catch (NumberFormatException e) {
            n = Integer.MAX_VALUE;
        }
        try{
            int last = lastVersion();
            StringBuilder res = new StringBuilder();
            for(; last  >= 0 && n >= 0; n--, last--){
                try(Scanner sc = new Scanner(new File(".gvt/v" + last + "/comment"))){
                    res.append(last).append(": ").append(sc.nextLine()).append("\n");
                }
            }
            exitHandler.exit(0, res.toString());
        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        }
    }

    void version(String ... args){
        int version = 0;
        try{
            version = args.length < 2 ? lastVersion() : Integer.parseInt(args[1]);
            if(!new File(".gvt/v" + version).exists()){
                exitHandler.exit(60, "Invalid version number: " + version);
            } else {
                exitHandler.exit(0, "Version: " + version + "\n" + Files.readString(Path.of(".gvt/v" + version + "/comment")));
            }
        } catch (IOException e) {
            exitHandler.exit(60, "Invalid version number: " + version);
        }
    }

    public static void main(String... args) {
        Gvt gvt = new Gvt(new ExitHandler());
        gvt.mainInternal(args);
    }

    public void mainInternal(String... args) {
        if(!withoutArg(args)){
            return;
        }
        String c = args[0];
        if(!new File(".gvt").exists()){
           exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
        }
        if(unknownCommand(c)){
            COMMANDS command = COMMANDS.valueOf(c);
            command.action(this.exitHandler, this, args);
        }
    }
}
