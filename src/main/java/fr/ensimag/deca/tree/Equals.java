package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public boolean doEq() {
        return true;
    }

    @Override
    protected Instruction getBranchInvOpCmpInst(Label bLabel) {
        return new BNE(bLabel);
    }

    @Override
    protected Instruction getBranchOpCmpInst(Label bLabel) {
        return new BEQ(bLabel);
    }

    @Override
    protected Instruction getOpCmpInst(GPRegister gpReg) {
        return new SEQ(gpReg);
    }

    @Override
    protected Instruction getInvOpCmpInst(GPRegister gpReg) {
        return new SNE(gpReg);
    }

    public void codeGenCmpNullGb(DecacCompiler compiler, GPRegister gpReg) {
        CondManager cM = compiler.getCondManager();

        long id = cM.getUniqueId();
        if (cM.isDoingCond() && branchLabel != null) {
            Label endLabel = new Label("endCmpEqNull" + id);

            compiler.addInstruction(new LOAD_REG(gpReg.getHighRegOfLow(), Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            if (isInTrue) compiler.addInstruction(new BNE(endLabel));
            else compiler.addInstruction(new BNE(branchLabel));

            compiler.addInstruction(new LOAD_REG(gpReg.getLowReg(), Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            if (isInTrue) {
                compiler.addInstruction(new BNE(endLabel));
                compiler.addInstruction(new BRA(branchLabel));
            }
            else compiler.addInstruction(new BNE(branchLabel));

            compiler.addLabel(endLabel);
        } else {
            Label trueLabel = new Label("SccTrue" + id);
            Label falseLabel = new Label("SccFalse" + id);
            Label endLabel = new Label("SccEnd" + id);

            compiler.addInstruction(new LOAD_REG(gpReg.getHighRegOfLow(), Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            if (isInTrue) compiler.addInstruction(new BNE(falseLabel));
            else compiler.addInstruction(new BNE(trueLabel));

            compiler.addInstruction(new LOAD_REG(gpReg.getLowReg(), Register.A));
            compiler.addInstruction(new CMP_A(0, Register.A));
            if (isInTrue) {
                compiler.addInstruction(new BNE(falseLabel));
                compiler.addInstruction(new BRA(trueLabel));
            }
            else compiler.addInstruction(new BNE(trueLabel));

            compiler.addLabel(falseLabel);
            compiler.addInstruction(new LOAD_INT(0, gpReg.getLowReg()));
            compiler.addInstruction(new BRA(endLabel));
            compiler.addLabel(trueLabel);
            compiler.addInstruction(new LOAD_INT(1, gpReg.getLowReg()));
            compiler.addLabel(endLabel);
        }
    }

    @Override
    protected String getOperatorName() {
        return "==";
    }

}
