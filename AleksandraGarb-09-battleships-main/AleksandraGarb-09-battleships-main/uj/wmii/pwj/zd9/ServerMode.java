package uj.wmii.pwj.zd9;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerMode {
    private ServerSocket serverSocket;
    Game game;

    public ServerMode(String fileName) {
        try{
            game = new Game(fileName);
            InetAddress ip = InetAddress.getLocalHost();
            this.serverSocket = new ServerSocket(0, 1, ip);
            System.out.print("Server started on: " + serverSocket.getLocalSocketAddress() + "\n");
            game.socket = serverSocket.accept();
            game.updatePlayerDataStream();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Play(){
        while(game.getGameState() == GameState.TRWA){
            game.receiveMove();
            game.prepareAndSendMove();
        }
        if(game.getGameState() == GameState.PRZEGRANA)
            System.out.print("PRZEGRANA\n");
        else
            System.out.print("WYGRANA\n");
        game.printMaps();
        try{
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        game.closeConnection();
    }
}
