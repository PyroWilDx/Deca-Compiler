package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GameBoyManager;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LineGb;
import fr.ensimag.ima.pseudocode.instructions.WNL;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.addInstruction(new WNL());
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        GameBoyManager gbM = compiler.getGameBoyManager();

        int printId = gbM.getAndIncrPrintId();

        Label waitVBlankLabel = new Label("WaitVBlank" + printId);
        compiler.addLabel(waitVBlankLabel);
        compiler.add(new LineGb("ld a, [rLY]"));
        compiler.add(new LineGb("cp 144"));
        compiler.add(new LineGb("jp c, " + waitVBlankLabel));
        compiler.add(new LineGb("ld a, 0"));
        compiler.add(new LineGb("ld [rLCDC], a"));
        compiler.add(new LineGb("ld de, PrintTiles"));
        compiler.add(new LineGb("ld hl, $9000"));
        compiler.add(new LineGb("ld bc, PrintTilesEnd - PrintTiles"));

        Label copyTilesLabel = new Label("CopyTiles" + printId);
        compiler.addLabel(copyTilesLabel);
        compiler.add(new LineGb("ld a, [de]"));
        compiler.add(new LineGb("ld [hli], a"));
        compiler.add(new LineGb("inc de"));
        compiler.add(new LineGb("dec bc"));
        compiler.add(new LineGb("ld a, b"));
        compiler.add(new LineGb("or a, c"));
        compiler.add(new LineGb("jp nz, " + copyTilesLabel));
        compiler.add(new LineGb("ld de, PrintTilemap"));
        compiler.add(new LineGb("ld hl, $9800"));
        compiler.add(new LineGb("ld bc, PrintTilemapEnd - PrintTilemap"));

        Label copyTilemapLabel = new Label("CopyTilemap" + printId);
        compiler.addLabel(copyTilemapLabel);
        compiler.add(new LineGb("ld a, [de]"));
        compiler.add(new LineGb("ld [hli], a"));
        compiler.add(new LineGb("inc de"));
        compiler.add(new LineGb("dec bc"));
        compiler.add(new LineGb("ld a, b"));
        compiler.add(new LineGb("or a, c"));
        compiler.add(new LineGb("jp nz, " + copyTilemapLabel));
        compiler.add(new LineGb("ld a, LCDCF_ON | LCDCF_BGON"));
        compiler.add(new LineGb("ld [rLCDC], a"));
        compiler.add(new LineGb("ld a, %11100100"));
        compiler.add(new LineGb("ld [rBGP], a"));

        compiler.addLabel(new Label("endPrint" + printId));
    }

    @Override
    String getSuffix() {
        return "ln";
    }
}
