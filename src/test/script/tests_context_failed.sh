#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

# Runs text_context on a file which didn't pass test_context_invalid
echo "indicate number of failed_file"
echo "(index in the file or plotted by test_context_invalid.sh)"
read n
n=$(expr $n + 1);
file=$(sed -n "$n"p failed_files);
echo $file;
./src/test/script/launchers/test_context $file;