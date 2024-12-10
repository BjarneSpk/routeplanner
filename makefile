MVN = ./mvnw

ifeq ($(OS),Windows_NT)
    MVN = mvnw.cmd
endif

.PHONY: all
all: build run

.PHONY: build
build:
	$(MVN) clean compile

.PHONY: run
run:
	$(MVN) exec:java@Main -Dexec.args="$(MEMORY_OPTS)"

.PHONY: test
test:
	$(MVN) test

.PHONY: bench
bench:
	$(MVN) exec:java@Benchmark -Dexec.args="-graph $(graph) -lon $(lon) -lat $(lat) -que $(que) -s $(s)"
