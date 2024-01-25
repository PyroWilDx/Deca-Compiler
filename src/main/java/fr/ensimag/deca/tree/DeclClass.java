package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 *
 * @author gl47
 * @date 01/01/2024
 */
public class DeclClass extends AbstractDeclClass {

    private final AbstractIdentifier name;
    private final AbstractIdentifier superClass;
    private final ListDeclField fields;
    private final ListDeclMethod methods;

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superClass, ListDeclField fields, ListDeclMethod methods) {
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    public ListDeclField getFields() {
        return fields;
    }

    public AbstractIdentifier getName() {
        return name;
    }

    public AbstractIdentifier getSuperClass() {
        return superClass;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        if (this.name.getName().getName().equals("Object")) {
            return;
        }
        s.print("class ");
        name.decompile(s);
        s.print(" extends " + superClass.getName().getName());
        s.println(" {");
        s.indent();
        fields.decompile(s);
        methods.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        TypeDefinition defSuperClass = compiler.environmentType.get(this.superClass.getName());
        if (defSuperClass == null) {
            throw new ContextualError("Undeclared super class identifier : '" +
                    this.superClass.getName() + "'.",
                    superClass.getLocation());
        }
        if (!defSuperClass.isClass()) {
            throw new ContextualError("A class identifier is required.",
                    superClass.getLocation());
        }
        ClassDefinition superClassDef = (ClassDefinition) compiler.environmentType.get(this.superClass.getName());
        if (!compiler.environmentType.declareClasse(this.name,
                superClassDef, this.getLocation())) {
            throw new ContextualError("Class or type already exists.", this.name.getLocation());
        }
        // Done
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        EnvironmentExp envExpF = this.fields.verifyListDeclFieldMembers(compiler, this.superClass.getName(), name.getName());
        EnvironmentExp envExpM = this.methods.verifyListDeclMethodMembers(compiler, this.superClass.getName());
        int lastIndex = this.methods.getLastIndex();
        SymbolTable.Symbol clone = envExpM.disjointUnion(envExpF);
        if (clone != null) {
            throw new ContextualError("'" + clone.getName() +
                    "' is a field and a method at once.", getLocation());
        }
        ClassDefinition classDef = (ClassDefinition) compiler.environmentType.get(name.getName());
        EnvironmentExp voidClassEnv = classDef.getMembers();
        classDef.setNumberOfMethods(lastIndex);
        voidClassEnv.putAll(envExpM); // c'est vide donc pas de pb pour l'union disjointe !!
        // Done
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        ClassDefinition classDef = this.name.getClassDefinition();
        EnvironmentExp env = classDef.getMembers();
        this.fields.verifyListDeclFieldBody(compiler, env, classDef);
        this.methods.verifyListDeclMethodBody(compiler, env, classDef);
        // Done
    }

    @Override
    public void codeGenVTable(DecacCompiler compiler) {
        StackManager sM = compiler.getStackManager();
        VTableManager vTM = compiler.getVTableManager();

        SymbolTable.Symbol classSymbol = name.getName();
        String className = classSymbol.getName();
        SymbolTable.Symbol superClassSymbol = superClass.getName();
        String superClassName = superClassSymbol.getName();

        compiler.addComment("VTable of " + className);

        DAddr startAddr = sM.getOffsetAddr();
        VTable vT = new VTable(superClassSymbol, classSymbol, startAddr);
        vTM.addVTable(className, vT);

        compiler.addInstruction(
                new LEA(vTM.getClassAddr(superClassName), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, startAddr));
        sM.incrVTableCpt();

        methods.codeGenVTable(compiler, vT);
        fields.codeGenVTable(compiler, vT);
        // Done
    }

    @Override
    public void codeGenVTableGb(DecacCompiler compiler) {
        VTableManager vTM = compiler.getVTableManager();

        SymbolTable.Symbol classSymbol = name.getName();
        String className = classSymbol.getName();
        SymbolTable.Symbol superClassSymbol = superClass.getName();

        VTable vT = new VTable(superClassSymbol, classSymbol, null);
        vTM.addVTable(className, vT);

        methods.codeGenVTableGb(compiler, vT);
        fields.codeGenVTableGb(compiler, vT);
    }

    @Override
    public void codeGenDeclClass(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();
        StackManager sM = new StackManager(true);
        compiler.setStackManager(sM);
        VTableManager vTM = compiler.getVTableManager();

        String className = name.getName().getName();
        vTM.enterClass(className);

        String superClassName = superClass.getName().getName();

        compiler.addComment("Class " + className);

        compiler.addLabel(LabelUtils.getClassInitLabel(className));
        int iTSTO = compiler.getProgramLineCount();

        compiler.addInstruction(
                new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
        fields.codeGenSetFieldsTo0(compiler);
        if (!superClassName.equals(LabelUtils.OBJECT_CLASS_NAME)) {
            compiler.addInstruction(new PUSH(Register.R1));
            compiler.addInstruction(new BSR(LabelUtils.getClassInitLabel(superClassName)));
            compiler.addInstruction(new SUBSP(1));
        }

        rM.saveUsedRegs();
        rM.freeAllRegs();

        fields.codeGenListDeclField(compiler);

        boolean[] usedRegs = rM.popUsedRegs();
        RegManager.addSaveRegsInsts(compiler, iTSTO, usedRegs);
        RegManager.addRestoreRegsInsts(compiler, usedRegs);

        compiler.addInstruction(new RTS());

        methods.codeGenListDeclMethod(compiler);

        vTM.exitClass();
        // Done
    }

    @Override
    public void codeGenDeclClassGb(DecacCompiler compiler) throws ContextualError {
        RegManager rM = compiler.getRegManager();
        StackManager sM = new StackManager(true);
        compiler.setStackManager(sM);
        VTableManager vTM = compiler.getVTableManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        String className = name.getName().getName();
        vTM.enterClass(className);

        String superClassName = superClass.getName().getName();

        compiler.addComment("Class " + className);

        compiler.addLabel(LabelUtils.getClassInitLabel(className));
        int iTSTO = compiler.getProgramLineCount();

        fields.codeGenSetFieldsTo0Gb(compiler);
        if (!superClassName.equals(LabelUtils.OBJECT_CLASS_NAME)) {
            int spOffset = gbM.getCurrMethodSpOffset();
            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 3 + spOffset));
            compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A));
            compiler.addInstruction(new DEC_REG(Register.HL));
            compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));
            compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));
            compiler.addInstruction(new PUSH(Register.HL));

            compiler.addInstruction(new BSR(LabelUtils.getClassInitLabel(superClassName)));
            compiler.addInstruction(new INC_SP(Register.SP));
            compiler.addInstruction(new INC_SP(Register.SP));
        }

        rM.saveUsedRegs();
        rM.freeAllRegs();

        fields.codeGenListDeclFieldGb(compiler);

        boolean[] usedRegs = rM.popUsedRegs();
        RegManager.addSaveRegsInstsGb(compiler, iTSTO, usedRegs);
        RegManager.addRestoreRegsInstsGb(compiler, usedRegs);

        compiler.addInstruction(new RTS());

        methods.codeGenListDeclMethodGb(compiler);

        vTM.exitClass();
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        superClass.prettyPrint(s, prefix, false);
        fields.prettyPrint(s, prefix, false);
        methods.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        name.iterChildren(f);
        fields.iterChildren(f);
        methods.iterChildren(f);
        // Done
    }

}
