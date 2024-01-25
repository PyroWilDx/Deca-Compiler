package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass decl : this.getList()) {
            decl.verifyClass(compiler);
        }
        // Done
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass members: start");
        for (AbstractDeclClass decl : this.getList()) {
            decl.verifyClassMembers(compiler);
        }
        LOG.debug("verify listClass members: end");
        // Done
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass decl : getList()) {
            decl.verifyClassBody(compiler);
        }
        // Done
    }

    public void codeGenVTable(DecacCompiler compiler) {
        for (AbstractDeclClass declClass : getList()) {
            declClass.codeGenVTable(compiler);
        }
    }

    public void codeGenVTableGb(DecacCompiler compiler) {
        for (AbstractDeclClass declClass : getList()) {
            declClass.codeGenVTableGb(compiler);
        }
    }

    public void codeGenListDeclClass(DecacCompiler compiler) {
        for (AbstractDeclClass declClass: getList()) {
            declClass.codeGenDeclClass(compiler);
        }
    }

    public void codeGenListDeclClassGb(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass declClass: getList()) {
            declClass.codeGenDeclClassGb(compiler);
        }
    }

}
