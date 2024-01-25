package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        CondManager cM = compiler.getCondManager();

        cM.doOpCmp();
        super.codeGenInst(compiler);
        cM.exitOpCmp();
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        CondManager cM = compiler.getCondManager();

        cM.doOpCmp();
        super.codeGenInstGb(compiler);
        cM.exitOpCmp();
    }

    @Override
    protected void codeGenOp(DecacCompiler compiler,
                             DVal valReg, GPRegister saveReg) {
        CondManager cM = compiler.getCondManager();

        boolean isLeftZero = (getLeftOperand() instanceof AbstractLiteral) &&
                ((AbstractLiteral) getLeftOperand()).isZero();
        boolean isRightZero = (getRightOperand() instanceof AbstractLiteral) &&
                ((AbstractLiteral) getRightOperand()).isZero();
        boolean areBothLiteral = (getLeftOperand() instanceof AbstractLiteral) &&
                (getRightOperand() instanceof AbstractLiteral);
        if (!(isLeftZero || isRightZero) || areBothLiteral) {
            compiler.addInstruction(new CMP(valReg, saveReg));
        }
        if (cM.isDoingCond() && branchLabel != null) {
            if (isInTrue) compiler.addInstruction(getBranchOpCmpInst(branchLabel));
            else compiler.addInstruction(getBranchInvOpCmpInst(branchLabel));
        } else {
            if (isInTrue) compiler.addInstruction(getOpCmpInst(saveReg));
            else compiler.addInstruction(getInvOpCmpInst(saveReg));
        }
    }

    @Override
    protected void codeGenOpGb(DecacCompiler compiler,
                               DVal valReg, GPRegister saveReg) {
        CondManager cM = compiler.getCondManager();

        if (doAdd()) {
            if (!(valReg instanceof GPRegister)) {
                if (valReg instanceof ImmediateInteger) {
                    ImmediateInteger valRegImmInt = (ImmediateInteger) valReg;
                    valRegImmInt.addValue(1);
                } else if (valReg instanceof ImmediateFloat) {
                    ImmediateFloat valRegImmFloat = (ImmediateFloat) valReg;
                    valRegImmFloat.addValue(1);
                }
            } else {
                GPRegister gpReg = (GPRegister) valReg;
                compiler.addInstruction(new LOAD_REG(gpReg.getLowReg(), Register.A));
                compiler.addInstruction(new ADD_A(1, Register.A));
                compiler.addInstruction(new LOAD_REG(Register.A, gpReg.getLowReg()));
            }
        }

        if (!(getRightOperand() instanceof NullLiteral)) {
            compiler.addInstruction(new LOAD_REG(saveReg.getLowReg(), Register.A));
            compiler.addInstruction(new CMP_A(valReg, Register.A));
            if (cM.isDoingCond() && branchLabel != null) {
                if (isInTrue) compiler.addInstruction(getBranchOpCmpInst(branchLabel));
                else compiler.addInstruction(getBranchInvOpCmpInst(branchLabel));
            } else {
                long id = cM.getUniqueId();
                Label trueLabel = new Label("SccTrue" + id);
                Label falseLabel = new Label("SccFalse" + id);
                Label endLabel = new Label("SccEnd" + id);
                if (isInTrue) compiler.addInstruction(getBranchOpCmpInst(trueLabel));
                else compiler.addInstruction(getBranchInvOpCmpInst(trueLabel));

                compiler.addLabel(falseLabel);
                compiler.addInstruction(new LOAD_INT(0, saveReg.getLowReg()));
                compiler.addInstruction(new BRA(endLabel));
                compiler.addLabel(trueLabel);
                compiler.addInstruction(new LOAD_INT(1, saveReg.getLowReg()));
                compiler.addLabel(endLabel);
            }
        } else {
            if (this instanceof Equals) {
                Equals eq = (Equals) this;
                eq.codeGenCmpNullGb(compiler, saveReg);
            } else if (this instanceof NotEquals) {
                NotEquals nEq = (NotEquals) this;
                nEq.codeGenCmpNullGb(compiler, saveReg);
            }
        }
    }

    protected boolean doAdd() {
        return false;
    }

    protected abstract Instruction getBranchInvOpCmpInst(Label bLabel);

    protected abstract Instruction getBranchOpCmpInst(Label bLabel);

    protected abstract Instruction getOpCmpInst(GPRegister gpReg);

    protected abstract Instruction getInvOpCmpInst(GPRegister gpReg);
}
