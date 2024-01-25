package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 *
 * @author gl47
 * @date 01/01/2024
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getOperand().isInTrue = !isInTrue;
        getOperand().branchLabel = branchLabel;
        getOperand().codeGenInst(compiler);
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        getOperand().isInTrue = !isInTrue;
        getOperand().branchLabel = branchLabel;
        getOperand().codeGenInstGb(compiler);
    }

    @Override
    protected void codeGenOpUnary(DecacCompiler compiler, GPRegister gpReg) {
        // Not Used
    }

    @Override
    protected void codeGenOpUnaryGb(DecacCompiler compiler, GPRegister gpReg) {

    }

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
