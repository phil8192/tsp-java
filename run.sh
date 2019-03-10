#!/bin/bash
HEAP=8192m
IN=data/rat783.points
OUT=/tmp/rat.out
ALGO=gls_fls

# GLS parameters
MAX_RUNS=10000000
ALPHA=0.025
PENALTY_MATRIX_TYPE="array"
#PENALTY_MATRIX_TYPE="disk"
#PENALTY_MATRIX=/mnt/nvme/phil/santa.matrix
#rm -f $PENALTY_MATRIX

java -Djava.library.path=target/lib -Xmx${HEAP} -cp target/tsp.jar:. net.parasec.tsp.TSPMain ${IN} ${OUT} ${ALGO} ${MAX_RUNS} ${ALPHA} ${PENALTY_MATRIX_TYPE} ${PENALTY_MATRIX}
