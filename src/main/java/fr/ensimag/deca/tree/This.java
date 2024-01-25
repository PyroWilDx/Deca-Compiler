package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GameBoyManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class This extends AbstractExpr {
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if (currentClass.getType().equals(compiler.environmentType.OBJECT)) {
            throw new ContextualError("'this' cannot refer to Object class.", getLocation());
        }
        this.setType(currentClass.getType());
        return currentClass.getType();
        // Done
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        GPRegister gpReg = rM.getFreeReg();
        compiler.addInstruction(
                new LOAD(new RegisterOffset(-2, Register.LB), gpReg));
        rM.freeReg(gpReg);
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        GPRegister gpReg = rM.getFreeReg();
        int spOffset = gbM.getCurrMethodSpOffset();
        compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 3 + spOffset));
        compiler.addInstruction(new LOAD_VAL(Register.HL, gpReg.getHighReg()));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, gpReg.getLowReg()));
        rM.freeReg(gpReg);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {

    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }
}
