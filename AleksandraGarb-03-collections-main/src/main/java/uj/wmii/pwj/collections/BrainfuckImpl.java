package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Stack;

public class BrainfuckImpl implements Brainfuck{
    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final byte[] memory;
    private int pointer;

    public BrainfuckImpl(String program, PrintStream out, InputStream in, int stackSize) {

        this.program = program;
        this.out = out;
        this.in = in;
        this.memory = new byte[stackSize];
        this.pointer = 0;
    }

    @Override
    public void execute() {
        Stack<Integer> loop = new Stack<>();
        for(int i = 0; i < program.length(); i++) {
            char command = program.charAt(i);
            switch(command) {
                case '+' -> memory[pointer]++;
                case '-' -> memory[pointer]--;
                case '>' -> pointer = pointer + 1;
                case '<' -> pointer = pointer - 1;
                case '.' -> out.print((char) (memory[pointer]));
                case ',' -> {
                    try{
                        int input = in.read();
                        memory[pointer] = (byte) input;
                    } catch (IOException e){
                        throw new RuntimeException(e);
                    }
                }
                case '[' -> {
                    if (memory[pointer] == 0) {  //szukam konca petli
                        int loops = 1;
                        while (loops > 0) {
                            i++;
                            if (i >= program.length()) {
                                throw new IllegalStateException("Not close '['");
                            }
                            if (program.charAt(i) == '[') {
                                loops++;
                            } else if (program.charAt(i) == ']') {
                                loops--;
                            }
                        }
                    } else {
                        loop.push(i);
                    }
                }
                case ']' -> {
                    if (loop.isEmpty()) {
                        throw new IllegalStateException("Not open ']'");
                    }
                    if (memory[pointer] != 0) {
                        i = loop.peek();
                    } else {
                        loop.pop();
                    }
                }
                default -> {}
            }
        }
    }
}
