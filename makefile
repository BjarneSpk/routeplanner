MVN = ./mvnw

ifeq ($(OS),Windows_NT)
    MVN = mvnw.cmd
endif

.PHONY: build
build:
	$(MVN) clean compile

.PHONY: run
run:
	$(MVN) exec:java -Dexec.mainClass="de.unistuttgart.fmi.App" -Dexec.args="-graph $(graph) -lon $(lon) -lat $(lat) -que $(que) -s $(s)"

.PHONY: bench
bench:
	$(MVN) exec:java -Dexec.mainClass="de.unistuttgart.fmi.Benchmark" -Dexec.args="-graph $(graph) -lon $(lon) -lat $(lat) -que $(que) -s $(s)"
