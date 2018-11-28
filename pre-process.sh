#!/bin/bash
# convert tsplib format file to simple point format.

cat $1 |grep "^[0-9]" |awk '{print $1 " " $2 " " $3}'

