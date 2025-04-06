package uj.wmii.pwj.spreadsheet;

public class Spreadsheet {

    public static boolean isInt(String str) {
        return str.matches("-?\\d+");
    }

    public static boolean isReference(String str) {
        return str.matches("\\$[A-Z]+\\d+");
    }

    public static int[] parseReference(String ref) {
        int i = 1;  //pomijam $
        while (i < ref.length() && Character.isLetter(ref.charAt(i))) {
            i++;
        }
        String column = ref.substring(1, i);    //od 1 do końca liter
        String row = ref.substring(i);          //od początku cyfr do końca

        int columnIndex = 0;
        for(char c: column.toCharArray() ) {
            columnIndex = columnIndex * 26 + (c - 'A' + 1);     //dla AB => cI = 0*26+('A'-'A'+1)=1; cI=1*26+('B'-'A'+1)=28
        }
        columnIndex--;
        int rowIndex = Integer.parseInt(row) - 1;

        return new int[]{rowIndex, columnIndex};
    }

    public static String getReference(String[][] input, String str) {
        int[] refIndices = parseReference(str);
        String res = input[refIndices[0]][refIndices[1]];
        while (true){
            assert res != null;
            if (isInt(res)) break;
            if(isReference(res)){
                refIndices = parseReference(res);
                res = input[refIndices[0]][refIndices[1]];
            } else {
                res = evaluateExpression(input, res);
            }
        }
        return res;
    }

    public static int getValue(String[][] input, String cell){
        if(isInt(cell)){
            return Integer.parseInt(cell);
        } else if (isReference(cell)){
            return Integer.parseInt(getReference(input, cell));

        } else return 0;
    }

    public static int[] getNumbers (String[][] input, String expression){
        expression = expression.substring(4, expression.length() - 1);
        String[] numbers = expression.split(",");
        int number1 = getValue(input, numbers[0]);
        int number2 = getValue(input, numbers[1]);
        return new int[]{number1, number2};
    }

    public static String evaluateExpression(String[][] input, String expression){
        expression = expression.substring(1);   //usuwam "="
        int[] numbers = getNumbers(input, expression);
        if (expression.startsWith("ADD")) {
            return String.valueOf(numbers[0] + numbers[1]);
        } else if (expression.startsWith("SUB")) {
            return String.valueOf(numbers[0] - numbers[1]);
        } else if (expression.startsWith("MUL")) {
            return String.valueOf(numbers[0] * numbers[1]);
        } else if (expression.startsWith("DIV")) {
            if( numbers[1] != 0) {
                return String.valueOf(numbers[0] / numbers[1]);
            } else return "#DZIEL/0!";
        } else if (expression.startsWith("MOD")) {
            return String.valueOf(numbers[0] % numbers[1]);
        }
        return null;
    }


    public String[][] calculate(String[][] input) {
        for(int i = 0; i < input.length; i++) {
            for(int j = 0; j < input[i].length; j++) {
                if (isReference(input[i][j])) {
                    input[i][j] = getReference(input, input[i][j]);
                } else if(input[i][j].startsWith("=")){
                    input[i][j] = evaluateExpression(input, input[i][j]);
                }
            }
        }
        return input;
    }
}
