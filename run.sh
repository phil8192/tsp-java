#!/bin/bash
# example: ./run.sh data/gr9882.tsp /tmp/out.points gls_fls

./pre-process.sh $1 >/tmp/tsp.points

java -Djava.library.path=target/lib \
	-server \
	-XX:+TieredCompilation \
	-XX:+AggressiveOpts \
	-Xmx8192m \
	-cp target/tsp.jar:. \
	net.parasec.tsp.TSPMain /tmp/tsp.points $2 $3
