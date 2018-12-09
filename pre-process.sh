#!/bin/bash
# convert tsplib format file to simple point format.
# id's offset by -1 so tour starts at 0.
cat $1 |grep "^[0-9]" |awk '{print $1-1 " " $2 " " $3}'
