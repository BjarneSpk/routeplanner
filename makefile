MEMORY_OPTS = -Xmx12g

.PHONY: all
all: build run

.PHONY: build
build:
	mvn clean compile

.PHONY: run
run:
	mvn exec:java@Main -Dexec.args="$(MEMORY_OPTS)"

.PHONY: test
test:
	mvn test
