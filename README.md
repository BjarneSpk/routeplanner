# Routeplanner

## Prerequisites

- [make](https://www.gnu.org/software/make/)
- [JDK 21](https://openjdk.org/projects/jdk/21/)

## Usage

| Goal          | Command      |
| ------------- | ------------ |
| Compile       | `make build` |
| Run           | `make run`   |
| Run & Compile | `make all`   |
| Bench         | `make bench` |
## Example
```sh
make bench graph=/Users/bjarne/routeplanner/stgtregbz.fmi lon=48.744970 lat=9.107321 que=/Users/bjarne/Benchs/stgtregbz.que s=638394
```
