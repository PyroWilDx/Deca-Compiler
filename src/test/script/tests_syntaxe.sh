#! /bin/sh

# Auteur : gl47
# Version initiale : 01/01/2024

# Notre script de tests de la syntaxe

# dans le cas du fichier valide, on vérifie qu'il n'y a pas eu
# d'erreur et que l'arbre est correct l'arbre donné est bien le bon
# en stoquant la valeur attendue dans un fichier et en
# utilisant la commande unix "diff".
#

RED='\033[0;31m';
GREEN='\033[0;32m';
NC='\033[0m'; # No Color

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "${GREEN}Echec attendu pour test_synt sur $1.${NC}"
    else
        echo "KO -- Succes inattendu de test_synt sur $1. ------"
        exit 1
    fi
}

test_synt_valide () {
    # $1 = premier argument.
    #test_synt "$1" > "resultatTestSynt$1.res"
    test_synt "$1" > "${1%.deca}".res
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "KO -- Echec inattendu pour test_synt sur $1"
        exit 1
    else
      if diff -q "${1%.deca}_resultatCorrect.res" "${1%.deca}".res
      then
        echo "${GREEN}Succes attendu et arbres compatibles de test_synt sur $1${NC}"
      else
        echo "KO -- arbre incompatible,pourtant Succes attendu de test_synt sur $1"
      fi
    fi
}

echo "-----------Langage Hello World-----------"
for cas_de_test in src/test/deca/syntax/invalid/helloWorld/*.deca
do
    test_synt_invalide "$cas_de_test"
done

test_synt_valide "src/test/deca/syntax/valid/helloWorld/hello.deca"

echo "-----------Langage Hello World avec include-----------"

test_synt_valide "src/test/deca/syntax/valid/include/include.deca"


echo "-----------Langage Sans Objets-----------"
for cas_de_test in src/test/deca/syntax/invalid/sansObjet/*.deca
do
    test_synt_invalide "$cas_de_test"
done

test_synt_valide "src/test/deca/syntax/valid/sansObjet/noOperation.deca"
test_synt_valide "src/test/deca/syntax/valid/sansObjet/sansObjet.deca"
test_synt_valide "src/test/deca/syntax/valid/sansObjet/sansObjetComplexe.deca"

echo "-----------Langage Avec Objets-----------"
for cas_de_test in src/test/deca/syntax/valid/avecObjet/*.deca
do
    test_synt_valide "$cas_de_test"
done

for cas_de_test in src/test/deca/syntax/invalid/avecObjet/*.deca
do
    test_synt_invalide "$cas_de_test"
done
