package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GameBoyManager;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.deca.codegen.VTable;
import fr.ensimag.deca.codegen.VTableManager;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class DeclField extends AbstractDeclField {

    private final Visibility visibility;
    private final AbstractIdentifier type;
    private final AbstractIdentifier name;
    private final AbstractInitialization init;

    public DeclField(Visibility visibility, AbstractIdentifier type,
                     AbstractIdentifier name) {
        this.type = type;
        this.visibility = visibility;
        this.name = name;
        this.init = new NoInitialization();
    }

    public DeclField(Visibility visibility, AbstractIdentifier type,
                     AbstractIdentifier name, AbstractInitialization init) {
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public void codeGenVTable(DecacCompiler compiler, VTable vTable, int fieldOffset) {
        // Si y a déjà une variable du même nom, on la remplace dans le HashMap
        // Comme l'ancienne ne sera pas utilisée (car redéfinie), il ne devrait pas y avoir de problème
        vTable.addField(getName().getName(), fieldOffset);
    }

    @Override
    public void codeGenVTableGb(DecacCompiler compiler, VTable vTable, int fieldOffset) {
        vTable.addField(getName().getName(), fieldOffset);
    }

    @Override
    public void codeGenSetFieldTo0(DecacCompiler compiler, boolean doLoad) {
        VTableManager vTM = compiler.getVTableManager();

        if (doLoad) {
            if (getInitTypeCode() == TypeCode.INT_OR_BOOL) {
                compiler.addInstruction(new LOAD(0, Register.R0));
            } else if (getInitTypeCode() == TypeCode.FLOAT) {
                compiler.addInstruction(new LOAD(0.f, Register.R0));
            } else {
                compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
            }
        }

        int fieldOffset = vTM.getCurrFieldOffset(getName().getName());
        compiler.addInstruction(
                new STORE(Register.R0, new RegisterOffset(fieldOffset, Register.R1)));
    }

    @Override
    public void codeGenSetFieldTo0Gb(DecacCompiler compiler) {
        VTableManager vTM = compiler.getVTableManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        int fieldOffset = vTM.getCurrFieldOffset(getName().getName());

        int spOffset = gbM.getCurrMethodSpOffset(); // Devrait toujours être 0 mais on sait jamais
        compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 3 + spOffset));
        compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));
        compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));

        compiler.addInstruction(new LOAD_INT(0, Register.A));

        GameBoyManager.incHLByValue(compiler, fieldOffset * 2 + 1);

        compiler.addInstruction(new STORE_REG(Register.A, Register.HL));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new STORE_REG(Register.A, Register.HL));
    }

    @Override
    public void codeGenDeclField(DecacCompiler compiler) {
        if (init instanceof NoInitialization) return;

        RegManager rM = compiler.getRegManager();
        VTableManager vTM = compiler.getVTableManager();

//        TypeCode returnValue = null;

//        init.setVarTypeCode(getInitTypeCode());
        init.codeGenInit(compiler);

        GPRegister regValue;
        DVal lastImm = rM.getLastImm();
        if (lastImm == null) {
            regValue = rM.getLastReg();
        } else {
            regValue = Register.R0;
            // Utile dans le cas où on fait pas l'init à 0 mais on le fait là
//            if (init instanceof NoInitialization) {
//                returnValue = getInitTypeCode();
//                if (lastTypeCode == null || lastTypeCode != getInitTypeCode()) {
//                    compiler.addInstruction(new LOAD(lastImm, regValue));
//                }
//            } else {
            compiler.addInstruction(new LOAD(lastImm, regValue));
//            }
        }

        int fieldOffset = vTM.getCurrFieldOffset(getName().getName());
        compiler.addInstruction(
                new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
        compiler.addInstruction(
                new STORE(regValue, new RegisterOffset(fieldOffset, Register.R1)));
        if (regValue != Register.R0) {
            rM.freeReg(regValue);
        }

//        return returnValue;
        // Done
    }

    @Override
    public void codeGenDeclFieldGb(DecacCompiler compiler) {
        if (init instanceof NoInitialization) return;

        RegManager rM = compiler.getRegManager();
        VTableManager vTM = compiler.getVTableManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        int fieldOffset = vTM.getCurrFieldOffset(getName().getName());

        if (((Initialization) init).getExpression() instanceof New) {
            compiler.addInstruction(new SUBSP(4));
        }

        init.codeGenInitGb(compiler);

        if (((Initialization) init).getExpression() instanceof New) {
            compiler.addInstruction(new ADDSP(4));
        }

        GPRegister regValue = rM.getLastRegOrImm(compiler);

        int spOffset = gbM.getCurrMethodSpOffset();
        compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 3 + spOffset));
        compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));
        compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));

//        for (int i = 0; i < fieldOffset * 2; i++) {
//            compiler.addInstruction(new INC_REG(Register.HL));
//        }
//        compiler.addInstruction(new INC_REG(Register.HL));
        GameBoyManager.incHLByValue(compiler, fieldOffset * 2 + 1);

        compiler.addInstruction(new STORE_REG(regValue.getHighReg(), Register.HL));
        compiler.addInstruction(new DEC_REG(Register.HL));
        compiler.addInstruction(new STORE_REG(regValue.getLowReg(), Register.HL));

        rM.freeReg(regValue);
    }

    @Override
    public TypeCode getInitTypeCode() {
        if (type.getType().isInt() || type.getType().isBoolean()) {
            return TypeCode.INT_OR_BOOL;
        } else if (type.getType().isFloat()) {
            return TypeCode.FLOAT;
        }
        return TypeCode.OBJECT;
    }

    @Override
    public EnvironmentExp verifyDeclFieldMembers(DecacCompiler compiler,
                                                 SymbolTable.Symbol superClass,
                                                 SymbolTable.Symbol className, int index) throws ContextualError {
        Type t = this.type.verifyType(compiler);
        if (t.equals(compiler.environmentType.VOID)) {
            throw new ContextualError("Field type cannot be void.",
                    getLocation());
        }
        TypeDefinition def = compiler.environmentType.get(superClass);
        if (def.isClass()) {
            ClassDefinition superClassDef = (ClassDefinition) def;
            EnvironmentExp envExpSuper = superClassDef.getMembers();
            ExpDefinition expDef = envExpSuper.get(this.name.getName());
            if (expDef != null && !expDef.isField()) {
                throw new ContextualError("A method '" + this.name.getName() +
                        "' already exists in super class.", getLocation());
            }
        }
        EnvironmentExp env = new EnvironmentExp(null);
        ClassDefinition classDefinition = (ClassDefinition) compiler.environmentType.get(className);
        ExpDefinition expDef = new FieldDefinition(t, getLocation(), visibility, classDefinition, index);
        try {
            env.declare(this.name.getName(), expDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new DecacInternalError("Symbol cannot have been declared twice.");
        }
        return env;
        // Done
    }

    @Override
    public void verifyDeclFieldBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition classDef) throws ContextualError {
        Type t = this.type.verifyType(compiler);
        this.init.verifyInitialization(compiler, t, localEnv, classDef);
        // Done
    }

    @Override
    public SymbolTable.Symbol getName() {
        return name.getName();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.name.decompile(s);
        this.init.decompile(s);
        s.println(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        System.out.println(prefix + visibility);
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        init.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
        name.iterChildren(f);
        init.iterChildren(f);
    }
}
