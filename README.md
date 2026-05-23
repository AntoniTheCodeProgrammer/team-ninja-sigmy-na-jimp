# Team Ninja Sigmy na JIMP - Graph Visualization Project

Student project for calculating and visualizing graph vertex coordinates.

The project has two main parts:

- **C command-line program** - reads a graph, runs a selected layout algorithm, and writes vertex coordinates.
- **Java Swing GUI** - visualizes graphs and coordinates, lets the user inspect and adjust the drawing, and can run the compiled C program from the interface.

Java is the visualization and interaction layer. Layout algorithms are handled by the C program.

## Project Structure

```text
.
+-- include/              C headers
+-- src/                  C source code
+-- input/                example graph input files
+-- output/               generated coordinate files
+-- user_interface/       Java Swing source files
+-- scripts/              helper scripts, including Python visualization
+-- docs/                 project documentation
+-- Makefile              C build file
+-- README.md
```

Important Java note: source files are currently stored in `user_interface/`, but their package name is `etap2_java`. Compile Java with `javac -d . ...` so class files are generated in the correct package directory.

## Requirements

### C

- `gcc`
- `make` is optional
- standard math library `-lm`

### Java

- JDK with `javac` and `java`
- Java Swing, included in the standard JDK

### Optional Python helper

- Python 3
- `matplotlib`, only for `scripts/visualize.py`

## Input Format

Graph input is a text edge list. Each non-empty line has:

```text
<edge_name> <vertex_A> <vertex_B> <weight>
```

Example:

```text
AB 1 2 1.0
BC 2 3 2.5
CD 3 4 7.0
```

Vertex identifiers used by the C part are integers. Java stores vertex IDs as strings, but it can load IDs written by the C program because they are read textually.

## Coordinate Output Format

Text coordinate output:

```text
<vertex_id> <x> <y>
```

Example:

```text
1 10.0 20.0
2 40.0 20.0
3 25.0 60.0
```

The C program can also write binary coordinate files. The binary format is repeated records of:

```text
int id
double x
double y
```

This binary coordinate format is platform-dependent and has no header or version field. TXT is recommended for normal Java integration.

There is no binary graph input format in this project.

## C Program

The C program is controlled from the command line:

```text
program <input_file> <output_file> <mode> <algorithm>
```

Arguments:

- `input_file` - graph edge list file
- `output_file` - coordinate output file
- `mode` - `text` or `binary`
- `algorithm`:
  - `1` - force-directed layout
  - `2` - Barycentric Tutte-style layout

If an input or output file name does not contain a path separator, the C program uses:

- `input/<input_file>`
- `output/<output_file>`

### Build C

Using Make:

```bash
make
```

Direct GCC command:

```bash
gcc -Wall -Wextra -Iinclude -o program src/main.c src/import.c src/algorithm.c src/extraction.c -lm
```

On Windows with GCC, the output may be `program.exe`.

### Run C Examples

Windows PowerShell:

```powershell
.\program.exe in_file.txt coords_force.txt text 1
.\program.exe in_file.txt coords_tutte.txt text 2
```

Linux/macOS:

```bash
./program in_file.txt coords_force.txt text 1
./program in_file.txt coords_tutte.txt text 2
```

The generated coordinate files will be written to `output/` when only a file name is passed.

## C Algorithms

### Algorithm 1: Force-directed layout

A simple force-directed layout inspired by Fruchterman-Reingold / Eades ideas. Vertices repel each other, while connected vertices attract each other through edges.

This algorithm is useful for generally readable graph drawings, but it does not guarantee planar drawings.

### Algorithm 2: Barycentric Tutte-style layout

A simplified planar-inspired layout:

- selects boundary vertices,
- fixes them on an outer polygon,
- initializes inner vertices inside the boundary,
- repeatedly moves non-boundary vertices toward neighbor averages.

This is a student-friendly approximation of a barycentric/Tutte-style idea. It is not a complete planarity test and does not guarantee a crossing-free drawing for every graph.

## Java Swing Application

The Java application is a GUI viewer for graph structures and coordinates.

Main features:

- load graph TXT files,
- load coordinate TXT files,
- optionally load binary coordinate files created by the C program,
- save current coordinates as TXT,
- run C algorithm 1 from the GUI,
- run C algorithm 2 from the GUI,
- zoom with mouse wheel,
- pan by dragging empty background,
- reset view to fit the graph,
- show or hide vertex labels,
- show or hide edge weights,
- drag vertices to manually change coordinates.

Java does not compute graph layouts itself in the active GUI flow. The old Java layout classes, if present, are not used by the user-facing interface.

### Compile Java

Because Java files are stored in `user_interface/` but use package `etap2_java`, compile with `-d .` from the project root:

Windows PowerShell:

```powershell
javac -d . user_interface\*.java
```

Linux/macOS:

```bash
javac -d . user_interface/*.java
```

### Run Java GUI

```bash
java etap2_java.Main
```

On Windows PowerShell the same command works:

```powershell
java etap2_java.Main
```

### Running C Algorithms from Java

The Java GUI can call the compiled C executable with `ProcessBuilder`.

Before using the layout buttons in Java, build the C program first:

```bash
make
```

or:

```bash
gcc -Wall -Wextra -Iinclude -o program src/main.c src/import.c src/algorithm.c src/extraction.c -lm
```

In the GUI:

- `Force-directed layout` runs C algorithm `1`.
- `Barycentric Tutte-style Layout` runs C algorithm `2`.

Java writes and reloads generated coordinates through:

```text
output/java_generated_coords.txt
```

If the C executable is missing, the GUI shows a message asking the user to build the C program first.

## Typical Workflow

1. Build the C program.
2. Compile the Java GUI.
3. Run the Java GUI.
4. Load a graph TXT file from `input/`.
5. Run `Force-directed layout` or `Barycentric Tutte-style Layout`, or load an existing coordinate file.
6. Inspect the graph.
7. Use zoom, pan, reset view, labels, and weights.
8. Drag vertices if manual correction is needed.
9. Save final coordinates to TXT.

## Python Visualization Helper

The script `scripts/visualize.py` can generate a static image from a graph input file and a coordinate output file:

```bash
python scripts/visualize.py in_file.txt coords_force.txt
```

This script is optional. The main GUI visualization is the Java Swing application.

## Documentation

Additional documentation is in `docs/`:

- `docs/C/` - C part documentation
- `docs/java/` - Java part documentation

## Generated Files

The following files are generated during normal work and should not usually be committed:

- Java `.class` files
- `program` / `program.exe`
- object files such as `*.o`
- temporary coordinate outputs in `output/`
- generated images such as `*.png`

## Limitations

- The C import currently works with integer vertex IDs.
- Binary coordinate files are platform-dependent.
- Java does not load binary graph files.
- Algorithm 2 is planar-inspired but does not guarantee planar output.
- Java algorithm buttons require a compiled C executable.

## Quick Command Summary

Build C:

```bash
make
```

or:

```bash
gcc -Wall -Wextra -Iinclude -o program src/main.c src/import.c src/algorithm.c src/extraction.c -lm
```

Run C algorithm 1:

```bash
./program in_file.txt coords_force.txt text 1
```

Run C algorithm 2:

```bash
./program in_file.txt coords_tutte.txt text 2
```

Compile Java:

```bash
javac -d . user_interface/*.java
```

Run Java:

```bash
java etap2_java.Main
```
