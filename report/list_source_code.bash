#!/usr/bin/env bash
echo "" > source_files.tex
find ../core/src -name "*.java" | while read -r line ; do
echo "\lstinputlisting[title=$(basename $line)]{$line}" >> source_files.tex
done

