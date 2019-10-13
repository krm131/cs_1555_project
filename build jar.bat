cd src
javac expressRailway.java
jar cfe expressRailway.jar expressRailway *.class org/* ../sql/*
move expressRailway.jar ..
del *.class