package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Location;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl47
 * @date 01/01/2024
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        this.compiler = compiler;
        envTypes = new HashMap<Symbol, TypeDefinition>();
        classesAndNull = new ArrayList<>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // not added to envTypes, it's not visible for the user.

        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);
        Symbol equalsMethodSymbol = compiler.createSymbol("equals");
        Signature equalsSignature = new Signature();
        equalsSignature.add(OBJECT);
        MethodDefinition equalsDef = new MethodDefinition(BOOLEAN, Location.BUILTIN, equalsSignature, 0);
        try {
            OBJECT.getDefinition().getMembers().declare(equalsMethodSymbol, equalsDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new DecacInternalError("Equals symbol already declared ???");
        }
        envTypes.put(objectSymb, OBJECT.getDefinition());
        classesAndNull.add(OBJECT);
        OBJECT.getDefinition().incNumberOfMethods();

        Symbol nullSymb = compiler.createSymbol("null");
        NULL = new NullType(nullSymb);
        classesAndNull.add(NULL);

        typeUnaryOp = new HashMap<>();
        typeUnaryOp.put(new KeyTypeUnaryOp("-", INT), INT);
        typeUnaryOp.put(new KeyTypeUnaryOp("-", FLOAT), FLOAT);
        typeUnaryOp.put(new KeyTypeUnaryOp("!", BOOLEAN), BOOLEAN);

        typeArithOp = new HashMap<>();
        typeArithOp.put(new KeyTypeArithOp(INT, INT), INT);
        typeArithOp.put(new KeyTypeArithOp(INT, FLOAT), FLOAT);
        typeArithOp.put(new KeyTypeArithOp(FLOAT, INT), FLOAT);
        typeArithOp.put(new KeyTypeArithOp(FLOAT, FLOAT), FLOAT);

        typeBinaryOp = new HashMap<>();
        for (KeyTypeArithOp key : typeArithOp.keySet()) {
            Type type1 = key.getType1();
            Type type2 = key.getType2();
            Type type = typeArithOp.get(key);
            for (String op : new String[]{"+", "-", "/", "*"}) {
                typeBinaryOp.put(new KeyTypeBinaryOp(op, type1, type2), type);
            }
            for (String op : new String[]{"==", "!=", "<=", "<", ">=", ">"}) {
                typeBinaryOp.put(new KeyTypeBinaryOp(op, type1, type2), BOOLEAN);
            }
        }
        typeBinaryOp.put(new KeyTypeBinaryOp("%", INT, INT), INT);
        for (String op : new String[]{"&&", "||", "==", "!="}) {
            typeBinaryOp.put(new KeyTypeBinaryOp(op, BOOLEAN, BOOLEAN), BOOLEAN);
        }
        for (Type type1 : classesAndNull) {
            for (Type type2 : classesAndNull) {
                typeBinaryOp.put(new KeyTypeBinaryOp("==", type1, type2), BOOLEAN);
                typeBinaryOp.put(new KeyTypeBinaryOp("!=", type1, type2), BOOLEAN);
            }
        }
    }
    private final DecacCompiler compiler;
    private final Map<Symbol, TypeDefinition> envTypes;
    private final List<Type> classesAndNull;
    private final Map<KeyTypeUnaryOp, Type> typeUnaryOp;
    public Type getTypeUnaryOp(String op, Type type) {
        KeyTypeUnaryOp key = new KeyTypeUnaryOp(op, type);
        if (!typeUnaryOp.containsKey(key)) return null;
        return typeUnaryOp.get(key);
    }
    private final Map<KeyTypeArithOp, Type> typeArithOp;
    public Type getTypeArithOp(Type type1, Type type2) {
        KeyTypeArithOp key = new KeyTypeArithOp(type1, type2);
        if (!typeArithOp.containsKey(key)) return null;
        return typeArithOp.get(key);
    }
    private final Map<KeyTypeBinaryOp, Type> typeBinaryOp;
    public Type getTypeBinaryOp(String op, Type type1, Type type2) {
        KeyTypeBinaryOp key = new KeyTypeBinaryOp(op, type1, type2);
        if (!typeBinaryOp.containsKey(key)) return null;
        return typeBinaryOp.get(key);
    }
    public TypeDefinition get(Symbol name) {
        return envTypes.get(name);
    }
    public boolean declareClasse(AbstractIdentifier name, ClassDefinition superClass, Location location) {
        if (envTypes.containsKey(name.getName())) return false;
        Symbol symb = name.getName();
        ClassType type = new ClassType(symb, location, superClass);
        name.setDefinition(type.getDefinition());
        envTypes.put(symb, type.getDefinition());
        classesAndNull.add(type);

        for (Type type2 : classesAndNull) {
            typeBinaryOp.put(new KeyTypeBinaryOp("==", type, type2), BOOLEAN);
            typeBinaryOp.put(new KeyTypeBinaryOp("!=", type, type2), BOOLEAN);
            typeBinaryOp.put(new KeyTypeBinaryOp("==", type2, type), BOOLEAN);
            typeBinaryOp.put(new KeyTypeBinaryOp("!=", type2, type), BOOLEAN);
        }
        return true;
    }
    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public boolean subtype(Type type1, Type type2) {
        if (type1.equals(type2)) return true;
        if (type1.isClass()) {
            ClassType classType = (ClassType) type1;
            if (!type2.isClass()) return false;
            ClassType classType2 = (ClassType) type2;
            return classType.isSubClassOf(classType2);
        }
        return (type1.isNull() && type2.isClass());

    }
    public AbstractExpr assignCompatible(Type type1,
                                    AbstractExpr expr2) {
        Type type2 = expr2.getType();
        if (type1.isFloat() && type2.isInt()) {
            return new ConvFloat(expr2);
        }
        if (type1.equals(type2) || compiler.environmentType.subtype(type2, type1)) return expr2;
        return null;
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final ClassType OBJECT;
    public final NullType NULL;
}
