package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {
    @MyTest
    public void testSomething() {
        System.out.println("I'm testing something!");
    }

    @MyTest
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest(intParams = {1, 2}, intExpected = {3})
    public int test1CorrectAddition(int a, int b) {
        return a + b;
    }

    @MyTest(intParams = {1, 2}, intExpected = {3})
    public int test2IncorrectAddition(int a, int b) {
        return a + b + 1;
    }

    @MyTest(strParams = {"Hello", "World"}, strExpected = {"HelloWorld"})
    public String test3Concatenation(String a, String b) {
        return a + b;
    }

    @MyTest(strParams = {"Hello", "World"}, strExpected = {"HelloWorld"})
    public String test4IncorrectConcatenation(String a, String b) {
        return a + b + "!";
    }

    @MyTest(intParams = {0, 0}, intExpected = {0})
    public int test5ZeroCorrect(int a, int b) {
        return a + b;
    }

    @MyTest(intParams = {1, 2}, intExpected = {4})
    public int test6IncorrectAddition(int a, int b) {
        return a + b;
    }

    @MyTest(strParams = {"test"}, strExpected = {"test"})
    public void test7error(String str) {
        System.out.println(str);
        //jest strExpected a funkcja jest void
    }

    @MyTest(strParams = {"test"})
    public int test8error(String str) {
        //jest zwrocona warosc a nie ma expected
        return 0;
    }

    @MyTest(strParams = {"test"}, strExpected = {"test"})
    public int test9ArrayIndexOutOfBoundsError(String str) {
        //zwracam element poza rozmiarem tablicy
        int[] tablica = new int[10];
        return tablica[20];
    }

    @MyTest(strParams = {"test", "test2"}, strExpected = {"test"})
    public int test10error(String str) {
        //rozna liczba argumentow
        int[] tablica = new int[10];
        return tablica[20];
    }

    @MyTest(strParams = {"ALEKSANDRA"}, strExpected = {"aleksandra"})
    public String test11toLowerCaseCorrect(String str) {
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append(Character.toLowerCase(currentChar));
            } else result.append(currentChar);
        }
        return result.toString();
    }

    @MyTest(strParams = {"ALEKSANDRA"}, strExpected = {"aleksandra"})
    public String test12toLowerCaseIncorrect(String str) {
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                result.append(Character.toLowerCase(currentChar));
            } else result.append(currentChar);
        }
        return result.toString();
    }

    @MyTest(intParams = {2}, strParams = {"aleksandra"}, strExpected = {"aLeKsAnDrA"})
    public String test13changeCase(int n, String word) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            if ((i + 1) % n == 0) {
                result.append(Character.toUpperCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    @MyTest
    public void test14CorrectAssert() {
        int result = 2 + 2;
        assert result == 4 : "2 + 2 should be 4";
    }

    @MyTest
    public void test15IncorrectAssert() {
        int result = 2 + 2;
        assert result == 5 : "2 + 2 should not be 5";
    }

    //7 success
    //5 fail
    //5 error
}
