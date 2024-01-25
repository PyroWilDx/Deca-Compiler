package fr.ensimag.deca.tree;


import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.SGE;
import fr.ensimag.ima.pseudocode.instructions.SLT;

/**
 * Operator "x >= y"
 *
 * @author gl47
 * @date 01/01/2024
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected Instruction getBranchInvOpCmpInst(Label bLabel) {
        return new BLT(bLabel);
    }

    @Override
    protected Instruction getBranchOpCmpInst(Label bLabel) {
        return new BGE(bLabel);
    }

    @Override
    protected Instruction getOpCmpInst(GPRegister gpReg) {
        return new SGE(gpReg);
    }

    @Override
    protected Instruction getInvOpCmpInst(GPRegister gpReg) {
        return new SLT(gpReg);
    }

    @Override
    protected String getOperatorName() {
        return ">=";
    }

}
