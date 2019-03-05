#!/bin/bash
HEAP=8192m
#IN=data/santa.points
IN=data/santa_lkh.points
#IN=data/santa_minima_1.points
OUT=/tmp/santa.out
PENALTY_MATRIX=/mnt/nvme/phil/santa.matrix 
MAX_RUNS=1000000
INITIAL_ACTIVE=false
ALPHA=0.05
rm -f ${PENALTY_MATRIX}
java -Djava.library.path=target/lib -Xmx${HEAP} -cp target/tsp.jar:. net.parasec.tsp.santa.TSPSantaMain ${IN} ${OUT} ${PENALTY_MATRIX} ${MAX_RUNS} ${INITIAL_ACTIVE} ${ALPHA}
