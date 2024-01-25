package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.LOAD_INT;

/**
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        CondManager cM = compiler.getCondManager();

        cM.doCond();

        boolean firstCond = false;
        if (branchLabel == null) {
            firstCond = true;
            branchLabel = cM.getUniqueLabel();
        }

        Label fastEndLabel = setOperandCondVals(cM);

        getLeftOperand().codeGenInst(compiler);
        getRightOperand().codeGenInst(compiler);

        if (fastEndLabel != null) compiler.addLabel(fastEndLabel);

        if (firstCond){
            Label endLabel = cM.getUniqueLabel();

            GPRegister gpReg = rM.getFreeReg();

            compiler.addInstruction(new LOAD(0, gpReg));
            compiler.addInstruction(new BRA(endLabel));

            compiler.addLabel(branchLabel);
            compiler.addInstruction(new LOAD(1, gpReg));

            rM.freeReg(gpReg);

            compiler.addLabel(endLabel);
        }

        cM.exitCond();
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        CondManager cM = compiler.getCondManager();

        cM.doCond();

        boolean firstCond = false;
        if (branchLabel == null) {
            firstCond = true;
            branchLabel = cM.getUniqueLabel();
        }

        Label fastEndLabel = setOperandCondVals(cM);

        getLeftOperand().codeGenInstGb(compiler);
        getRightOperand().codeGenInstGb(compiler);

        if (fastEndLabel != null) compiler.addLabel(fastEndLabel);

        if (firstCond){
            Label endLabel = cM.getUniqueLabel();

            GPRegister gpReg = rM.getFreeReg();

            compiler.addInstruction(new LOAD_INT(0, gpReg));
            compiler.addInstruction(new BRA(endLabel));

            compiler.addLabel(branchLabel);
            compiler.addInstruction(new LOAD_INT(1, gpReg));

            rM.freeReg(gpReg);

            compiler.addLabel(endLabel);
        }

        cM.exitCond();
    }

    public abstract Label setOperandCondVals(CondManager cM);

    @Override
    protected void codeGenOp(DecacCompiler compiler,
                             DVal valReg, GPRegister saveReg) {
        // Not Used
    }

    @Override
    protected void codeGenOpGb(DecacCompiler compiler,
                               DVal valReg, GPRegister saveReg) {
        // Not Used
    }
}
