package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class New extends AbstractExpr {
    private final AbstractIdentifier type;

    public New(AbstractIdentifier type) {
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type t = this.type.verifyType(compiler);
        if (!t.isClass()) {
            throw new ContextualError("'" + t.getName() +
                    "' is not a class.", getLocation());
        }
        this.setType(t);
        return t;
        // Done
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        ErrorManager eM = compiler.getErrorManager();
        VTableManager vTM = compiler.getVTableManager();

        vTM.enterClass(type.getType().getName().getName());

        int fieldsCount = vTM.getCurrFieldCountOfClass();

        GPRegister gpReg = rM.getFreeReg();

        compiler.addInstruction(new NEW(fieldsCount + 1, gpReg));
        if (compiler.getCompilerOptions().doCheck()) {
            compiler.addInstruction(new BOV(eM.getHeapOverflowLabel()));
        }
        compiler.addInstruction(new LEA(vTM.getCurrClassAddr(), Register.R0));
        compiler.addInstruction(
                new STORE(Register.R0, new RegisterOffset(0, gpReg)));
        compiler.addInstruction(new PUSH(gpReg));
        compiler.addInstruction(new BSR(LabelUtils.getClassInitLabel(vTM.getCurrClassName())));
        compiler.addInstruction(new POP(gpReg));

        rM.freeReg(gpReg);

        vTM.exitClass();
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        StackManager sM = compiler.getStackManager();
        VTableManager vTM = compiler.getVTableManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        vTM.enterClass(type.getType().getName().getName());
        int fieldsCount = vTM.getCurrFieldCountOfClass();
        Label initMethodLabel = LabelUtils.getClassInitLabel(vTM.getCurrClassName());
        vTM.exitClass();

        // Pour faire un New statique (Décommenter aussi dans GameBoyManager)
//        int currFieldAddr = gbM.getNextDynamicFieldAddr();
//        gbM.addDynamicFields(fieldsCount);

        int parentMethodVarOffset = 0;
        if (vTM.isInMethod()) {
            parentMethodVarOffset = gbM.getCurrMethodVarCount(vTM);
            parentMethodVarOffset += sM.getTmpVar();
            compiler.addInstruction(new SUBSP(parentMethodVarOffset * 2));
        }

        // Load Next Dynamic Field Addr and PUSH
        compiler.addInstruction(new LOAD_INT(GameBoyManager.dynamicFieldsCptAddr, Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));
        compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));
        compiler.addInstruction(new PUSH(Register.HL));

//        compiler.addInstruction(new LOAD_INT(currFieldAddr, Register.HL)); // New Statique

        GPRegister gpReg = rM.getFreeReg();

        // Increment Next Dynamic Field Addr
        compiler.addInstruction(new LOAD_INT(GameBoyManager.dynamicFieldsCptAddr, Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A)); // High
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L)); // Low
        compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));
        GameBoyManager.incHLByValue(compiler, fieldsCount * 2);
        compiler.addInstruction(new LOAD_INT(GameBoyManager.dynamicFieldsCptAddr, gpReg));
        compiler.addInstruction(new LOAD_REG(Register.HL.getHighReg(), Register.A));
        compiler.addInstruction(new STORE_REG(Register.A, gpReg));
        compiler.addInstruction(new DEC_REG(gpReg));
        compiler.addInstruction(new LOAD_REG(Register.HL.getLowReg(), Register.A));
        compiler.addInstruction(new STORE_REG(Register.A, gpReg));

        // Call Init
        compiler.addInstruction(new BSR(initMethodLabel));

        // Pop Addr
        compiler.addInstruction(new POP(gpReg));

        if (vTM.isInMethod()) {
            compiler.addInstruction(new ADDSP(parentMethodVarOffset * 2));
        }

        rM.freeReg(gpReg);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        type.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
    }
}
