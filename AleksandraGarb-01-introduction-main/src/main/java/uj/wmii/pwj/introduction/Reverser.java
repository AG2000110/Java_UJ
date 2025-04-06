package uj.wmii.pwj.introduction;

public class Reverser {

    public String reverse(String input) {
        if(input == null) return input;
        input = input.trim();
        String result = "";
        for (int i = input.length() - 1; i >= 0; i--) {
            result += input.charAt(i);
        }
        return result;
    }

    public String reverseWords(String input) {
        if(input == null) return input;
        input = input.trim();
        String[] words = input.split(" ");
        String reversedWords = "";
        for (int i = words.length - 1; i >= 0; i--) {
            reversedWords += words[i];
            reversedWords += " ";
        }

        return reversedWords.trim();
    }

}
