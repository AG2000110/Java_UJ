package uj.wmii.pwj.zd9;


enum GameState{
    TRWA,
    PRZEGRANA,
    WYGRANA
}

public class Map {
    int numberRows;
    int numberCols;
    char[][] NewStringMap;

    //moja mapa
    public Map(int numberRows1, int numberCols1, String PreviousStringMap) {
        this.numberRows = numberRows1;
        this.numberCols = numberCols1;
        NewStringMap = new char[numberRows1][numberCols1];
        for (int i = 0; i < numberRows1; i++) {
            for (int j = 0; j < numberCols1; j++) {
                NewStringMap[i][j] = PreviousStringMap.charAt(i * numberCols + j);
            }
        }
    }

    //mapa przeciwnika
    public Map(int numberRows1, int numberCols1) {
        this.numberRows = numberRows1;
        this.numberCols = numberCols1;
        NewStringMap = new char[numberRows1][numberCols1];
        for (int i = 0; i < numberRows1; i++) {
            for (int j = 0; j < numberCols1; j++) {
                NewStringMap[i][j] = '?';
            }
        }
    }
    //wyswietlenie mapy
    public void show(){
        System.out.print(" ");
        for (int i = 0; i < numberCols; i++) {
            System.out.print(" " + i);
        }
        System.out.print("\n");
        for (int i = 0; i < numberRows; i++) {
            System.out.print((char) ('A' + i) + " ");
            for (int j = 0; j < numberCols; j++) {
                System.out.print(NewStringMap[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    //zmiana symbolu na mapie
    public void change(int row, int col, char symbol){
        NewStringMap[row][col] = symbol;
    }

    //sprawdzam czy symbol jest na mapie, zeby wiedziec czy gra przegrana czy wygrana juz
    public boolean isSymbolOnMap(char symbol){
        for (int i = 0; i < numberRows; i++) {
            for (int j = 0; j < numberCols; j++) {
                if(NewStringMap[i][j] == symbol){
                    return true;
                }
            }
        }
        return false;
    }

    //do sprawdzenia czy wspolrzedne mieszcza sie w zakresie mapy
    public boolean isOnMap(int row, int col){
        return row >= 0 && row < numberRows && col >= 0 && col < numberCols;
    }

    //do sprawdzenia czy przy danych wspolrzednych znajduje sie ten konkretny symbol
    public boolean isThatSymbol(int row, int col, char symbol){
        if(isOnMap(row, col)){
            return NewStringMap[row][col] == symbol;
        }
        return false;
    }

    //sprawdzenie czy dookola jest ten symbol - po odkryciu statku
    public boolean isSymbolAround(int row, int col, char symbol){
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(isOnMap(row+i, col+j)){
                    if(!(i == 0 && j == 0) && NewStringMap[row+i][col+j] == symbol)
                        return true;
                }
            }
        }
        return false;
    }

    //wspolrzedne punktu dookola
    public Point CoordinateOfSymbolAround(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isOnMap(row + i, col + j) && !(i == 0 && j == 0)) {
                    if (NewStringMap[row + i][col + j] == '?') {
                        return new Point(row + i, col + j);
                    }
                }
            }
        }
        return null;
    }

    //odkrywa pola wokol statju calkowicie zatopionego
    public void discover(int row, int col, char symbol){
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(isOnMap(row+i, col+j)){
                    if(NewStringMap[row+i][col+j] == symbol)
                        NewStringMap[row+i][col+j] = '.';
                }
            }
        }
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(isOnMap(row+i, col+j)){
                    if(NewStringMap[row+i][col+j] == '@' && isSymbolAround(row+i, col+j, symbol))
                        discover(row+i, col+j, symbol);
                }
            }
        }
    }

}
