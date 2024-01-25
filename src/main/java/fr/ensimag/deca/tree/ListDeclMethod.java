package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.util.ArrayList;
import java.util.LinkedList;

public class ListDeclMethod extends TreeList<AbstractDeclMethod> {
    private int lastIndex;

    public int getLastIndex() {
        return lastIndex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod decl : getList()) {
            decl.decompile(s);
        }
    }

    public EnvironmentExp verifyListDeclMethodMembers(DecacCompiler compiler,
                                                      SymbolTable.Symbol superClass) throws ContextualError {
        EnvironmentExp envReturn = new EnvironmentExp(null);
        ClassDefinition superClassDef = compiler.environmentType.get(superClass).asClassDefinition("Impossible d'arriver ici", getLocation());
        int index = superClassDef.getNumberOfMethods() + 1;
        for (AbstractDeclMethod decl : this.getList()) {
            EnvironmentExp env = decl.verifyDeclMethodMembers(compiler, superClass, index);
            if (!decl.isOverride()) index++;
            if (envReturn.disjointUnion(env) != null) {
                throw new ContextualError("Method '" + decl.getName()
                        + "' already defined.", decl.getLocation());
            }
        }
        this.lastIndex = index;
        return envReturn;
    }

    public void verifyListDeclMethodBody(DecacCompiler compiler,
                                         EnvironmentExp localEnv,
                                         ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclMethod decl : getList()) {
            decl.verifyDeclMethodBody(compiler, localEnv, currentClass);
        }
    }

    public LinkedList<AbstractDeclMethod> getListOrdered() {
        ArrayList<AbstractDeclMethod> list = new ArrayList<>(getList());
        LinkedList<AbstractDeclMethod> ordered = new LinkedList<>();

        if (list.isEmpty()) return ordered;

        // Bon, un tri en O(n²) devrait pas trop poser de problème, à part si la classe contient des milliers de méthodes
        for (int i = 0; i < list.size(); i++) {
            int minIndex = -1;
            int minVal = Integer.MAX_VALUE;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j) != null &&
                        list.get(j).getMethodIndex() < minVal) {
                    minVal = list.get(j).getMethodIndex();
                    minIndex = j;
                }
            }
            ordered.add(list.get(minIndex));
            list.set(minIndex, null);
        }

        return ordered;
    }

    public void codeGenVTable(DecacCompiler compiler, VTable vTable) {
        StackManager sM = compiler.getStackManager();
        VTableManager vTM = compiler.getVTableManager();

        LinkedList<AbstractDeclMethod> orderedMethods = getListOrdered();

        VTable superClassVTable = vTable.getVTableOfSuperClass(vTM);
        LinkedList<VMethodInfo> superClassMethods = superClassVTable.getClassMethods();

        int methodOffset = 1;

        for (VMethodInfo methodInfo : superClassMethods) {
            boolean isPresentInCurrClass = false;
            AbstractDeclMethod redefinedMethod = null;
            for (AbstractDeclMethod declMethod : orderedMethods) {
                if (methodInfo.getMethodName().equals(declMethod.getName().getName())) {
                    isPresentInCurrClass = true;
                    redefinedMethod = declMethod;
                    break;
                }
            }
            if (!isPresentInCurrClass) {
                compiler.addInstruction(new LOAD(new LabelOperand(
                        LabelUtils.getMethodLabel(methodInfo.getClassName(),
                                methodInfo.getMethodName())),
                        Register.R0));
                DAddr mAddr = sM.getOffsetAddr();
                compiler.addInstruction(new STORE(Register.R0, mAddr));
                sM.incrVTableCpt();

                vTable.addSuperMethod(methodInfo.getClassName(),
                        methodInfo.getMethodName(), methodOffset);
                vTable.copyMethodParams(superClassVTable, methodInfo.getMethodName());
            } else {
                redefinedMethod.codeGenVTable(compiler, vTable, methodOffset);
                orderedMethods.remove(redefinedMethod);
            }
            methodOffset++;
        }

        for (AbstractDeclMethod declMethod : orderedMethods) {
            declMethod.codeGenVTable(compiler, vTable, methodOffset);
            methodOffset++;
        }
        // Done
    }

    public void codeGenVTableGb(DecacCompiler compiler, VTable vTable) {
        VTableManager vTM = compiler.getVTableManager();

        LinkedList<AbstractDeclMethod> orderedMethods = getListOrdered();

        VTable superClassVTable = vTable.getVTableOfSuperClass(vTM);
        LinkedList<VMethodInfo> superClassMethods = superClassVTable.getClassMethods();

        int methodOffset = 1;

        for (VMethodInfo methodInfo : superClassMethods) {
            boolean isPresentInCurrClass = false;
            AbstractDeclMethod redefinedMethod = null;
            for (AbstractDeclMethod declMethod : orderedMethods) {
                if (methodInfo.getMethodName().equals(declMethod.getName().getName())) {
                    isPresentInCurrClass = true;
                    redefinedMethod = declMethod;
                    break;
                }
            }
            if (!isPresentInCurrClass) {
                vTable.addSuperMethod(methodInfo.getClassName(),
                        methodInfo.getMethodName(), methodOffset);
                vTable.copyMethodParams(superClassVTable, methodInfo.getMethodName());
            } else {
                redefinedMethod.codeGenVTableGb(compiler, vTable, methodOffset);
                orderedMethods.remove(redefinedMethod);
            }
            methodOffset++;
        }

        for (AbstractDeclMethod declMethod : orderedMethods) {
            declMethod.codeGenVTableGb(compiler, vTable, methodOffset);
            methodOffset++;
        }
    }

    public void codeGenListDeclMethod(DecacCompiler compiler) {
        for (AbstractDeclMethod method : getListOrdered()) {
            method.codeGenDeclMethod(compiler);
        }
    }

    public void codeGenListDeclMethodGb(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclMethod method : getListOrdered()) {
            method.codeGenDeclMethodGb(compiler);
        }
    }

}
