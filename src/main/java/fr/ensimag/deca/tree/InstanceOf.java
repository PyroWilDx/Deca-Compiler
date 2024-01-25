package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class InstanceOf extends AbstractExpr {

    private final AbstractExpr expr;
    private final AbstractIdentifier type;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier type) {
        this.expr = expr;
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        this.expr.verifyExpr(compiler, localEnv, currentClass);
        this.type.verifyType(compiler);
        if ((!this.expr.getType().isClass() && !this.expr.getType().isNull()) || !this.type.getType().isClass()) {
            throw new ContextualError("InstanceOf can only be used on class types.", this.getLocation());
        }
        this.setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        CondManager cM = compiler.getCondManager();
        VTableManager vTM = compiler.getVTableManager();

        expr.codeGenInst(compiler);

        GPRegister gpReg = rM.getLastReg();
        if (gpReg == Register.R0) {
            gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD(Register.R0, gpReg));
        }

        long idCpt = cM.getUniqueId();
        Label startLabel = new Label("startInstanceOf" + idCpt);
        Label endTrueLabel = new Label("endTrueInstanceOf" + idCpt);
        Label endFalseLabel = new Label("endFalseInstanceOf" + idCpt);
        Label endLabel = new Label("endInstanceOf" + idCpt);

        compiler.addInstruction(new LEA(vTM.getClassAddr(type.getName().getName()), Register.R0));

        if (cM.isDoingCond() && branchLabel != null) {
            compiler.addLabel(startLabel);

            compiler.addInstruction(new LOAD(new RegisterOffset(0, gpReg), gpReg));

            compiler.addInstruction(new CMP(new NullOperand(), gpReg));
            if (isInTrue) compiler.addInstruction(new BEQ(endLabel));
            else compiler.addInstruction(new BEQ(branchLabel));

            compiler.addInstruction(new CMP(Register.R0, gpReg));
            if (isInTrue) compiler.addInstruction(new BEQ(branchLabel));
            else compiler.addInstruction(new BEQ(endLabel));

            compiler.addInstruction(new BRA(startLabel));

            compiler.addLabel(endLabel);
        } else {
            compiler.addLabel(startLabel);
            compiler.addInstruction(new LOAD(new RegisterOffset(0, gpReg), gpReg));

            compiler.addInstruction(new CMP(new NullOperand(), gpReg));
            if (isInTrue) compiler.addInstruction(new BEQ(endFalseLabel));
            else compiler.addInstruction(new BEQ(endTrueLabel));

            compiler.addInstruction(new CMP(Register.R0, gpReg));
            if (isInTrue) compiler.addInstruction(new BEQ(endTrueLabel));
            else compiler.addInstruction(new BEQ(endFalseLabel));

            compiler.addInstruction(new BRA(startLabel));

            compiler.addLabel(endTrueLabel);
            compiler.addInstruction(new LOAD(1, gpReg));
            compiler.addInstruction(new BRA(endLabel));

            compiler.addLabel(endFalseLabel);
            compiler.addInstruction(new LOAD(0, gpReg));

            compiler.addLabel(endLabel);
        }

        rM.freeReg(gpReg);
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        // J'pense y aura pas de instanceof en GameBoy xd
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expr.decompile(s);
        s.print("instanceof");
        type.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iterChildren(f);
        type.iterChildren(f);
    }
}
