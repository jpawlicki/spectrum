default:
	rm -rf bin
	mkdir bin
	javac -d ./bin src/sparks/graphics/*.java src/sparks/maingame/*.java src/sparks/menus/*.java src/sparks/shared/*.java src/sparks/sound/*.java

clean:
	rm -rf bin
