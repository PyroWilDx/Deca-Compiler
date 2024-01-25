package fr.ensimag.ima.pseudocode;

import java.io.PrintStream;

public class LineAsm extends AbstractLine {

    private final String asm;

    public LineAsm(String asm) {
        this.asm = asm;
    }

    @Override
    void display(PrintStream s) {
        s.println(asm);
    }
}
