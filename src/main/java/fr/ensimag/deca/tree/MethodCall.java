package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;
import java.util.List;

public class MethodCall extends AbstractMethodCall {
    private final AbstractExpr expr;
    private final AbstractIdentifier methodIdent;
    private final RValueStar rValueStar;

    public MethodCall(AbstractExpr expr, AbstractIdentifier methodIdent, RValueStar rValueStar) {
        this.expr = expr;
        this.methodIdent = methodIdent;
        this.rValueStar = rValueStar;
    }

    public Type verifyMethodCall(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type type = this.expr.verifyExpr(compiler, localEnv, currentClass);
        TypeDefinition definition = compiler.environmentType.get(type.getName());
        ClassDefinition classDefinition = definition.asClassDefinition("Method call on native type.", getLocation());

        EnvironmentExp classEnv = classDefinition.getMembers();
        MethodIdentNonTerminalReturn sigAndType = this.methodIdent.verifyMethodIdent(classEnv);
        Signature sig = sigAndType.getSignature();
        Type t = sigAndType.getType();
        this.setType(t);
        this.rValueStar.verifyRValueStar(compiler, localEnv, currentClass, sig);
        return t;
        // Done
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        return this.verifyMethodCall(compiler, localEnv, currentClass);
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        ErrorManager eM = compiler.getErrorManager();
        CondManager cM = compiler.getCondManager();
        VTableManager vTM = compiler.getVTableManager();

        vTM.enterMethodCall();

        vTM.enterClass(expr.getType().getName().getName());
        vTM.enterMethod(methodIdent.getName().getName());

        int addSp = vTM.getCurrParamCountOfMethod() + 1;
        int methodOffset = vTM.getCurrMethodOffset();

        vTM.exitMethod();
        vTM.exitClass();

        compiler.addInstruction(new ADDSP(addSp));

        expr.codeGenInst(compiler);
        GPRegister gpReg = rM.getLastReg();
        compiler.addInstruction(new STORE(gpReg, new RegisterOffset(0, Register.SP)));
        rM.freeReg(gpReg);

        int currParamIndex = -1;
        for (AbstractExpr arg : rValueStar.getList()) {
            arg.codeGenInst(compiler);
            gpReg = rM.getLastRegOrImm(compiler);
            compiler.addInstruction(
                    new STORE(gpReg, new RegisterOffset(currParamIndex, Register.SP)));
            rM.freeReg(gpReg);
            currParamIndex--;
        }

        gpReg = rM.getFreeReg();

        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), gpReg));
        if (compiler.getCompilerOptions().doCheck()) {
            compiler.addInstruction(new CMP(new NullOperand(), gpReg));
            compiler.addInstruction(new BEQ(eM.getNullPointerLabel()));
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(0, gpReg), gpReg));

        compiler.addInstruction(
                new BSR(new RegisterOffset(methodOffset, gpReg)));

        rM.freeReg(gpReg);

        compiler.addInstruction(new SUBSP(addSp));

        if (!getType().isVoid()) {
            rM.freeRegForce(Register.R0);
        }

        if (!getType().isClass() && cM.isDoingCond() &&
                branchLabel != null && cM.isNotDoingOpCmp()) {
            rM.getLastReg(); // On enlève R0
            compiler.addInstruction(new CMP(0, Register.R0));
            if (isInTrue) compiler.addInstruction(new BNE(branchLabel));
            else compiler.addInstruction(new BEQ(branchLabel));
        } else {
            if (getType().isBoolean() && !isInTrue) {
                rM.getLastReg(); // On enlève R0
                gpReg = rM.getFreeReg();
                compiler.addInstruction(new CMP(0, Register.R0));
                compiler.addInstruction(new SEQ(gpReg));
                rM.freeReg(gpReg);
            }
        }

        vTM.exitMethodCall();
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        StackManager sM = compiler.getStackManager();
        CondManager cM = compiler.getCondManager();
        VTableManager vTM = compiler.getVTableManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        vTM.enterMethodCall();

        vTM.enterClass(expr.getType().getName().getName());
        vTM.enterMethod(methodIdent.getName().getName());

        int addSp = vTM.getCurrParamCountOfMethod() * 2 + 2;
        Label mLabel = vTM.getCurrMethodLabel();

        vTM.exitMethod();
        vTM.exitClass();

        int parentMethodVarOffset = 0;
        if (vTM.isInMethod()) {
            int lastSpOffset = gbM.getCurrMethodSpOffset();
            if (lastSpOffset == 0) {
                parentMethodVarOffset = gbM.getCurrMethodVarCount(vTM);
                parentMethodVarOffset += sM.getTmpVar();
                parentMethodVarOffset *= 2;
            } else {
                parentMethodVarOffset = 0;
            }

            gbM.pushCurrMethodSpOffset(parentMethodVarOffset);
            compiler.addInstruction(new SUBSP(parentMethodVarOffset));
            // Normalement, après ça c'est pas possible d'avoir "ld hl, SP+e8" avec e8 négatif
        }

        List<AbstractExpr> args = rValueStar.getList();
        for (int i = args.size() - 1; i >= 0; i--) {
            args.get(i).codeGenInstGb(compiler);
            GPRegister gpReg = rM.getLastRegOrImm(compiler);
            compiler.addInstruction(new PUSH(gpReg));
            if (vTM.isInMethod()) {
                gbM.incr2CurrMethodSpOffset();
            }
            rM.freeReg(gpReg);
        }

        expr.codeGenInstGb(compiler);
        GPRegister gpReg = rM.getLastReg();
        compiler.addInstruction(new PUSH(gpReg));
        rM.freeReg(gpReg);

        compiler.addInstruction(new BSR(mLabel));

        if (vTM.isInMethod()) {
            compiler.addInstruction(new ADDSP(parentMethodVarOffset));
            gbM.popCurrMethodSpOffset();
        }

        compiler.addInstruction(new ADDSP(addSp));

        if (!getType().isVoid()) {
            rM.freeRegForce(Register.HL);
        }

        if (!getType().isClass() && cM.isDoingCond() &&
                branchLabel != null && cM.isNotDoingOpCmp()) {
            rM.getLastReg(); // On enlève R0
            compiler.addInstruction(new LOAD_REG(Register.HL.getLowReg(), Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            if (isInTrue) compiler.addInstruction(new BNE(branchLabel));
            else compiler.addInstruction(new BEQ(branchLabel));
        } else {
            if (getType().isBoolean() && !isInTrue) {
                rM.getLastReg(); // On enlève R0
                gpReg = rM.getFreeReg();
                long id = cM.getUniqueId();
                Label falseLabel = new Label("SccFalse" + id);
                Label trueLabel = new Label("SccTrue" + id);
                Label endLabel = new Label("SccEnd" + id);

                compiler.addInstruction(new LOAD_REG(Register.HL.getLowReg(), Register.A));
                compiler.addInstruction(new CMP_A(0, Register.A));
                compiler.addInstruction(new BEQ(trueLabel));

                compiler.addLabel(falseLabel);
                compiler.addInstruction(new LOAD_INT(0, gpReg.getLowReg()));
                compiler.addInstruction(new BRA(endLabel));
                compiler.addLabel(trueLabel);
                compiler.addInstruction(new LOAD_INT(1, gpReg.getLowReg()));
                compiler.addLabel(endLabel);
                rM.freeReg(gpReg);
            }
        }

        vTM.exitMethodCall();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        this.expr.decompile(s);
        s.print(".");
        this.methodIdent.decompile(s);
        s.print("(");
        this.rValueStar.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        methodIdent.prettyPrint(s, prefix, false);
        rValueStar.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        methodIdent.iter(f);
        rValueStar.iter(f);
    }
}
