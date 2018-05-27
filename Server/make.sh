#!/bin/bash

if [ -z $1 ];then
	echo "Usage: $0 <C++ SourceFile (no extension)>"
	exit
fi

if [ ! -e "$1.cpp" ]; then
	echo "Error, $1.cpp is not found."
	echo "Note: do not include file extension"
	exit
fi

if [ -e "$1" ]; then
	echo "Found executable '$1' --> copy to 'backup'"
	mv $1 backup
fi

#compile
echo "compiling ..."
g++ -g -Wall -o $1 $1.cpp $(pkg-config --cflags --libs opencv) -pthread
echo " "

if [ -e "$1" ]; then
	echo "executable '$1' is created"
fi