package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelUtils;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.deca.codegen.VTableManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.LOAD_INT;
import fr.ensimag.ima.pseudocode.instructions.LOAD_REG;

import java.io.PrintStream;

public class Return extends AbstractInst {
    private AbstractExpr expr;

    public Return(AbstractExpr expr) {
        this.expr = expr;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType) throws ContextualError {
        if (returnType.isVoid()) {
            throw new ContextualError("Cannot use 'return' in void method.", getLocation());
        }
        this.expr = this.expr.verifyRValue(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        VTableManager vTM = compiler.getVTableManager();

        expr.codeGenInst(compiler);

        if (!expr.getType().isNull()) {
            DVal dVal = rM.getLastImm();
            if (dVal == null) {
                GPRegister gpReg = rM.getLastReg();
                compiler.addInstruction(new LOAD(gpReg, Register.R0));
                rM.freeReg(gpReg);
            } else {
                compiler.addInstruction(new LOAD(dVal, Register.R0));
            }
        }

        Label mEndLabel =
                LabelUtils.getMethodEndLabel(vTM.getCurrClassName(), vTM.getCurrMethodName());
        compiler.addInstruction(new BRA(mEndLabel));
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        VTableManager vTM = compiler.getVTableManager();

        expr.codeGenInstGb(compiler);

        DVal dVal = rM.getLastImm();
        if (dVal == null) {
            GPRegister gpReg = rM.getLastReg();
            compiler.addInstruction(new LOAD_REG(gpReg.getHighReg(), Register.HL.getHighReg()));
            compiler.addInstruction(new LOAD_REG(gpReg.getLowReg(), Register.HL.getLowReg()));
            rM.freeReg(gpReg);
        } else {
            compiler.addInstruction(new LOAD_INT(dVal, Register.HL));
        }

        Label mEndLabel =
                LabelUtils.getMethodEndLabel(vTM.getCurrClassName(), vTM.getCurrMethodName());
        compiler.addInstruction(new BRA(mEndLabel));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, true);
    }
}
