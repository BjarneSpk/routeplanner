# Routeplanner

Routeplanner for the [FMI](https://fmi.uni-stuttgart.de) programming project in winter term 24/25.
Implements closest node search and closest path using dijkstras shortest path algorithm, using below 7GB of ram.
For grading see [here](#grading).

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
make bench graph=/Users/bjarne/routeplanner/stgtregbz.fmi lon=48.744970 lat=9.107321 que=/Users/bjarne/Benchs/stgtregbz.que s=638394
```

## Grading

```sh
make build
make bench graph=/Users/bjarne/germany.fmi lon=48.744970 lat=9.107321 que=/Users/bjarne/germany.que s=8371825
```

