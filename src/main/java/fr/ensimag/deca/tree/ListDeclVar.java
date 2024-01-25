package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.StackManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl47
 * @date 01/01/2024
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    public void codeGenListDeclVar(DecacCompiler compiler) {
        StackManager sM = compiler.getStackManager();

        sM.doDeclarations();
        for (AbstractDeclVar declVar : getList()) {
            declVar.codeGenDeclVar(compiler);
        }
        sM.finishDoingDeclrations();
    }

    public void codeGenListDeclVarGb(DecacCompiler compiler) {
        StackManager sM = compiler.getStackManager();

        sM.doDeclarations();
        for (AbstractDeclVar declVar : getList()) {
            declVar.codeGenDeclVarGb(compiler);
        }
        sM.finishDoingDeclrations();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar declVar : getList()) {
            declVar.decompile(s);
            s.println();
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler     contains the "env_types" attribute
     * @param localEnv     its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *                     in precondition, its "current" dictionary corresponds to
     *                     the "env_exp" attribute
     *                     in postcondition, its "current" dictionary corresponds to
     *                     the "env_exp_r" attribute
     * @param currentClass corresponds to "class" attribute (null in the main bloc).
     */
    EnvironmentExp verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp envExpSup, EnvironmentExp localEnv,
                                          ClassDefinition currentClass) throws ContextualError {
        EnvironmentExp envReturn = localEnv.copy();
        for (AbstractDeclVar declVar : this.getList()) {
            declVar.verifyDeclVar(compiler, envExpSup, envReturn, currentClass);
        }
        return envReturn;
        // Done
    }

}
