# Routeplanner

Routeplanner for the [FMI](https://fmi.uni-stuttgart.de) programming project in winter term 24/25.
Implements closest node search (<= 4ms) and closest path (<= 8s) using dijkstras shortest path algorithm for a graph of germany (20m nodes, 50m edges), using below 7GB of ram. Comes with a simple web-interface using [Leaflet.js](https://leafletjs.com/) and [OpenStreetMap](https://www.openstreetmap.org/), based on AJAX.
For usage examples see [here](#example).

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
