#!/usr/bin/env python3

import os
import sys
import subprocess

allTestedFiles = []
doParallel = False


def prettyPrint(msg):
    print()
    # print("\033[32m==============================================\033[0m")
    print("\033[1;34m==============================================\033[0m")
    # print(f"\033[32m{msg}\033[0m")
    print(f"\033[1;34m{msg}\033[0m")
    # print("\033[32m==============================================\033[0m")
    print("\033[1;34m==============================================\033[0m")
    print()


def printOrAssert(out, expectedResult, doAssert, perf=False):
    if doAssert:
        if not perf:
            if expectedResult != out:
                print("\033[0;31m", end="")
                print(" Failed")
                print("Expected Result :")
                print(expectedResult.decode('utf-8'))
                print("Result of Program :")
                print(out.decode('utf-8'), end="")
                print("\033[0m", end="")
            else:
                print(f"\033[32m Passed\033[0m")
                sys.stdout.flush()
        else:
            expectedLength = len(expectedResult)
            if expectedResult != out[:expectedLength]:
                print("\033[0;31m", end="")
                print(" Failed")
                print("Expected Result :")
                print(expectedResult.decode('utf-8'))
                print()
                print("Result of Program :")
                print(out.decode('utf-8'))
                print("\033[0m", end="")
            else:
                print(f"\033[32m Passed\033[0m")
                print(f"\033[1;36m{out[expectedLength:-1].decode('utf-8')}\033[0m")

    else:
        print()
        print(out.decode('utf-8'))


def doVerify(decaFilePath,
             expectedResult=b"", decacExpected="",
             decacOptions="", decacFail=False,
             execError=False, execFail=False,
             input="",
             imaOptions="",
             doAssert=True):
    extIndex = decaFilePath.rfind(".")
    decaFilePathNoExt = decaFilePath[:extIndex]
    if (("-b" not in decacOptions) and ("-n" not in decacOptions) and
            ("-v" not in decacOptions) and ("-d" not in decacOptions) and
            ("-p" not in decacOptions) and (not decacFail)):
        allTestedFiles.append(decaFilePathNoExt)  # To Test -P Later...

    if doParallel and (decaFilePathNoExt not in allTestedFiles):
        return 0

    print(f"――――――――――― {'/'.join(decaFilePath.split('/')[2:])} ―――――――――――", end="")
    sys.stdout.flush()

    if not doParallel:
        decacCmd = f"decac {decacOptions} ./src/test/deca/{decaFilePath}"
        if decacFail:  # Ne doit plus être utilisé
            decacCmd += " > /dev/null 2>&1"
            out = os.system(decacCmd)
            assert (os.WEXITSTATUS(out) != 0)
            return 0
        if ("-b" in decacOptions) or ("-p" in decacOptions) or ("-d" in decacOptions):
            out = subprocess.check_output(decacCmd, shell=True)
            if "-d" in decacOptions:
                if expectedResult in out:
                    print(f"\033[32m Passed\033[0m")
                else:
                    print("\033[0;31m Failed\033[0m")

                return 0
            printOrAssert(out, decacExpected, doAssert)
            return 0

        os.system(decacCmd)

    if "-v" in decacOptions:
        print(f"\033[32m Passed\033[0m")
        return 0

    execCmd = f"ima {imaOptions} ./src/test/deca/{decaFilePathNoExt}.ass"
    if execError:
        try:
            subprocess.check_output(execCmd, input=input, shell=True)  # Sould Fail
            assert False
        except subprocess.CalledProcessError as e:
            printOrAssert(e.output, expectedResult, doAssert)
        return 0

    if execFail:
        try:
            subprocess.check_output(execCmd, input=input, shell=True)  # Sould Fail
            assert False
        except subprocess.CalledProcessError as e:
            printOrAssert(e.output, expectedResult, doAssert)
            return 0

    out = subprocess.check_output(execCmd, input=input, shell=True)
    printOrAssert(out, expectedResult, doAssert, "-s" in imaOptions)


def doTests():
    """Test Étape C"""

    """
    ============================================
    ============================================
    """
    if not doParallel:
        prettyPrint("TEST DE L'ÉTAPE C (VALIDE)")

    doVerify("codegen/valid/iostream/printString.deca",
             expectedResult=b"Hello World ! Second Argument\n"
                            b"Second Println\n"
                            b"Print Normal 1, Println Normal 2\n")

    doVerify("codegen/valid/iostream/printIntFloat.deca",
             expectedResult=b"Chaine de Int : 1 2 42 -1 0 -42\n"
                            b"Chaine de Float : 1.22000e+00 -4.24242e+01 0.00000e+003.1416 -2.78000e+00\n")

    doVerify("codegen/valid/iostream/printFloatHexa.deca",
             expectedResult=b"0x1.3851ecp+0 -0x1.5364d8p+5 0x0p+03.1416 -0x1.63d70ap+1\n")

    doVerify("codegen/valid/iostream/includeSimple.deca",
             expectedResult=b"Hello World\n")

    doVerify("codegen/valid/declarations/declVarSimple.deca",
             expectedResult=b"x = 1 | y = 2\n")

    doVerify("codegen/valid/declarations/declVarMany.deca",
             expectedResult=b"x = 1\n"
                            b"y = 42 | z = 3.14160e+00\n")

    doVerify("codegen/valid/operations/opArith.deca",
             expectedResult=b"1 + 1 = 2\n"
                            b"1 - 1 = 0\n"
                            b"1 - 42 = -41\n"
                            b"1 - -42 = 43\n"
                            b"0 * 1 = 0\n"
                            b"1 * 0 = 0\n"
                            b"1 * 1 = 1\n"
                            b"10 * 42 = 420\n"
                            b"10 / 3 = 3\n"
                            b"0 / 1 = 0\n"
                            b"1.1 + 3.2 = 4.30000e+00\n"
                            b"1.1 - 3.2 = -2.10000e+00\n"
                            b"3.14 * 3.14 = 9.85960e+00\n"
                            b"10.0 / 3.0 = 3.33333e+00\n"
                            b"0.0 / 1.0 = 0.00000e+00\n"
                            b"20.8 / 4.0 = 5.20000e+00\n"
                            b"4 * 6 / 2 / 2 * 10 = 60\n")

    doVerify("codegen/valid/operations/opArithConv.deca",
             expectedResult=b"1.1 + 2 = 3.10000e+00\n"
                            b"1.1 - 2 = -9.00000e-01\n"
                            b"42 - 42.0 = 0.00000e+00\n"
                            b"10 * 4.2 = 4.20000e+01\n"
                            b"4.0 / 3 = 1.33333e+00\n"
                            b"4 / 3.0 = 1.33333e+00\n")

    doVerify("codegen/valid/operations/opShift.deca",
             expectedResult=b"14 14 112 112 1792 896 224 832 104 0 0 14336 28672 25 50 100 0 0 64\n")

    doVerify("codegen/valid/conditions/boolLazyEval.deca",
             expectedResult=b"")

    doVerify("codegen/valid/conditions/ifThenElseSimple.deca",
             expectedResult=b"12345678910111213141516\n")

    doVerify("codegen/valid/conditions/ifThenElseComplex.deca",
             expectedResult=b"OK\n")

    doVerify("codegen/valid/conditions/whileSimple.deca",
             expectedResult=b"0123456789\n")

    doVerify("codegen/valid/conditions/whileComplex.deca",
             expectedResult=b"168 * 42 * 10 = 70560\n"
                            b"x = 70560\n")

    doVerify("codegen/valid/conditions/whileIfThenElse.deca",
             expectedResult=b"4321\n")

    doVerify("codegen/valid/objects/fields/newSimple.deca")

    doVerify("codegen/valid/objects/fields/fieldSimple.deca")

    doVerify("codegen/valid/objects/fields/fieldSelection.deca",
             expectedResult=b"1.00000e+00 2 4 0 1.00000e+00\n"
                            b"8.00000e+00 3.20000e+01 16 0.00000e+00 1\n"
                            b"42 12\n")

    doVerify("codegen/valid/objects/fields/fieldInitReg.deca",
             expectedResult=b"1 20 2\n")

    doVerify("codegen/valid/objects/fields/fieldInitFieldSimple.deca",
             expectedResult=b"10\n")

    doVerify("codegen/valid/objects/fields/fieldInitMethod.deca",
             expectedResult=b"30 60 10\n")

    doVerify("codegen/valid/objects/fields/fieldInitFieldComplex.deca",
             expectedResult=b"0 0 20\n")

    doVerify("codegen/valid/objects/instance/thisSimple.deca",
             expectedResult=b"2 2\n"
                            b"6 6\n")

    doVerify("codegen/valid/objects/instance/noThisAccess.deca",
             expectedResult=b"2 6\n"
                            b"60 120\n")

    doVerify("codegen/valid/objects/methods/methodSimple.deca")

    doVerify("codegen/valid/objects/methods/methodCallSimple.deca",
             expectedResult=b"Method Called\n")

    doVerify("codegen/valid/objects/methods/methodCallParams.deca",
             expectedResult=b"x + y + z = 10 + 20 + 30 = 60\n"
                            b"x * y * z = 2 * 6 * 10 = 120\n")

    doVerify("codegen/valid/objects/methods/methodCallReturn.deca",
             b"0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20\n")

    doVerify("codegen/valid/objects/methods/earlyReturn.deca",
             expectedResult=b"10\n")

    doVerify("codegen/valid/objects/methods/varInMethod.deca",
             expectedResult=b"2 4 16 22 44\n")

    doVerify("codegen/valid/objects/methods/recursiveMethod.deca",
             expectedResult=b"40 80 120 160 200 240 280 320 360 400\n"
                            b"400 1 2\n")

    doVerify("codegen/valid/objects/methods/methodCallMethod.deca",
             expectedResult=b"10 20 30\n"
                            b"80 70 60\n"
                            b"180 170 160\n"
                            b"110 120 130\n"
                            b"80 70 60\n"
                            b"180 170 160\n")

    doVerify("codegen/valid/objects/methods/callOtherClassMethod.deca",
             expectedResult=b"1 2 3 42\n"
                            b"42 3 0 0\n")

    doVerify("codegen/valid/objects/methods/callInsideCall.deca",
             expectedResult=b"200 30 0 30\n")

    doVerify("codegen/valid/objects/extends/extendsFieldSimple.deca",
             expectedResult=b"1 2\n"
                            b"1 2 0\n")

    doVerify("codegen/valid/objects/extends/extendsFields.deca",
             expectedResult=b"1 2\n"
                            b"4 2 0\n")

    doVerify("codegen/valid/objects/extends/extendsMethods.deca",
             expectedResult=b"1 2 4 2 0\n")

    doVerify("codegen/valid/objects/polymorphism/redefinedMethodOrder.deca",
             expectedResult=b"A0 A10 A20 A30 A40\n"
                            b"A0 B10 B20 B30 A40 B50 B60\n"
                            b"A0 C10 B20 B30 C40 B50 C60 C70 C80\n")

    doVerify("codegen/valid/objects/polymorphism/fieldRedef.deca",
             expectedResult=b"1 2 1\n")

    doVerify("codegen/valid/objects/polymorphism/ex_Video5_Page11.deca",
             expectedResult=b"p1 : Point 2d : (1, 1)\n"
                            b"p3 before p2.diag(3) : Point 3d : (2, 2, 2)\n"
                            b"p3 after p2.diag(3) : Point 3d : (5, 5, 5)\n"
                            b"p2 : Point 3d : (5, 5, 5)\n")

    doVerify("codegen/valid/objects/others/equalsSimple.deca",
             expectedResult=b"OK1 OK2 OK3 OK4 OK5 OK6\n")

    doVerify("codegen/valid/objects/others/assignInside.deca",
             expectedResult=b"0 0\n"
                            b"10 0\n"
                            b"OK\n"
                            b"36 6\n"
                            b"36 100\n")

    doVerify("codegen/valid/objects/others/newNoAssign.deca",
             expectedResult=b"42\n"
                            b"60\n"
                            b"42 60 70\n"
                            b"1\n")

    doVerify("codegen/valid/objects/others/asmSimple.deca",
             expectedResult=b"10 180 2 4\n")

    doVerify("codegen/valid/objects/others/instanceOf.deca",
             expectedResult=b"OK1 OK2 OK3 OK4 OK5 OK6 OK7 OK8 OK9 OK10 OK11 OK12 OK13 OK14 OK15 OK16 OK17 OK18 OK19 OK20\n")

    doVerify("codegen/valid/objects/others/castObj.deca",
             expectedResult=b"1.00000e+01 20 10 30 60 4.20000e+01 42 1.00000e+01\n")

    doVerify("codegen/valid/registers/opRegOverflow.deca",
             expectedResult=b"52 52\n",
             decacOptions="-r 4")

    doVerify("codegen/valid/registers/methodRegOverflow.deca",
             expectedResult=b"600\n"
                            b"OK\n",
             decacOptions="-r 4")

    doVerify("codegen/valid/programs/linkedList.deca",
             expectedResult=b"3\n"
                            b"2\n"
                            b"1\n")

    doVerify("codegen/valid/programs/binaryTree.deca",
             expectedResult=b"false\n"
                            b"true\n"
                            b"true\n"
                            b"true\n"
                            b"true\n"
                            b"false\n"
                            b"true\n"
                            b"true\n"
                            b"true\n"
                            b"false\n")

    doVerify("codegen/valid/provided/ecrit0.deca",
             expectedResult=b"ok\n"
                            b"ok\n")

    doVerify("codegen/valid/provided/entier1.deca",
             expectedResult=b"1\n"
                            b"2\n")

    doVerify("codegen/valid/provided/cond0.deca",
             expectedResult=b"ok\n")

    doVerify("codegen/valid/provided/exdoc.deca",
             expectedResult=b"a.getX() = 1\n")

    """
    ============================================
    ============================================
    """
    if not doParallel:
        prettyPrint("TEST DE L'ÉTAPE C (INTERACTIVE)")

    doVerify("codegen/interactive/iostream/readIntFloat.deca",
             expectedResult=b"3.20000e+00\n",
             input=b"1\n2.2")

    """
    ============================================
    ============================================
    """
    if not doParallel:
        prettyPrint("TEST DE L'ÉTAPE C (INVALIDE)")

    doVerify("codegen/invalid/errors/declVarNoInit.deca",
             expectedResult=b"  ** IMA ** ERREUR ** Ligne 13 : \n"
                            b"    WINT avec R1 indefini\n",
             execError=True)

    doVerify("codegen/invalid/errors/divisionBy0.deca",
             expectedResult=b"Error: Division by 0\n",
             execError=True)

    doVerify("codegen/invalid/errors/moduloBy0.deca",
             expectedResult=b"2\n"
                            b"Error: Division by 0\n",
             execError=True)

    doVerify("codegen/invalid/errors/moduloBy0Const.deca",
             expectedResult=b"Error: Division by 0\n",
             execError=True)

    doVerify("codegen/invalid/errors/floatOverflow.deca",
             expectedResult=b"Error: Float Operation Overflow\n",
             execError=True)

    doVerify("codegen/invalid/errors/floatOverflowConstIntFloat.deca",
             expectedResult=b"Error: Float Operation Overflow\n",
             execError=True)

    doVerify("codegen/invalid/errors/floatOverflowConstFloatInt.deca",
             expectedResult=b"Error: Float Operation Overflow\n",
             execError=True)

    doVerify("codegen/invalid/errors/floatOverflowConstFloatFloat.deca",
             expectedResult=b"Error: Float Operation Overflow\n",
             execError=True)

    doVerify("codegen/interactive/errors/readError.deca",
             expectedResult=b"Error: Input/Output Error\n",
             execError=True,
             input=b"10")

    doVerify("codegen/invalid/errors/stackOverflow.deca",
             expectedResult=b"Error: Stack Overflow\n",
             execError=True)

    doVerify("codegen/invalid/errors/nullPointer.deca",
             expectedResult=b"Error: Dereferencing Null Pointer\n",
             execError=True)

    doVerify("codegen/invalid/errors/heapOverflow.deca",
             expectedResult=b"Error: Heap Overflow\n",
             execError=True)

    doVerify("codegen/invalid/errors/missingReturn.deca",
             expectedResult=b"Error: Exiting function 'A.missingReturn()' without return\n",
             execError=True)

    doVerify("codegen/invalid/errors/invalidCast.deca",
             expectedResult=b"Error: Failed to cast variable of type 'A' to class 'B'\n",
             execError=True)

    """
    ============================================
    ============================================
    """
    if not doParallel:
        prettyPrint("TEST DE L'ÉTAPE C (PERF)")

    doVerify("codegen/perf/provided/syracuse42.deca",
             decacOptions="-n",
             expectedResult=b"8\n",
             imaOptions="-s")

    doVerify("codegen/perf/provided/ln2.deca",
             expectedResult=b"6.93148e-01 = 0x1.62e448p-1\n",
             decacOptions="-n",
             imaOptions="-s")

    doVerify("codegen/perf/provided/ln2_fct.deca",
             expectedResult=b"6.93148e-01 = 0x1.62e448p-1\n",
             decacOptions="-n",
             imaOptions="-s")

    """
    ============================================
    ============================================
    """
    if not doParallel:
        prettyPrint("TEST DE L'ÉTAPE C (OPTION)")

    doVerify("codegen/valid/options/optionBanner.deca",
             decacExpected=b"== Banner : Gr10 / Gl47 ==\n",
             decacOptions="-b")

    doVerify("codegen/invalid/options/optionParse.deca",
             decacExpected=b"class A extends Object {\n\tint a;\n\tfloat method(int b, float c) {\n\t\t(this.a = b);\n\t\treturn c;\n\t}\n\tvoid methodAsm()\n\tasm(\"CODE ASS\");\n}\n{\n\tA class1 = new A();\n\tint x = 1;\n\tfloat y = 2;\n\tfloat z = 0x1.3851ecp0;\n\tboolean bool = false;\n\tprintln((1 / 0));\n\tif (((class1 != null) && (!bool))) {\n\t\t(class1.a = 1);\n\t\t(z = class1.method(x, z));\n\t} else {\n\t}\n\t;\n\t(z = ((x + y) - (x * (x + y))));\n\tprint(x, y, z);\n\tprintln(\"z = \", z);\n\tprintlnx(y, z);\n\tif ((y == z)) {\n\t\t(y = z);\n\t} else {\n\t\t(y = (z - 1));\n\t}\n\twhile (false) {\n\t\tif ((y != z)) {\n\t\t\tif ((y < z)) {\n\t\t\t\tif ((y > z)) {\n\t\t\t\t\t(x = readInt());\n\t\t\t\t} else {\n\t\t\t\t\t(y = 4);\n\t\t\t\t}\n\t\t\t} else {\n\t\t\t}\n\t\t} else {\n\t\t\twhile (false) {\n\t\t\t\tprint();\n\t\t\t}\n\t\t}\n\t\t(y = readFloat());\n\t}\n\tx;\n\ty;\n\tz;\n}\n",
             decacOptions="-p")

    doVerify("codegen/valid/options/optionParseEmptyMain.deca",
             decacExpected=b"\n",
             decacOptions="-p")

    doVerify("codegen/valid/options/optionVerification.deca",
             decacOptions="-v")

    doVerify("codegen/invalid/options/optionNoCheck.deca",
             expectedResult=b"1\n",
             decacOptions="-n")

    doVerify("codegen/invalid/options/floatOverflowConstIntFloat.deca",
             expectedResult=b"",
             decacOptions="-n")

    doVerify("codegen/invalid/options/floatOverflowConstFloatInt.deca",
             expectedResult=b"",
             decacOptions="-n")

    doVerify("codegen/invalid/options/floatOverflowConstFloatFloat.deca",
             expectedResult=b"",
             decacOptions="-n")

    doVerify("codegen/valid/options/optionDebugInfo.deca",
             expectedResult=b"Lexing and parsing of",
             decacOptions="-d")

    doVerify("codegen/valid/options/optionDebugDebug.deca",
             expectedResult=b"Generated assembly code",
             decacOptions="-d -d")

    doVerify("codegen/valid/options/optionDebugTrace.deca",
             expectedResult=b"You can now use ima to execute",
             decacOptions="-d -d -d")

    return 0


def decacParallel():
    global doParallel
    doParallel = True

    prettyPrint("TEST DE L'ÉTAPE C AVEC OPTION -P")

    decacCmd = f"./src/main/bin/decac -P"
    for filePath in allTestedFiles:
        decacCmd += f" ./src/test/deca/{filePath}.deca"
    print("\033[1mRemoving .ass files...\033[0m")
    for filePath in allTestedFiles:  # To Ensure that -P Recompiles All
        os.system(f"\\rm ./src/test/deca/{filePath}.ass")
    print("\033[1mRemove Successful\033[0m")
    print()

    os.system(decacCmd)

    doTests()

    return 0


def main():
    os.chdir(os.getcwd().split("src")[0])

    print()

    print("\033[1mRemoving .ass files...\033[0m")
    os.system("find ./src/test/deca/codegen/ -name \"*.ass\" -type f -delete")
    print("\033[1mRemove Successful\033[0m")

    doTests()

    # With -P
    decacParallel()


if __name__ == '__main__':
    main()
    print()
