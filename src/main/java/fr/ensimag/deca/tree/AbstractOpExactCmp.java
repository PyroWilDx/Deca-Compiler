package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        CondManager cM = compiler.getCondManager();

        if ((getLeftOperand() instanceof BooleanLiteral) &&
                getRightOperand() instanceof BooleanLiteral) {
            BooleanLiteral lOperand = (BooleanLiteral) getLeftOperand();
            BooleanLiteral rOperand = (BooleanLiteral) getRightOperand();
            boolean caseTrue1 = (doEq() && lOperand.getValue() == rOperand.getValue() && isInTrue) ||
                    (!doEq() && lOperand.getValue() != rOperand.getValue() && isInTrue);
            boolean caseTrue2 = (doEq() && lOperand.getValue() != rOperand.getValue() && !isInTrue) ||
                    (!doEq() && lOperand.getValue() == rOperand.getValue() && !isInTrue);
            if (caseTrue1 || caseTrue2) {
                if (cM.isDoingCond()) {
                    compiler.addInstruction(new BRA(branchLabel));
                } else {
                    rM.setLastImm(new ImmediateInteger(1));
                }
            } else { // False
                if (!cM.isDoingCond()) {
                    rM.setLastImm(new ImmediateInteger(0));
                }
            }
        } else if (getLeftOperand() instanceof BooleanLiteral) {
            BooleanLiteral lOperand = (BooleanLiteral) getLeftOperand();
            getRightOperand().isInTrue = isInTrue;
            if ((doEq() && !lOperand.getValue()) ||
                    (!doEq() && lOperand.getValue())) {
                getRightOperand().isInTrue = !getRightOperand().isInTrue;
            }
            getRightOperand().branchLabel = branchLabel;
            getRightOperand().codeGenInst(compiler);
        } else if (getRightOperand() instanceof BooleanLiteral) {
            BooleanLiteral rOperand = (BooleanLiteral) getRightOperand();
            getLeftOperand().isInTrue = isInTrue;
            if ((doEq() && !rOperand.getValue()) ||
                    (!doEq() && rOperand.getValue())) {
                getLeftOperand().isInTrue = !getLeftOperand().isInTrue;
            }
            getLeftOperand().branchLabel = branchLabel;
            getLeftOperand().codeGenInst(compiler);
        } else {
            super.codeGenInst(compiler);
        }
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        CondManager cM = compiler.getCondManager();

        if ((getLeftOperand() instanceof BooleanLiteral) &&
                getRightOperand() instanceof BooleanLiteral) {
            BooleanLiteral lOperand = (BooleanLiteral) getLeftOperand();
            BooleanLiteral rOperand = (BooleanLiteral) getRightOperand();
            boolean caseTrue1 = (doEq() && lOperand.getValue() == rOperand.getValue() && isInTrue) ||
                    (!doEq() && lOperand.getValue() != rOperand.getValue() && isInTrue);
            boolean caseTrue2 = (doEq() && lOperand.getValue() != rOperand.getValue() && !isInTrue) ||
                    (!doEq() && lOperand.getValue() == rOperand.getValue() && !isInTrue);
            if (caseTrue1 || caseTrue2) {
                if (cM.isDoingCond() && branchLabel != null) {
                    compiler.addInstruction(new BRA(branchLabel));
                } else {
                    rM.setLastImm(new ImmediateInteger(1));
                }
            } else { // False
                if (!cM.isDoingCond()) {
                    rM.setLastImm(new ImmediateInteger(0));
                }
            }
        } else if (getLeftOperand() instanceof BooleanLiteral) {
            BooleanLiteral lOperand = (BooleanLiteral) getLeftOperand();
            getRightOperand().isInTrue = isInTrue;
            if ((doEq() && !lOperand.getValue()) ||
                    (!doEq() && lOperand.getValue())) {
                getRightOperand().isInTrue = !getRightOperand().isInTrue;
            }
            getRightOperand().branchLabel = branchLabel;
            getRightOperand().codeGenInstGb(compiler);
        } else if (getRightOperand() instanceof BooleanLiteral) {
            BooleanLiteral rOperand = (BooleanLiteral) getRightOperand();
            getLeftOperand().isInTrue = isInTrue;
            if ((doEq() && !rOperand.getValue()) ||
                    (!doEq() && rOperand.getValue())) {
                getLeftOperand().isInTrue = !getLeftOperand().isInTrue;
            }
            getLeftOperand().branchLabel = branchLabel;
            getLeftOperand().codeGenInstGb(compiler);
        } else {
            super.codeGenInstGb(compiler);
        }
    }

    public abstract boolean doEq();

}