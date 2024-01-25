package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.OPP;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected void codeGenOpUnary(DecacCompiler compiler, GPRegister gpReg) {
        compiler.addInstruction(new OPP(gpReg, gpReg));
        // Done
    }

    @Override
    protected void codeGenOpUnaryGb(DecacCompiler compiler, GPRegister gpReg) {
        // Non
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

}
