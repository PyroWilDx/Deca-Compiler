package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.VTable;
import fr.ensimag.deca.codegen.VTableManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

public class ListDeclField extends TreeList<AbstractDeclField> {
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField decl : getList()) {
            decl.decompile(s);
        }
    }

    public EnvironmentExp verifyListDeclFieldMembers(DecacCompiler compiler, SymbolTable.Symbol superClass,
                                                     SymbolTable.Symbol name) throws ContextualError {
        EnvironmentExp envReturn = new EnvironmentExp(null);
        int index = 1;
        for (AbstractDeclField decl : this.getList()) {
            EnvironmentExp env = decl.verifyDeclFieldMembers(compiler, superClass, name, index);
            index++;
            if (envReturn.disjointUnion(env) != null) {
                throw new ContextualError("Field '" + decl.getName() +
                        "' already defined.", decl.getLocation());
            }
        }
        return envReturn;
        // Done
    }

    public void verifyListDeclFieldBody(DecacCompiler compiler,
                                        EnvironmentExp localEnv,
                                        ClassDefinition classDef) throws ContextualError {
        for (AbstractDeclField decl : getList()) {
            decl.verifyDeclFieldBody(compiler, localEnv, classDef);
        }
    }

    public void codeGenVTable(DecacCompiler compiler, VTable vTable) {
        VTableManager vTM = compiler.getVTableManager();

        VTable superClassVTable = vTable.getVTableOfSuperClass(vTM);
        vTable.addAllFields(superClassVTable);

        int fieldOffset = superClassVTable.getFieldsCount() + 1;
        for (AbstractDeclField declField : getList()) {
            declField.codeGenVTable(compiler, vTable, fieldOffset);
            fieldOffset++;
        }
    }

    public void codeGenVTableGb(DecacCompiler compiler, VTable vTable) {
        VTableManager vTM = compiler.getVTableManager();

        VTable superClassVTable = vTable.getVTableOfSuperClass(vTM);
        vTable.addAllFields(superClassVTable);

        int fieldOffset = superClassVTable.getFieldsCount();
        for (AbstractDeclField declField : getList()) {
            declField.codeGenVTableGb(compiler, vTable, fieldOffset);
            fieldOffset++;
        }
    }

    public void codeGenSetFieldsTo0(DecacCompiler compiler) {
        AbstractDeclField.TypeCode lastTypeCode = null;
        for (AbstractDeclField declField : getList()) {
            AbstractDeclField.TypeCode currTypeCode = declField.getInitTypeCode();
            declField.codeGenSetFieldTo0(compiler, currTypeCode != lastTypeCode);
            lastTypeCode = currTypeCode;
        }
    }

    public void codeGenSetFieldsTo0Gb(DecacCompiler compiler) {
        for (AbstractDeclField declField : getList()) {
            declField.codeGenSetFieldTo0Gb(compiler);
        }
    }

    public void codeGenListDeclField(DecacCompiler compiler) {
        for (AbstractDeclField declField : getList()) {
            declField.codeGenDeclField(compiler);
        }
    }

    public void codeGenListDeclFieldGb(DecacCompiler compiler) {
        for (AbstractDeclField declField : getList()) {
            declField.codeGenDeclFieldGb(compiler);
        }
    }
}
