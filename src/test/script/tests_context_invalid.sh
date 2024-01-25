#! /bin/sh

failed_files=;

echo "" > failed_files;

RED='\033[0;31m';
GREEN='\033[0;32m';
NC='\033[0m'; # No Color

cd "$(dirname "$0")"/../../.. || exit 1

compteur=0;
valids=0


isItTheGoodError () {
  compteur=$(expr $compteur + 1);
  if test "$real_error" = "$expected_error"
    then
      echo "${GREEN}PASSED${NC}";
      valids=$(expr $valids + 1);
    else
      echo "${RED}FAILED${NC}";
      failed_files="$failed_files $fichier";
    fi
}

testChemin() {
  for fichier in $chemin
  do
    basename "$fichier" | sed 's/\.deca//g' | sed 's/_/\ /' | sed 's/_/\./';
    ./src/test/script/launchers/test_context "$fichier"  1>debug 2>error;
    real_error=$(sed -r 's/.*regle.*:([0-9]*):([0-9]*):/Ligne \1:/g' error);
    echo $real_error;
    echo =============== REPONSE ATTENDUE ===============;
    expected_error=$(sed -n '5p' < "$fichier" | sed 's/\/\/\ *//g');
    echo $expected_error;
    isItTheGoodError;
    echo;
    echo;
  done
}

echo ==========================================================;
echo "                   LANGUAGE SANS OBJET"
echo ==========================================================;
chemin="src/test/deca/context/invalid/langage_sans_objet/*/*.deca";
testChemin;
echo;echo;
echo ==========================================================;
echo "                   LANGUAGE AVEC OBJET"
echo ==========================================================;
chemin="src/test/deca/context/invalid/langage_essentiel/*/*.deca"
testChemin;

rm error debug;

echo "${GREEN}PASSED : $valids / $compteur${NC}";
echo "failed : ";
i=1;
for failed_file in $failed_files
do
  echo "$i : ${RED}$(basename "$failed_file")${NC}";
  i=$(expr $i + 1);
  echo $failed_file >> failed_files;
done
