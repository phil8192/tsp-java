#!/bin/bash
HEAP=8192m
IN=data/rat783.points
OUT=/tmp/rat.out
ALGO=gls_fls
MAX_RUNS=1000000
ALPHA=0.025
java -Djava.library.path=target/lib -Xmx${HEAP} -cp target/tsp.jar:. net.parasec.tsp.TSPMain ${IN} ${OUT} ${ALGO} ${MAX_RUNS} ${ALPHA}
