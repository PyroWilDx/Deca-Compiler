package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GameBoyManager;
import fr.ensimag.deca.codegen.GameBoyUtils;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LineGb;

import java.util.List;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Print extends AbstractPrint {
    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex  if true, then float should be displayed as hexadecimal (printx)
     */
    public Print(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) throws ContextualError {
        GameBoyManager gbM = compiler.getGameBoyManager();
        List<AbstractExpr> argumentList = this.getArguments().getList();
        if (argumentList.isEmpty()) return;

        int numberOfArguments = argumentList.size();
        int x, y;
        if (numberOfArguments != 3 && numberOfArguments != 1) {
            throw new ContextualError("Print must have one or three arguments (print(\"message\") or print(\"message\", x, y)", getLocation());
        }
        if (numberOfArguments == 1) {
            x = 5;
            y = 5;
        } else {
            x = argumentList.get(1).asIntLiteral("Print second argument must be an int literal.", getLocation()).getValue();
            y = argumentList.get(2).asIntLiteral("Print third argument must be an int literal.", getLocation()).getValue();
        }

        int printId = gbM.getAndIncrPrintId();

        Label waitVBlankLabel = new Label("WaitVBlank" + printId);
        compiler.addLabel(waitVBlankLabel);
        // Turn screen off.
        compiler.add(new LineGb("; Turn the LCD off"));
        compiler.add(new LineGb("ld a, 0"));
        compiler.add(new LineGb("ld [rLCDC], a"));

        AbstractExpr firstArg = argumentList.get(0);
        String message;
        message = firstArg.asStringLiteral("Print first argument must be a string literal.",
                getLocation()).getValue();


        for (int i = 1; i < message.length() - 1; i++) {
            char l = message.charAt(i);
            String tileIndex = GameBoyUtils.getLetterAdress(l);
            int addrInTileMap = 38912 + y * 32 + x;
            compiler.add(new LineGb("ld hl, " + addrInTileMap));
            compiler.add(new LineGb("ld [hl], " + tileIndex));
            if (x % 32 == 19) x += 12;
            x++;
        }


        compiler.add(new LineGb("; Turn the LCD on"));
        compiler.add(new LineGb("ld a, LCDCF_ON | LCDCF_BGON | LCDCF_OBJON"));
        compiler.add(new LineGb("ld [rLCDC], a"));
    }

    @Override
    String getSuffix() {
        return "";
    }
}
