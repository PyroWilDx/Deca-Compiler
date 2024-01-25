package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int doOpInt(int a, int b) {
        return a % b;
    }

    @Override
    protected float doOpFloat(float a, float b) {
        return 0.f;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) { // Opti % 2
        RegManager rM = compiler.getRegManager();

        if ((getRightOperand() instanceof IntLiteral) &&
                ((IntLiteral) getRightOperand()).getValue() == 2) {
            if (getLeftOperand() instanceof IntLiteral) {
                super.codeGenInst(compiler);
                return;
            }
            getLeftOperand().codeGenInst(compiler);
            GPRegister lReg = rM.getLastRegOrImm(compiler);
            if (lReg == Register.R0) {
                lReg = rM.getFreeReg();
                compiler.addInstruction(new LOAD(Register.R0, lReg));
            }
            if (rM.isUsingAllRegs()) {
                compiler.addInstruction(new PUSH(Register.R0));
                compiler.addInstruction(new LOAD(lReg, Register.R0));
                compiler.addInstruction(new SHR(Register.R0));
                compiler.addInstruction(new SHL(Register.R0));
                compiler.addInstruction(new SUB(Register.R0, lReg));
                compiler.addInstruction(new POP(Register.R0));
            } else {
                GPRegister div2Reg = rM.getFreeReg();
                compiler.addInstruction(new LOAD(lReg, div2Reg));
                compiler.addInstruction(new SHR(div2Reg));
                compiler.addInstruction(new SHL(div2Reg));
                compiler.addInstruction(new SUB(div2Reg, lReg));
                rM.freeReg(div2Reg);
            }
            rM.freeReg(lReg);
        } else {
            super.codeGenInst(compiler);
        }
    }

    @Override
    protected void codeGenOpArith(DecacCompiler compiler,
                                  DVal valReg, GPRegister saveReg) {
        ErrorManager eM = compiler.getErrorManager();

        compiler.addInstruction(new REM(valReg, saveReg));
        if (compiler.getCompilerOptions().doCheck()) {
            compiler.addInstruction(new BOV(eM.getDivBy0Label()));
        }
        // Done
    }

    @Override
    protected void codeGenOpArithGb(DecacCompiler compiler,
                                    DVal valReg, GPRegister saveReg) {
        CondManager cM = compiler.getCondManager();

        Label startLabel = new Label("ModuloStart" + cM.getUniqueId());
        Label endLabel = new Label("ModuloEnd" + cM.getUniqueId());

        compiler.addInstruction(new LOAD_GEN(valReg, Register.A));
        compiler.addInstruction(new CMP_A(0, Register.A));
        compiler.addInstruction(new BEQ(endLabel));

        compiler.addInstruction(new LOAD_REG(saveReg, Register.A));

        compiler.addLabel(startLabel);
        compiler.addInstruction(new CMP_A(valReg, Register.A));
        compiler.addInstruction(new BLT(endLabel));
        compiler.addInstruction(new SUB_A(valReg, Register.A));

        compiler.addInstruction(new BRA(startLabel));
        compiler.addLabel(endLabel);
        compiler.addInstruction(new LOAD_REG(Register.A, saveReg));
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

}
