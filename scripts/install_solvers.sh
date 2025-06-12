#!/usr/bin/env sh

TEMP_DIR="$(pwd)/temp"
SOLVERS_DIR="$1"

Z3_VER=${2-"4.15.1"}
CVC4_VER=${3-"1.8"}
CVC5_VER=${4-"1.2.1"}

mkdir -p "$SOLVERS_DIR"
mkdir -p "$TEMP_DIR"
# cvc5
wget "https://github.com/cvc5/cvc5/releases/download/cvc5-${CVC5_VER}/cvc5-Linux-static.zip" -O "$TEMP_DIR/cvc5.zip" -q
unzip "$TEMP_DIR/cvc5.zip" -d "$TEMP_DIR" 
CVC5_DIR=$(find "$TEMP_DIR" -mindepth 1 -maxdepth 1 -type d -name "*cvc5*")
mv "$CVC5_DIR/bin/cvc5" "$SOLVERS_DIR/cvc5"
chmod +x "$SOLVERS_DIR/cvc5"
rm -rf "$TEMP_DIR"

# CVC4
wget "https://cvc4.cs.stanford.edu/downloads/builds/x86_64-linux-opt/cvc4-${CVC4_VER}-x86_64-linux-opt" -O "$SOLVERS_DIR/cvc4" -q
chmod +x "$SOLVERS_DIR/cvc4"

# z3
mkdir -p "$TEMP_DIR"
wget "https://github.com/Z3Prover/z3/releases/download/z3-${Z3_VER}/z3-${Z3_VER}-x64-glibc-2.35.zip" -O "$TEMP_DIR/z3.zip" -q
unzip "$TEMP_DIR/z3.zip" -d "$TEMP_DIR" 
Z3_DIR=$(find "$TEMP_DIR" -mindepth 1 -maxdepth 1 -type d -name "*z3*")
mv "$Z3_DIR/bin/z3" "$SOLVERS_DIR/z3"
chmod +x "$SOLVERS_DIR/z3"
rm -rf "$TEMP_DIR"

echo "************** Solvers Installed **************"
exit 0