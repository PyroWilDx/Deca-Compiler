package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SHL;
import fr.ensimag.ima.pseudocode.instructions.SHR;

/**
 * Arithmetic binary operations (+, -, /, ...)
 *
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    public FloatLiteral isFloatLiteral() {
        if ((getLeftOperand() instanceof FloatLiteral) &&
                (getRightOperand() instanceof FloatLiteral)) {
            FloatLiteral fLL = (FloatLiteral) getLeftOperand();
            FloatLiteral fLR = (FloatLiteral) getRightOperand();
            return new FloatLiteral(doOpFloat(fLL.getValue(), fLR.getValue()));
        }
        return null;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        ErrorManager eM = compiler.getErrorManager();

        if ((getLeftOperand() instanceof IntLiteral) &&
                (getRightOperand() instanceof IntLiteral)) {
            IntLiteral iLL = (IntLiteral) getLeftOperand();
            IntLiteral iLR = (IntLiteral) getRightOperand();
            if ((this instanceof Divide) || (this instanceof Modulo)) {
                if (iLR.getValue() == 0) {
                    if (compiler.getCompilerOptions().doCheck()) {
                        compiler.addInstruction(new BRA(eM.getDivBy0Label()));
                    } else {
                        super.codeGenInst(compiler);
                    }
                    return;
                }
            }
            int value = doOpInt(iLL.getValue(), iLR.getValue());
            GPRegister gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD(value, gpReg));
            rM.freeReg(gpReg);

        } else if ((getLeftOperand() instanceof ConvFloat) &&
                (getRightOperand() instanceof FloatLiteral)) {
            IntLiteral iLL;
            AbstractExpr lExp = ((ConvFloat) getLeftOperand()).getOperand();
            if (lExp instanceof IntLiteral) {
                iLL = (IntLiteral) lExp;
            } else {
                super.codeGenInst(compiler);
                return;
            }
            FloatLiteral fLR = (FloatLiteral) getRightOperand();
            float value = doOpFloat((float) iLL.getValue(), fLR.getValue());
            if (Float.isInfinite(value)) {
                if (compiler.getCompilerOptions().doCheck()) {
                    compiler.addInstruction(new BRA(eM.getFloatOverflowLabel()));
                } else {
                    super.codeGenInst(compiler);
                }
                return;
            }
            GPRegister gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD(value, gpReg));
            rM.freeReg(gpReg);

        } else if ((getLeftOperand() instanceof FloatLiteral) &&
                (getRightOperand() instanceof ConvFloat)) {
            FloatLiteral fLL = (FloatLiteral) getLeftOperand();
            IntLiteral iLR;
            AbstractExpr rExp = ((ConvFloat) getRightOperand()).getOperand();
            if (rExp instanceof IntLiteral) {
                iLR = (IntLiteral) rExp;
            } else {
                super.codeGenInst(compiler);
                return;
            }
            float value = doOpFloat(fLL.getValue(), (float) iLR.getValue());
            if (Float.isInfinite(value)) {
                if (compiler.getCompilerOptions().doCheck()) {
                    compiler.addInstruction(new BRA(eM.getFloatOverflowLabel()));
                } else {
                    super.codeGenInst(compiler);
                }
                return;
            }
            GPRegister gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD(value, gpReg));
            rM.freeReg(gpReg);

        } else if ((getLeftOperand() instanceof FloatLiteral) &&
                getRightOperand() instanceof FloatLiteral) {
            FloatLiteral fLL = (FloatLiteral) getLeftOperand();
            FloatLiteral fLR = (FloatLiteral) getRightOperand();
            float value = doOpFloat(fLL.getValue(), fLR.getValue());
            if (Float.isInfinite(value)) {
                if (compiler.getCompilerOptions().doCheck()) {
                    compiler.addInstruction(new BRA(eM.getFloatOverflowLabel()));
                } else {
                    super.codeGenInst(compiler);
                }
                return;
            }
            GPRegister gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD(value, gpReg));
            rM.freeReg(gpReg);

        } else {
            if ((this instanceof Multiply) || (this instanceof Divide)) {
                if ((getLeftOperand() instanceof IntLiteral) &&
                        ((IntLiteral) getLeftOperand()).isPowerOf2()) {
                    if (this instanceof Divide) {
                        super.codeGenInst(compiler);
                        return;
                    }
                    IntLiteral iLL = (IntLiteral) getLeftOperand();
                    int twoExp = iLL.getExponentOf2();
                    if (twoExp >= 10) {
                        super.codeGenInst(compiler);
                        return;
                    }
                    getRightOperand().codeGenInst(compiler);
                    GPRegister gpReg = rM.getLastRegOrImm(compiler);
                    if (gpReg == Register.R0) {
                        gpReg = rM.getFreeReg();
                        compiler.addInstruction(new LOAD(Register.R0, gpReg));
                    }
                    for (int i = 0; i < twoExp; i++) {
                        compiler.addInstruction(new SHL(gpReg));
                    }
                    rM.freeReg(gpReg);

                } else if ((getRightOperand() instanceof IntLiteral) &&
                        ((IntLiteral) getRightOperand()).isPowerOf2()) {
                    IntLiteral iLL = (IntLiteral) getRightOperand();
                    int twoExp = iLL.getExponentOf2();
                    if (twoExp >= 10) {
                        super.codeGenInst(compiler);
                        return;
                    }
                    getLeftOperand().codeGenInst(compiler);
                    GPRegister gpReg = rM.getLastRegOrImm(compiler);
                    if (gpReg == Register.R0) {
                        gpReg = rM.getFreeReg();
                        compiler.addInstruction(new LOAD(Register.R0, gpReg));
                    }
                    for (int i = 0; i < twoExp; i++) {
                        if (this instanceof Multiply) {
                            compiler.addInstruction(new SHL(gpReg));
                        } else { // Divide
                            compiler.addInstruction(new SHR(gpReg));
                        }
                    }
                    rM.freeReg(gpReg);

                } else {
                    super.codeGenInst(compiler);
                }
            } else {
                super.codeGenInst(compiler);
            }
        }
    }

    protected abstract int doOpInt(int a, int b);

    protected abstract float doOpFloat(float a, float b);

    @Override
    protected void codeGenOp(DecacCompiler compiler,
                             DVal valReg, GPRegister saveReg) {
        codeGenOpArith(compiler, valReg, saveReg);
    }

    @Override
    protected void codeGenOpGb(DecacCompiler compiler,
                               DVal valReg, GPRegister saveReg) {
        codeGenOpArithGb(compiler, valReg, saveReg);
    }

    protected abstract void codeGenOpArith(DecacCompiler compiler,
                                           DVal valReg, GPRegister saveReg);

    protected abstract void codeGenOpArithGb(DecacCompiler compiler,
                                             DVal valReg, GPRegister saveReg);

}
