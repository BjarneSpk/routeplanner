# Routeplanner

Routeplanner for the [FMI](https://fmi.uni-stuttgart.de) programming project in winter term 24/25.
Implements closest node search and closest path using dijkstras shortest path algorithm, using below 7GB of ram.
For examples see [here](#example).

## Prerequisites

- [make](https://www.gnu.org/software/make/)
- [JDK 21](https://openjdk.org/projects/jdk/21/)

## Usage

| Goal    | Command             |
| ------- | ------------------- |
| Compile | `make build`        |
| Run     | `make run <args>`   |
| Bench   | `make bench <args>` |

## Example

```sh
# compile with maven wrapper
make build
# run benchmark with given parameters
make bench graph=/Users/bjarne/germany.fmi lat=48.744970 lon=9.107321 que=/Users/bjarne/germany.que s=8371825
# start frontend on http://localhost:8080/
make run graph=/Users/bjarne/germany.fmi
```
