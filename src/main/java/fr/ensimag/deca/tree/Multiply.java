package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int doOpInt(int a, int b) {
        return a * b;
    }

    @Override
    protected float doOpFloat(float a, float b) {
        return a * b;
    }

    @Override
    protected void codeGenOpArith(DecacCompiler compiler,
                                  DVal valReg, GPRegister saveReg) {
        compiler.addInstruction(new MUL(valReg, saveReg));
    }

    @Override
    protected void codeGenOpArithGb(DecacCompiler compiler,
                                    DVal valReg, GPRegister saveReg) {
        CondManager cM = compiler.getCondManager();

        if (!(valReg instanceof GPRegister)) {
            int value = 0;
            if (valReg instanceof ImmediateInteger) {
                ImmediateInteger valImmInt = (ImmediateInteger) valReg;
                value = valImmInt.getValue();
            } else if (valReg instanceof ImmediateFloat) {
                ImmediateFloat valImmFloat = (ImmediateFloat) valReg;
                value = valImmFloat.getIntValue();
            }
            if (value != 0) {
                compiler.addInstruction(new LOAD_REG(saveReg, Register.A));
                for (int i = 0; i < value - 1; i++) {
                    compiler.addInstruction(new ADD_A(saveReg, Register.A));
                }
                compiler.addInstruction(new LOAD_REG(Register.A, saveReg));
            } else {
                compiler.addInstruction(new LOAD_INT(0, saveReg));
            }
        } else {
            long id = cM.getUniqueId();
            Label startLabel = new Label("MulStart" + id);
            Label set0Label = new Label("MulByZero" + id);
            Label setValueLabel = new Label("MulSetValue" + id);
            Label endLabel = new Label("MulEnd" + id);

            GPRegister valGpReg = (GPRegister) valReg;
            valGpReg = valGpReg.getLowReg();

            compiler.addInstruction(new LOAD_REG(valGpReg, Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            compiler.addInstruction(new BEQ(set0Label));

            compiler.addInstruction(new LOAD_REG(saveReg, Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            compiler.addInstruction(new BEQ(set0Label));

            compiler.addInstruction(new LOAD_REG(saveReg, Register.A));
            compiler.addInstruction(new LOAD_REG(valGpReg, GPRegister.L));

            compiler.addLabel(startLabel);
            compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));
            compiler.addInstruction(new LOAD_REG(GPRegister.L, Register.A));

            compiler.addInstruction(new SUB_A(1, Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            compiler.addInstruction(new BEQ(setValueLabel));

            compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.L));
            compiler.addInstruction(new LOAD_REG(GPRegister.H, Register.A));

            compiler.addInstruction(new ADD_A(saveReg, Register.A));

            compiler.addInstruction(new BRA(startLabel));

            compiler.addLabel(set0Label);
            compiler.addInstruction(new LOAD_INT(0, saveReg));
            compiler.addInstruction(new BRA(endLabel));

            compiler.addLabel(setValueLabel);
            compiler.addInstruction(new LOAD_REG(GPRegister.H, saveReg));

            compiler.addLabel(endLabel);
        }
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }

}
