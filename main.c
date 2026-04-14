#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "struct.h"
#include "import.h"
#include "algorithm.h"
#include "extraction.h"


int main(int argc, char **argv){
    Edge *edges;
    Point *points;
    int edge_count;
    int point_count;

    if (argc < 3) {
        fprintf(stderr, "Usage: %s <input_file> <output_file> [text|binary]\n", argv[0]);
        return 1;
    }

    edges = import_from_file(argv[1], &edge_count, &point_count);
    points = malloc(sizeof(Point) * point_count);
    for (int i = 0; i < point_count; i++) points[i].id = i;

    algorithm(points, edges, point_count, edge_count);

    if (argc >= 4 && strcmp(argv[3], "text") == 0){
        save_to_text(argv[2], points, point_count);
    }
    else{
        save_to_binary(argv[2], points, point_count);
    }

    free_edges(edges, edge_count);
    free(points);
    return 0;
}