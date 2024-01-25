#!/usr/bin/env python3

import os
import sys
import subprocess

nbTest = 0
maxTest = 1


def prettyPrint(msg):
    print()
    # print("\033[32m==============================================\033[0m")
    print("\033[1;34m――――――――――――――――――――――――――――――――――――――――――――――\033[0m")
    # print(f"\033[32m{msg}\033[0m")
    print(f"\033[1;34m{msg}\033[0m")
    # print("\033[32m==============================================\033[0m")
    print("\033[1;34m――――――――――――――――――――――――――――――――――――――――――――――\033[0m")
    print()


def doVerify(decaFilePath):
    global nbTest

    extIndex = decaFilePath.rfind(".")
    decaFilePathNoExt = decaFilePath[:extIndex]
    lastSlashIndex = decaFilePathNoExt.rfind("/")
    decaFileNameNoExt = decaFilePathNoExt[lastSlashIndex + 1:]

    print(f"\033[32m=========== {'/'.join(decaFilePath.split('/')[1:])} ===========\033[0m")
    sys.stdout.flush()

    decacCmd = f"decac -gdebug ./src/test/deca/{decaFilePath}"
    os.system(decacCmd)

    bin = "./src/test/deca/gameboy/bin"
    os.system(f"mkdir -p {bin}")
    os.system(f"cp ./src/test/deca/{decaFilePathNoExt}.asm {bin}")

    lastDir = os.getcwd()
    os.chdir(bin)

    os.system(f"rgbasm -L -o {decaFileNameNoExt}.o {decaFileNameNoExt}.asm")
    os.system(f"rgblink -o {decaFileNameNoExt}.gb {decaFileNameNoExt}.o")
    os.system(f"rgbfix -v -p 0xFF {decaFileNameNoExt}.gb")

    # os.system(f"Emulicious.jar {decaFileNameNoExt}.gb &")
    os.system(f"Emulicious.jar {decaFileNameNoExt}.gb > /dev/null 2>&1 &")

    os.chdir(lastDir)

    nbTest += 1

    if nbTest == maxTest:
        input("Enter to Continue...")
        os.system("pkill -f Emulicious.jar")
        nbTest = 0


def doTests():
    """Test Étape C"""

    print()

    prettyPrint("TEST Librarie GameBoy")

    doVerify("gameboy_lib/SnakeGame.deca")

    return 0


def main():
    os.chdir(os.getcwd().split("src")[0])

    print()

    print("\033[1mRemoving .asm files...\033[0m")
    os.system("find ./src/test/deca/gameboy/ -name \"*.asm\" -type f -delete")
    # os.system("\\rm ./src/test/deca/gameboy/bin/*.asm")
    print("\033[1mRemoving .o files...\033[0m")
    os.system("\\rm ./src/test/deca/gameboy/bin/*.o")
    print("\033[1mRemoving .gb files...\033[0m")
    os.system("\\rm ./src/test/deca/gameboy/bin/*.gb")
    print("\033[1mRemove Successful\033[0m")

    doTests()


if __name__ == '__main__':
    os.system("pkill -f Emulicious.jar")
    main()
    print()
    input("Enter to Exit...")
    os.system("pkill -f Emulicious.jar")
