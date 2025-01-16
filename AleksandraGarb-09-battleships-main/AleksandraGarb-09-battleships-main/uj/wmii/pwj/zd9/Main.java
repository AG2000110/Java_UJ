package uj.wmii.pwj.zd9;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    static String path = "Map.txt";
    static Scanner sc = new Scanner(System.in);

    public static void CreateFile(String str) throws IOException {
        Files.deleteIfExists(Path.of(path));
        FileOutputStream out = new FileOutputStream(path);
        out.write(str.getBytes());
        out.close();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server or Player?");
        String mode = sc.nextLine();

        //genrowanie mapy
        MapGenerator generator = new MapGenerator();
        String map = generator.generateMap();
        CreateFile(map);

        if(mode.equals("Server")){
            ServerMode server = new ServerMode(path);
            System.out.print("Server started\n");
            server.Play();
        } else if(mode.equals("Player")){
            System.out.print("Port: ... \n");
            int port = Integer.parseInt(sc.nextLine());
            System.out.print("Server to connect: ... \n");
            String serverName = sc.nextLine();
            ClientMode client = new ClientMode(serverName, port, path);
            System.out.print("Connected to server\n");
            client.Play();

        }
        sc.close();
    }






}
