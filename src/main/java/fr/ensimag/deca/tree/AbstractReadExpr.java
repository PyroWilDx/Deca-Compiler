package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * read...() statement.
 *
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractReadExpr extends AbstractExpr {

    public AbstractReadExpr() {
        super();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        ErrorManager eM = compiler.getErrorManager();

        codeGenOpRead(compiler);
        compiler.addInstruction(new BOV(eM.getIoErrLabel()));

        GPRegister gpReg = rM.getFreeReg();
        compiler.addInstruction(new LOAD(Register.R1, gpReg));
        rM.freeReg(gpReg);
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        // Pas de read
    }

    protected abstract void codeGenOpRead(DecacCompiler compiler);
}
