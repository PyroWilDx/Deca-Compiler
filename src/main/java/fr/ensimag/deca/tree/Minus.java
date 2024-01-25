package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD_REG;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.SUB_A;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected int doOpInt(int a, int b) {
        return a - b;
    }

    @Override
    protected float doOpFloat(float a, float b) {
        return a - b;
    }

    @Override
    protected void codeGenOpArith(DecacCompiler compiler,
                                  DVal valReg, GPRegister saveReg) {
        compiler.addInstruction(new SUB(valReg, saveReg));
        // Done
    }

    @Override
    protected void codeGenOpArithGb(DecacCompiler compiler,
                                    DVal valReg, GPRegister saveReg) {
        compiler.addInstruction(new LOAD_REG(saveReg.getLowReg(), Register.A));
        compiler.addInstruction(new SUB_A(valReg, Register.A));
        compiler.addInstruction(new LOAD_REG(Register.A, saveReg.getLowReg()));
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

}
