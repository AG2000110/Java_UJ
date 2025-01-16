package uj.wmii.pwj.zd9;

import java.io.IOException;
import java.net.Socket;

public class ClientMode {
    Game game;

    public ClientMode(String ip, int port, String path) {
        this.game = new Game(path);
        try{
            game.socket = new Socket(ip, port);
            game.updatePlayerDataStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Play(){
        while (game.getGameState() == GameState.TRWA){
            game.prepareAndSendMove();
            game.receiveMove();
        }
        if (game.getGameState() == GameState.PRZEGRANA) System.out.println("PRZEGRANA");
        else System.out.println("WYGRANA");
        game.printMaps();
        game.closeConnection();
    }
}
