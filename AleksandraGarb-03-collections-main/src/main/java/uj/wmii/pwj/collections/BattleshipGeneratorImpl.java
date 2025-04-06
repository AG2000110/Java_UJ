package uj.wmii.pwj.collections;

import java.util.Arrays;
import java.util.Random;

public class BattleshipGeneratorImpl implements BattleshipGenerator {
    private static final int SIZE = 10;
    private static final int BOARD_SIZE = SIZE * SIZE;
    private static final char WATER = '.';
    private static final char SHIP = '#';
    private final char[] board = new char[BOARD_SIZE];
    private final Random random = new Random();

    public BattleshipGeneratorImpl() {
        Arrays.fill(board, WATER);
    }

    @Override
    public String generateMap() {
        placeShips(1, 4); //1 czteromasztowiec
        placeShips(2, 3); //2 trzymasztowce
        placeShips(3, 2); //3 dwumasztowce
        placeShips(4, 1); //4 jednomasztowce

        return new String(board);
    }

    private void placeShips(int count, int length) {
        while (count > 0) {
            int index = random.nextInt(BOARD_SIZE); //losowe miejsce na mapie
            boolean horizontal = random.nextBoolean();  //pionowo czy poziomo

            if (canPlaceShip(index, length, horizontal)) {
                placeShip(index, length, horizontal);
                count--;
            }
        }
    }

    private boolean canPlaceShip(int index, int length, boolean horizontal) {

        int row = index / SIZE;     //wiersz jakby w tab 2D
        int col = index % SIZE;     //kolumna jakby w 2D

        //czy miesci sie na planszy?
        if (horizontal) {
            if (col + length > SIZE) return false;
        } else {
            if (row + length > SIZE) return false;
        }

        //czy nie jest za blisko innych statkow?
        for (int i = -1; i <= length; i++) {
            for (int j = -1; j <= 1; j++) {
                int r = row + (horizontal ? j : i);
                int c = col + (horizontal ? i : j);
                int checkIndex = r * SIZE + c;

                if (isValid(r, c) && board[checkIndex] == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    private void placeShip(int index, int length, boolean horizontal) {
        for (int i = 0; i < length; i++) {
            int placeIndex = horizontal ? index + i : index + i * SIZE;
            board[placeIndex] = SHIP;
        }
    }

    //zeby nie sprawdzac poza mapa
    private boolean isValid(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
