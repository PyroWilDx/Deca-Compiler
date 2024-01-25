package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.util.Iterator;

public class ListDeclParam extends TreeList<AbstractParam> {

    @Override
    public void decompile(IndentPrintStream s) {
        Iterator<AbstractParam> iter = this.iterator();
        while (iter.hasNext()) {
            iter.next().decompile(s);
            if (iter.hasNext()) {
                s.print(", ");
            }
        }
    }

    public Signature verifyListDeclParamMembers(DecacCompiler compiler) throws ContextualError {
        Signature sig = new Signature();
        for (AbstractParam param : this.getList()) {
            Type type = param.verifyDeclParamMembers(compiler);
            sig.add(type);
        }
        return sig;
        // Done
    }
    public EnvironmentExp verifyListDeclParamBody(DecacCompiler compiler) throws ContextualError {
        EnvironmentExp env = new EnvironmentExp(null);
        for (AbstractParam param : getList()) {
            EnvironmentExp envParam = param.verifyDeclParamBody(compiler);
            if (env.disjointUnion(envParam) != null) {
                throw new ContextualError("Parameter '" +
                        param.getName() +
                        "' already exists.", getLocation());
            }
        }
        return env;
        // Done
    }
}
