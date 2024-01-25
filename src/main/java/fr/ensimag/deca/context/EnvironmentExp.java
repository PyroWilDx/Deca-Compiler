package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * <p>
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * <p>
 * The dictionary at the head of this list thus corresponds to the "current"
 * block (eg class).
 * <p>
 * Searching a definition (through method get) is done in the "current"
 * dictionary and in the parentEnvironment if it fails.
 * <p>
 * Insertion (through method declare) is always done in the "current" dictionary.
 *
 * @author gl47
 * @date 01/01/2024
 */
public class EnvironmentExp {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).
    private EnvironmentExp parentEnvironment;
    private HashMap<Symbol, ExpDefinition> env;

    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.env = new HashMap<>();
    }

    public Symbol disjointUnion(EnvironmentExp envExp) {
        // if the union isn't disjoint, returns the symbol occuring twice
        for (Symbol s1 : envExp.getKeys()) {
            if (this.get(s1) != null) return s1;
            ExpDefinition def = envExp.env.get(s1);
            this.env.put(s1, def);
        }

        return null;
    }
    public void putAll(EnvironmentExp env) {
        for (Symbol s1 : env.getKeys()) {
            ExpDefinition def = env.env.get(s1);
            this.env.put(s1, def);
        }
    }
    public void setEnv(HashMap<Symbol, ExpDefinition> env) {
        this.env = env;
    }

    public Set<Symbol> getKeys() {
        if (this.parentEnvironment == null) {
            return this.env.keySet();
        }
        this.env.keySet().iterator();
        Set<Symbol> concat = new HashSet<>(this.env.keySet());
        concat.addAll(this.parentEnvironment.getKeys());
        return concat;
    }


    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    public static EnvironmentExp empile(EnvironmentExp env1, EnvironmentExp env2) {
        EnvironmentExp copyEnv1 = env1.copy();
        EnvironmentExp copyEnv2 = env2.copy();
        EnvironmentExp dernier = copyEnv1;
        while (dernier.parentEnvironment != null) dernier = dernier.parentEnvironment;
        dernier.parentEnvironment = copyEnv2;
        return copyEnv1;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(Symbol key) {
        if (env.containsKey(key)) return env.get(key);

        if (parentEnvironment == null) return null;

        return parentEnvironment.get(key);
        // Done
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * <p>
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary
     * - or, hides the previous declaration otherwise.
     *
     * @param name Name of the symbol to define
     * @param def  Definition of the symbol
     * @throws DoubleDefException if the symbol is already defined at the "current" dictionary
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        if (env.containsKey(name)) {
            throw new DoubleDefException();
        }
        env.put(name, def);
        // Done
    }

    public EnvironmentExp copy() {
        EnvironmentExp copy;
        if (this.parentEnvironment == null) copy = new EnvironmentExp(null);
        else copy = new EnvironmentExp(this.parentEnvironment.copy());
        HashMap<Symbol, ExpDefinition> clone = new HashMap<>(this.env);
        copy.setEnv(clone);
        return copy;
    }

    @Override
    public String toString() {
        if (this.parentEnvironment == null) return this.env.toString();
        return this.env.toString() + "\n" + this.parentEnvironment.toString();
    }
}
