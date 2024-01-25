package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class CodeGenUtils {

    private CodeGenUtils() {

    }

    public static DAddr extractAddrFromIdent(DecacCompiler compiler, AbstractIdentifier ident) {
        RegManager rM = compiler.getRegManager();
        VTableManager vTM = compiler.getVTableManager();

        DAddr iAddr = ident.getExpDefinition().getOperand();
        if (iAddr == null) { // It's a Method Param or Class Field
            String identName = ident.getName().getName();
            Integer paramOffset = vTM.getCurrParamOffsetOfMethod(identName);
            if (paramOffset != null) { // It's a Method Param
                iAddr = new RegisterOffset(paramOffset, Register.LB);
            } else { // It's a Class Field
                GPRegister gpReg = rM.getFreeReg();
                compiler.addInstruction(
                        new LOAD(new RegisterOffset(-2, Register.LB), gpReg));
                // Pas besoin vu qu'on est déjà dans une instance de la classe
//                compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
//                compiler.addInstruction(new BEQ(eM.getNullPointerLabel()));
                Integer fieldOffset = vTM.getCurrFieldOffset(identName);
                iAddr = new RegisterOffset(fieldOffset, gpReg);
                rM.freeReg(gpReg);
            }
        }

        return iAddr;
    }

}
