all: lib bin

lib: lib/drawerControllib.jar lib/libdrawerControl.so

bin: bin/drawerController

cleanup:
	rm -f src/drawerControllib/DrawerControl.class src/drawerControllib/DrawerControlJNI.class src/drawerControllib/Status.class src/drawerControllib/Status\$$SwigNext.class src/drawerControllib/DrawerControl.java src/drawerControllib/DrawerControlJNI.java src/drawerControllib/Status.java src/DrawerControl_wrap.c src/DrawerControl.o src/DrawerControl_wrap.o
	rmdir src/drawerControllib

lib/drawerControllib.jar: src/drawerControllib/DrawerControl.class src/drawerControllib/DrawerControlJNI.class src/drawerControllib/Status.class src/drawerControllib/Status$$SwigNext.class
	mkdir -p lib
	cd src;\
	jar cf drawerControllib.jar drawerControllib/DrawerControl.class drawerControllib/DrawerControlJNI.class drawerControllib/Status.class drawerControllib/Status\$$SwigNext.class
	mv src/drawerControllib.jar lib/drawerControllib.jar 

src/drawerControllib/DrawerControl.class: src/drawerControllib/DrawerControl.java
	cd src;\
	javac drawerControllib/DrawerControl.java

src/drawerControllib/DrawerControlJNI.class: src/drawerControllib/DrawerControlJNI.java
	cd src;\
	javac drawerControllib/DrawerControlJNI.java

src/drawerControllib/Status.class src/drawerControllib/Status$$SwigNext.class: src/drawerControllib/Status.java
	cd src;\
	javac drawerControllib/Status.java

src/drawerControllib/DrawerControl.java src/drawerControllib/DrawerControlJNI.java src/drawerControllib/Status.java src/DrawerControl_wrap.c: src/DrawerControl.i src/DrawerControl.c
	cd src;\
	mkdir -p drawerControllib;\
	swig -java -package drawerControllib -outdir drawerControllib DrawerControl.i

src/DrawerControl.i src/DrawerControl.c: src/DrawerControl.h

src/DrawerControl.o: src/DrawerControl.c
	gcc -c src/DrawerControl.c -o src/DrawerControl.o

src/DrawerControl_wrap.o: src/DrawerControl_wrap.c
	gcc -c src/DrawerControl_wrap.c -I/usr/lib/jvm/default-java/include -o src/DrawerControl_wrap.o

lib/libdrawerControl.so: src/DrawerControl.o src/DrawerControl_wrap.o
	mkdir -p lib
	gcc -shared src/DrawerControl.o src/DrawerControl_wrap.o -lhid -o lib/libdrawerControl.so

bin/drawerController: src/DrawerControl.c 
	mkdir -p bin
	gcc src/DrawerControl.c -lhid -o bin/drawerController



