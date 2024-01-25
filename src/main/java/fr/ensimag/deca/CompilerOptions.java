package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.ensimag.deca.codegen.GameBoyManager;
import fr.ensimag.deca.codegen.RegManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl47
 * @date 01/01/2024
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public boolean getPrintBanner() {
        return printBanner;
    }

    public boolean getParse() {
        return parse;
    }

    public boolean getVerification() {
        return verification;
    }

    public boolean doCheck() {
        return !noCheck;
    }

    public int getNOfRegs() {
        return nOfRegs;
    }

    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verification = false;
    private boolean noCheck = false;
    private int nOfRegs = RegManager.MAX_REG;
    private int debug = QUIET;
    private boolean parallel = false;
    private final List<File> sourceFiles = new ArrayList<File>();

    public void parseArgs(String[] args) throws CLIException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.charAt(0) != '-') {
                File decaFile = new File(arg);
                sourceFiles.add(decaFile);
            } else {
                switch (arg.charAt(1)) {
                    case 'b': // Banner
                        printBanner = true;
                        break;
                    case 'p': // Parse
                        if (verification) {
                            throwError("You can't use -v and -p at the same time.");
                        }
                        parse = true;
                        break;
                    case 'v': // Verification
                        if (parse) {
                            throwError("You can't use -p and -v at the same time.");
                        }
                        verification = true;
                        break;
                    case 'n': // No Check
                        noCheck = true;
                        break;
                    case 'r': // Registers
                        String nOfRegsStr = args[i + 1];
                        i++;
                        try {
                            nOfRegs = Integer.parseInt(nOfRegsStr);
                            if (GameBoyManager.doCp && nOfRegs > GameBoyManager.nRegs) nOfRegs = GameBoyManager.nRegs;
                            if (nOfRegs < 4 || nOfRegs > 16) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            throwError("You must specify an integer between 4 and 16 after -r : -r <Number of Registers>.");
                        }
                        break;
                    case 'd': // Debug
                        debug = Math.min(debug + 1, TRACE);
                        break;
                    case 'P': // Parallel
                        parallel = true;
                        break;
                    case 'g':
                        if (nOfRegs > GameBoyManager.nRegs) nOfRegs = GameBoyManager.nRegs;
                        GameBoyManager.doCp = true;
                        if (arg.substring(2).equals("b")) {
                            GameBoyManager.doCpRgbds = true;
                        } else if (arg.substring(2).equals("debug")) {
                            GameBoyManager.debugMode = true;
                        }
                        break;
                    case 'I':
                        GameBoyManager.doIncludeUtils = true;
                        break;
                    default:
                        throwError(arg + " is not a valid option.");
                }
            }
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
            case QUIET:
                break; // keep default
            case INFO:
                logger.setLevel(Level.INFO);
                break;
            case DEBUG:
                logger.setLevel(Level.DEBUG);
                break;
            case TRACE:
                logger.setLevel(Level.TRACE);
                break;
            default:
                logger.setLevel(Level.ALL);
                break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    private static void throwError(String msg) throws CLIException {
        throw new CLIException("Compiler Option Error : " + msg);
    }
}
