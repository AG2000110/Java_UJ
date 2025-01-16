package uj.wmii.pwj.zd9;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

enum Commands{
    start,
    pudło,
    trafiony,
    trafiony_zatopiony,
    ostatni_zatopiony,
    błąd_komunikacji
}

public class Game {
    private GameState gameState;
    private Commands answer;
    protected Socket socket;
    protected DataInputStream in;
    protected DataOutputStream out;
    Point lastPositiveShot;
    Point lastSendCord;
    boolean shootingMode;
    Map myMap;
    Map enemyMap;
    String move;

    private final Scanner scanner = new Scanner(System.in);

    public void printMaps(){
        myMap.show();
        System.out.print('\n');
        enemyMap.show();
    }

    public Game(String path){
        gameState = GameState.TRWA;
        answer = Commands.start;
        try{
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String mapInOneLine = reader.readLine();
            myMap = new Map(10,10,mapInOneLine);
            enemyMap = new Map(10,10);
            reader.readLine();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updatePlayerDataStream(){
        try{
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.in = new DataInputStream(this.socket.getInputStream());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public GameState getGameState(){
        return gameState;
    }

    public void sendMove(){
        try{
            out.writeUTF(move);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //zamieniam komende na string
    public String resultToString(Commands result){
        String str = result.toString();
        str = str.replace('_', ' ');
        return str;
    }

    //zamieniam string na komende
    public Commands stringToResult(String str){
        str = str.replace("\n", "");
        return switch (str){
            case "start" -> Commands.start;
            case "trafiony" -> Commands.trafiony;
            case "trafiony zatopiony" -> Commands.trafiony_zatopiony;
            case "ostatni zatopiony" -> Commands.ostatni_zatopiony;
            case "pudło" -> Commands.pudło;
            default -> Commands.błąd_komunikacji;
        };
    }

    //sprawdzam czy wiadomosc jest poprawna
    public void isGoodMessage(String message) throws IOException{
        if(message.contains(";")){
            String[] messageParts = message.split(";");
            if(stringToResult(messageParts[0]) == Commands.błąd_komunikacji)
                throw new IOException();
            Point point = new Point(messageParts[1]);
            if(point.getX() >= enemyMap.numberRows || point.getY() >= enemyMap.numberCols || point.getX() < 0 || point.getY() < 0)
                throw new IOException();
        }
    }

    //aktualizacja map
    public void updateMyMap(Commands command, Point point, Map map){
        if(command == Commands.pudło)
            map.change(point.getX(), point.getY(), '~');
        else if (command == Commands.trafiony || command == Commands.trafiony_zatopiony || command == Commands.ostatni_zatopiony)
            map.change(point.getX(), point.getY(), '@');
    }

    public void updateEnemyMap(Commands command, Point point, Map map){
        if(command == Commands.pudło)
            map.change(point.getX(), point.getY(), '.');
        else if (command == Commands.trafiony || command == Commands.trafiony_zatopiony || command == Commands.ostatni_zatopiony)
            map.change(point.getX(), point.getY(), '#');
    }

    //wynik strzalu przeciwnika
    Commands resultShot(String cord){
        Point point = new Point(cord);
        if(!myMap.isSymbolAround(point.getX(), point.getY(), '#') && myMap.isThatSymbol(point.getX(), point.getY(), '#')){
            myMap.change(point.getX(), point.getY(), '@');
            if(!myMap.isSymbolOnMap('#'))
                return Commands.ostatni_zatopiony;
            return Commands.trafiony_zatopiony;
        } else if (myMap.isThatSymbol(point.getX(), point.getY(), '#') || myMap.isThatSymbol(point.getX(), point.getY(), '@'))
           return Commands.trafiony;
        return Commands.pudło;
    }

    //odieranie ruchu przeciwnika
    public void receiveMove() {
        System.out.print("Czekam na ruch...\n");
        String message = "";
        for (int i = 0; i < 3; i++) {
            try {
                message = in.readUTF();
                isGoodMessage(message);
                break;
            } catch (IOException e) {
                try {
                    socket.setSoTimeout(1000);
                    sendMove();
                } catch (SocketException ignored) {
                }
            }
        }
        System.out.println("Otrzymana wiadomosc: " + message);
        if(message.contains(";")){
            String[] messageParts = message.split(";");
            Commands command = stringToResult(messageParts[0]);
            updateEnemyMap(command, lastSendCord, enemyMap);
            if(command == Commands.trafiony){
                shootingMode = true;
                lastPositiveShot = lastSendCord;
            }
            if(command == Commands.trafiony_zatopiony){
                enemyMap.discover(lastSendCord.getX(), lastSendCord.getY(), '?');
                shootingMode = false;
                lastPositiveShot = lastSendCord;
            }
            answer = resultShot(messageParts[1]);
            Point point = new Point(messageParts[1]);
            updateMyMap(answer, point, myMap);
        }
        if(stringToResult(message) == Commands.ostatni_zatopiony){
            updateEnemyMap(Commands.ostatni_zatopiony, lastSendCord, enemyMap);
            gameState = GameState.WYGRANA;
        }
    }

    //wybranie miejsca do mojego nastepnego strzalu
    public String choosePlaceToShoot() {
        if (shootingMode && enemyMap.isSymbolAround(lastPositiveShot.getX(), lastPositiveShot.getY(), '?')) {
            return enemyMap.CoordinateOfSymbolAround(lastPositiveShot.getX(), lastPositiveShot.getY()).toString();
        }

        for (int i = 0; i < enemyMap.numberRows; i++) {
            for (int j = 0; j < enemyMap.numberCols; j++) {
                if (enemyMap.isThatSymbol(i, j, '?')) {
                    return new Point(i, j).toString();
                }
            }
        }
        return null;
    }

    //wysylanie mojej wiadomosci
    public void prepareAndSendMove(){
        printMaps();
        if(!myMap.isSymbolOnMap('#'))
            gameState = GameState.PRZEGRANA;

        System.out.print("Stan gry: " + gameState.toString() + "\n");

        if(gameState == GameState.PRZEGRANA){
            move = resultToString(Commands.ostatni_zatopiony) + "\n";
            System.out.print("Stan gry: PRZEGRANA. Wysyłam wiadomość: "  + move);
            sendMove();
        }

        if(gameState == GameState.TRWA){
            System.out.print("\nJaki chcesz wykonać ruch? ");
            move = choosePlaceToShoot();
            lastSendCord = new Point(move);
            System.out.print(move + "\n");
            move = resultToString(answer) + ";" + move + "\n";
            System.out.print("Wysyłam: " + move);
            sendMove();
        }
    }

    public void closeConnection(){
        try{
            this.socket.close();
            this.in.close();
            this.out.close();
            this.scanner.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }










}

