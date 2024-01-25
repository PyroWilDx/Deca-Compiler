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

public class Cast extends AbstractExpr {
    private final AbstractIdentifier type;
    private final AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        this.type.verifyType(compiler);
        this.expr.verifyExpr(compiler, localEnv, currentClass);
        if (this.expr.getType().isInt() || this.expr.getType().isFloat()) {
            if (this.type.getType().isInt() || this.type.getType().isFloat()) {
                this.setType(this.type.getType());
                return this.type.getType();
            }
        }
        if (this.expr.getType().isVoid() || (
                !compiler.environmentType.subtype(this.expr.getType(), this.type.getType()) &&
                        !compiler.environmentType.subtype(this.type.getType(), this.expr.getType())
        )) {
            throw new ContextualError("Illegal cast.", this.getLocation());
        }
        this.setType(this.type.getType());
        return this.type.getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        CondManager cM = compiler.getCondManager();
        VTableManager vTM = compiler.getVTableManager();

        expr.codeGenInst(compiler);
        if (!type.getType().equals(expr.getType())) {
            GPRegister gpReg = rM.getLastRegOrImm(compiler);
            GPRegister targetReg = gpReg;
            if (gpReg == Register.R0) {
                targetReg = rM.getFreeReg();
            }
            if (type.getType().isInt()) {
                compiler.addInstruction(new INT(gpReg, targetReg));
            } else if (type.getType().isFloat()) {
                compiler.addInstruction(new FLOAT(gpReg, targetReg));
            } else {
                if (gpReg == Register.R0) {
                    compiler.addInstruction(new LOAD(Register.R0, targetReg));
                }

                long idCpt = cM.getUniqueId();
                Label startLabel = new Label("startCastInstanceOf" + idCpt);
                Label errorLabel = new Label("errorCastInstanceOf" + idCpt);
                Label endLabel = new Label("endCastInstanceOf" + idCpt);

                compiler.addInstruction(new CMP(new NullOperand(), targetReg));
                compiler.addInstruction(new BEQ(endLabel));

                compiler.addInstruction(new LEA(vTM.getClassAddr(type.getName().getName()), Register.R0));
                compiler.addInstruction(new LOAD(targetReg, Register.R1));

                compiler.addLabel(startLabel);
                compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R1), Register.R1));

                compiler.addInstruction(new CMP(new NullOperand(), Register.R1));
                compiler.addInstruction(new BEQ(errorLabel));

                compiler.addInstruction(new CMP(Register.R0, Register.R1));
                compiler.addInstruction(new BEQ(endLabel));

                compiler.addInstruction(new BRA(startLabel));

                compiler.addLabel(errorLabel);
                compiler.addInstruction(new WSTR("Error: Failed to cast variable of type '" +
                        expr.getType().getName() + "' to class '" + type.getName() + "'"));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new ERROR());

                compiler.addLabel(endLabel);
            }
            rM.freeReg(gpReg);
            if (targetReg != gpReg) rM.freeReg(targetReg);
        }
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        // J'pense y aura pas de cast en GameBoy xd
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(")");
        s.print("(");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
        expr.iterChildren(f);
    }
}
