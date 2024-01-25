package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl47
 * @date 01/01/2024
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }

    public ListDeclClass getClasses() {
        return classes;
    }

    public AbstractMain getMain() {
        return main;
    }

    private final ListDeclClass classes;
    private final AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        classes.verifyListClass(compiler);
        classes.verifyListClassMembers(compiler);
        classes.verifyListClassBody(compiler);
        main.verifyMain(compiler);
        LOG.debug("verify program: end");
        // Done
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        ErrorManager eM = compiler.getErrorManager();
        StackManager sM = new StackManager(false);
        compiler.setStackManager(sM);
        VTableManager vTM = compiler.getVTableManager();

        boolean generateObjectClass = !classes.getList().isEmpty();

        Label eLabel = null;
        if (generateObjectClass) {
            compiler.addComment("VTable of " + LabelUtils.OBJECT_CLASS_NAME);
            DAddr nAddr = sM.getOffsetAddr();
            LabelUtils.setObjectClassSymbol(compiler.environmentType.OBJECT.getName());
            VTable vT = new VTable(null, LabelUtils.OBJECT_CLASS_SYMBOL, nAddr);
            vTM.addVTable(LabelUtils.OBJECT_CLASS_NAME, vT);
            compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, nAddr));
            sM.incrVTableCpt();

            DAddr eAddr = sM.getOffsetAddr();
            vT.addMethod(LabelUtils.EQUALS_METHOD_NAME, 1);
            vT.addParamToMethod(LabelUtils.EQUALS_METHOD_NAME, "obj", -3);
            eLabel = LabelUtils.getMethodLabel(LabelUtils.OBJECT_CLASS_NAME, LabelUtils.EQUALS_METHOD_NAME);
            compiler.addInstruction(new LOAD(new LabelOperand(eLabel), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, eAddr));
            sM.incrVTableCpt();
        }

        classes.codeGenVTable(compiler);

        compiler.addComment("Start of Main Program");
        compiler.addComment("Main Program");

        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        compiler.addComment("End of Main Program");

        if (sM.getMaxStackSize() > 0) {
            if (sM.getAddSp() > 0) {
                compiler.addInstruction(0, new ADDSP(sM.getAddSp()));
            }
            if (compiler.getCompilerOptions().doCheck()) {
                compiler.addInstruction(0, new BOV(eM.getStackOverflowLabel()));
                compiler.addInstruction(0, new TSTO(sM.getMaxStackSize()));
            }
        }

        if (generateObjectClass) {
            compiler.addComment("");
            compiler.addComment("Class " + LabelUtils.OBJECT_CLASS_NAME);
            compiler.addLabel(eLabel);
            compiler.addInstruction(
                    new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
            compiler.addInstruction(
                    new CMP(new RegisterOffset(-3, Register.LB), Register.R0));
            compiler.addInstruction(new SEQ(Register.R0));
            compiler.addInstruction(new RTS());
        }

        classes.codeGenListDeclClass(compiler);

        compiler.addComment("");

        compiler.addComment("Start of Error Labels");
        eM.codeGenAllErrors(compiler);
        compiler.addComment("End of Error Labels");
        // Done
    }

    @Override
    public void codeGenProgramGb(DecacCompiler compiler) throws ContextualError{
        RegManager rM = compiler.getRegManager();
        StackManager sM = new StackManager(false);
        compiler.setStackManager(sM);
        VTableManager vTM = compiler.getVTableManager();

        compiler.add(new LineGb("INCLUDE \"hardware.inc\""));

        if (false) {
            compiler.add(new LineGb("INCLUDE \"utils.asm\""));
            compiler.add(new LineGb("INCLUDE \"variables.asm\""));
        }

        compiler.add(new LineGb(""));
        compiler.add(new LineGb("SECTION \"Header\", ROM0[$100]"));
        compiler.add(new LineGb(""));

        if (false) {
            compiler.add(new LineGb("call initVariables"));
        }

        compiler.add(new LineGb("jp EntryPoint"));
        compiler.add(new LineGb("ds $150 - @, 0"));

        compiler.add(new LineGb(""));
        compiler.addLabel(new Label("EntryPoint"));
        compiler.addComment("Shut down audio circuitry");
        compiler.add(new LineGb("ld a, 0"));
        compiler.add(new LineGb("ld [rNR52], a"));

        compiler.add(new LineGb("ld SP, " + GameBoyManager.Addr0));
        GPRegister gpReg = rM.getFreeReg();
        compiler.addInstruction(new LOAD_INT(GameBoyManager.AddrMax, gpReg));
        compiler.addInstruction(new LOAD_INT(GameBoyManager.dynamicFieldsCptAddr, Register.HL));
        compiler.addInstruction(new STORE_REG(gpReg.getHighReg(), Register.HL));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new STORE_REG(gpReg.getLowReg(), Register.HL));

        boolean generateObjectClass = !classes.getList().isEmpty();

        if (generateObjectClass) {
            compiler.addComment("VTable of " + LabelUtils.OBJECT_CLASS_NAME);
            LabelUtils.setObjectClassSymbol(compiler.environmentType.OBJECT.getName());
            VTable vT = new VTable(null, LabelUtils.OBJECT_CLASS_SYMBOL, null);
            vTM.addVTable(LabelUtils.OBJECT_CLASS_NAME, vT);
            vT.addMethod(LabelUtils.EQUALS_METHOD_NAME, 1);
            vT.addParamToMethod(LabelUtils.EQUALS_METHOD_NAME, "obj", -3);
        }

        classes.codeGenVTableGb(compiler);

        compiler.addComment("Start of Main Program");
        compiler.addComment("Main Program");

        main.codeGenMainGb(compiler);
        Label doneLabel = new Label("Done");
        compiler.addLabel(doneLabel);
        compiler.addInstruction(new HALT());
        compiler.addInstruction(new BRA(doneLabel));

        compiler.addComment("End of Main Program");

        if (generateObjectClass) {
            compiler.addComment("");
            compiler.addComment("Class " + LabelUtils.OBJECT_CLASS_NAME);

            String objClassName = LabelUtils.OBJECT_CLASS_NAME;
            String eqMethodName = LabelUtils.EQUALS_METHOD_NAME;
            Label eLabel = LabelUtils.getMethodLabel(objClassName, eqMethodName);
            Label falseLabel = new Label("equalsFalse");
            Label endLabel = LabelUtils.getMethodEndLabel(objClassName, eqMethodName);

            compiler.addLabel(eLabel);

            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 3));
            compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A)); // High Addr of Instance
            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 5));
            compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));  // High Addr of Arg
            compiler.addInstruction(new CMP_A(GPRegister.L, Register.A));
            compiler.addInstruction(new BNE(falseLabel));

            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 2));
            compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A)); // Low Addr of Instance
            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 4));
            compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));  // Low Addr of Arg
            compiler.addInstruction(new CMP_A(GPRegister.L, Register.A));
            compiler.addInstruction(new BNE(falseLabel));

            compiler.addInstruction(new LOAD_INT(1, Register.HL));
            compiler.addInstruction(new BRA(endLabel));

            compiler.addLabel(falseLabel);
            compiler.addInstruction(new LOAD_INT(0, Register.HL));

            compiler.addLabel(endLabel);
            compiler.addInstruction(new RTS());
        }

        classes.codeGenListDeclClassGb(compiler);

        GameBoyUtils.putHelloWorld(compiler);
        if (compiler.noTileInclude()) {
            compiler.add(new LineGb("Tiles:"));
            compiler.add(new LineGb("db $00,$00,$00,$00,$00,$00,$00,$00"));
            compiler.add(new LineGb("TilesEnd:"));
        }
     }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
