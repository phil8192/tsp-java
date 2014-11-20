#!/bin/bash
# example: ./run.sh data/wi29.tsp /tmp/out.points

./pre-process.sh $1 >/tmp/tsp.points

java -Djava.library.path=target/lib \
	-server \
	-XX:+TieredCompilation \
	-XX:+AggressiveOpts \
	-Xmx2048m \
	-cp target/tsp.jar:. \
	net.parasec.tsp.TSPMain /tmp/tsp.points $2

