all: natives

java: target
	javac src/main/java/net/parasec/tsp/impl/*.java \
		src/main/java/net/parasec/tsp/*.java \
		-d target/classes
	jar -cf target/tsp.jar -C target/classes net

natives: target java
	javah -cp target/classes -o target/lib/maths.h \
		net.parasec.tsp.impl.Maths
	gcc -O3 -march=native -Itarget/lib \
		-o target/lib/libmaths.so -shared -fPIC \
		src/main/c/maths.c 

target:
	mkdir -p target/classes
	mkdir -p target/lib

clean:
	rm -rf target

