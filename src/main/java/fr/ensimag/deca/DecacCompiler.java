package fr.ensimag.deca;

import fr.ensimag.deca.codegen.*;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;

import java.io.*;
import java.util.Collection;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 * <p>
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 * <p>
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl47
 * @date 01/01/2024
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();

        if (compilerOptions != null) {
            this.compilerOptions = compilerOptions;
        } else {
            this.compilerOptions = new CompilerOptions();
        }
        this.source = source;

        this.regManager = new RegManager(this.compilerOptions.getNOfRegs());
        this.errorManager = new ErrorManager();
        this.stackManager = null;
        this.condManager = new CondManager();
        this.vTableManager = new VTableManager();
        this.gameBoyManager = new GameBoyManager();
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    public void addInstruction(int index, Instruction instruction) {
        program.addInstruction(index, instruction);
    }

    public void addAllLine(Collection<AbstractLine> c) {
        program.addAllLine(c);
    }

    public void addAllLine(int index, Collection<AbstractLine> c) {
        program.addAllLine(index, c);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    public int getProgramLineCount() {
        return program.getLineCount();
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();


    /**
     * The global environment for types (and the symbolTable)
     */
    public final EnvironmentType environmentType = new EnvironmentType(this);
    public SymbolTable symbolTable = new SymbolTable();

    public Symbol createSymbol(String name) {
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        return symbolTable.create(name);
        // Done
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        int extensionIndex = sourceFile.lastIndexOf('.');
        String destFile;
        if (extensionIndex != -1) destFile = sourceFile.substring(0, extensionIndex);
        else destFile = sourceFile;
        if (GameBoyManager.doCp) destFile += ".asm";
        else destFile += ".ass";
        // Done
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    private final RegManager regManager;
    private final ErrorManager errorManager;
    private StackManager stackManager;
    private final CondManager condManager;
    private final VTableManager vTableManager;
    private final GameBoyManager gameBoyManager;
    private final StringBuilder tileIncludes = new StringBuilder();
    private final StringBuilder tilemapIncludes = new StringBuilder();
    public boolean noTileInclude() {
        return tileIncludes.length() == 0;
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName   name of the destination (assembly) file
     * @param out        stream to use for standard output (output of decac -p)
     * @param err        stream to use to display compilation errors
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
                              PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        LOG.info("Lexing and parsing of " + sourceName + "...");
        AbstractProgram prog = doLexingAndParsing(sourceName, err);
        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        assert (prog.checkAllLocations());
        LOG.info("Lexing and parsing of " + sourceName + " successful.");

        if (compilerOptions.getParse()) {
            LOG.info("Decompiling " + sourceName + "...");
            System.out.println(prog.decompile());
            LOG.info("Decompilation of " + sourceName + " successful.");
            LOG.info("Stopping because of -p...");
            return false;
        }

        LOG.info("Verification of " + sourceName + "...");
        prog.verifyProgram(this);
        assert (prog.checkAllDecorations());
        LOG.info("Verification of " + sourceName + " successful.");

        if (compilerOptions.getVerification()) {
            LOG.info("Stopping because of -v...");
            return false;
        }

        LOG.info("Compiling " + sourceName + "...");
        if (GameBoyManager.doCp) {
            prog.codeGenProgramGb(this);
        } else {
            prog.codeGenProgram(this);
        }

        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file...");
        PrintStream printStream = new PrintStream(fstream);
        program.display(printStream);
        if (GameBoyManager.doCp) {
            if (tileIncludes.length() > 0) {
                printStream.println("SECTION \"Tiles\", ROM0");
                printStream.println("Tiles:");
                printStream.print(tileIncludes);
                printStream.println("TilesEnd:");
            }
            if (tilemapIncludes.length() > 0) {
                printStream.println("SECTION \"Tilemaps\", ROM0");
                printStream.println("Tilemaps:");
                printStream.print(tilemapIncludes);
                printStream.println("TilemapsEnd:");
            }

            if (GameBoyManager.doCpRgbds) {
                // Generating the .o file
                if (GameBoyManager.debugMode)
                    LOG.setLevel(org.apache.log4j.Level.DEBUG);
                try {
                    ProcessBuilder pb;
                    LOG.debug("Source file: " + destName);
                    String includePath = System.getProperty("user.dir").split("src")[0] + "src/main/bin/include";
                    LOG.debug("Include path: " + includePath);
                    LOG.debug("Dest file: " + destName.replace(".asm", ".o"));
                    pb = new ProcessBuilder().command("rgbasm", "-L", "-I", includePath, "-o", destName.replace(".asm",
                            ".o"), destName);
                    if (GameBoyManager.debugMode) {
                        pb.inheritIO();
                    }
                    pb.start().waitFor();
                } catch (IOException | InterruptedException e) {
                    LOG.info("Failed to assemble generated files with rgbasm", e);
                    throw new DecacInternalError("Failed to assemble generated files with rgbasm");
                }

                LOG.debug("Compiled generated files with rgbasm...");

                // Generating the .gb file
                try {
                    ProcessBuilder pb;
                    pb = new ProcessBuilder().command("rgblink", "-o", destName.replace(".asm", ".gb"),
                            destName.replace(".asm", ".o"));
                    if (GameBoyManager.debugMode) {
                        pb.inheritIO();
                    }
                    pb.start().waitFor();
                } catch (IOException | InterruptedException e) {
                    LOG.debug("Failed to link generated files with rgblink", e);
                    throw new DecacInternalError("Failed to assemble generated files with rgblink");
                } finally {
//                    new File(destName.replace(".asm", ".o")).delete();
                }

                // Running rgbfix
                try {
                    ProcessBuilder pb;
                    pb = new ProcessBuilder().command("rgbfix", "-v", "-p", "0xFF", destName.replace(".asm", ".gb"));
                    if (GameBoyManager.debugMode) {
                        pb.inheritIO();
                    }
                    pb.start();
                } catch (IOException e) {
                    LOG.debug("Failed to run rgbfix", e);
                    throw new DecacInternalError("Failed to run rgbfix");
                }
            }
        }
        LOG.info("Compilation of " + sourceName + " successful.");

        LOG.trace("You can now use ima to execute " + destName);

        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err        Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError    When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     *                            compiler.
     * @throws LocationException  When a compilation error (incorrect program)
     *                            occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

    public void addTileInclude(StringBuilder tileContent) {
        tileIncludes.append(tileContent);
    }

    public void addTilemapInclude(StringBuilder tilemapContent) {
        tilemapIncludes.append(tilemapContent);
    }


    public RegManager getRegManager() {
        return regManager;
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public void setStackManager(StackManager value) {
        stackManager = value;
    }

    public StackManager getStackManager() {
        return stackManager;
    }

    public CondManager getCondManager() {
        return condManager;
    }

    public VTableManager getVTableManager() {
        return vTableManager;
    }

    public GameBoyManager getGameBoyManager() {
        return gameBoyManager;
    }

}
