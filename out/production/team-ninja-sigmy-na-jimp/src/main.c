#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../include/struct.h"
#include "../include/import.h"
#include "../include/algorithm.h"
#include "../include/extraction.h"

static void print_usage(const char *program_name) {
    fprintf(stderr, "Usage: %s <input_file> <output_file> <mode> <algorithm>\n", program_name);
    fprintf(stderr, "mode: text or binary\n");
    fprintf(stderr, "algorithm:\n");
    fprintf(stderr, "  1 - force-directed layout\n");
    fprintf(stderr, "  2 - planar-friendly barycentric layout\n");
}

int main(int argc, char **argv) {
    Edge *edges = NULL;
    Point *points = NULL;
    int edge_count = 0;
    int point_count = 0;
    int algorithm_id;
    char *endptr;

    // C owns algorithm selection. Java can call this executable with argument 1 or 2.
    if (argc != 5) {
        print_usage(argv[0]);
        return 1;
    }

    if (strcmp(argv[3], "text") != 0 && strcmp(argv[3], "binary") != 0) {
        print_usage(argv[0]);
        return 1;
    }

    algorithm_id = (int)strtol(argv[4], &endptr, 10);
    if (*endptr != '\0' || (algorithm_id != 1 && algorithm_id != 2)) {
        print_usage(argv[0]);
        return 1;
    }

    char input_path[512];
    if (strchr(argv[1], '/') == NULL && strchr(argv[1], '\\') == NULL) {
        snprintf(input_path, sizeof(input_path), "input/%s", argv[1]);
    } else {
        snprintf(input_path, sizeof(input_path), "%s", argv[1]);
    }

    char output_path[512];
    if (strchr(argv[2], '/') == NULL && strchr(argv[2], '\\') == NULL) {
        snprintf(output_path, sizeof(output_path), "output/%s", argv[2]);
    } else {
        snprintf(output_path, sizeof(output_path), "%s", argv[2]);
    }

    edges = import_from_file(input_path, &edge_count, &point_count);
    if (!edges) {
        return 1;
    }

    points = malloc(sizeof(Point) * point_count);
    if (!points) {
        fprintf(stderr, "Cannot allocate memory for points.\n");
        free_edges(edges, edge_count);
        return 1;
    }

    for (int i = 0; i < point_count; i++) {
        points[i].id = i;
    }

    if (algorithm(points, edges, point_count, edge_count, algorithm_id) != 0) {
        free_edges(edges, edge_count);
        free(points);
        return 1;
    }

    // Text output remains compatible with Java: <vertex_id> <x> <y>.
    int save_result;
    if (strcmp(argv[3], "text") == 0) {
        save_result = save_to_text(output_path, points, point_count);
    } else {
        save_result = save_to_binary(output_path, points, point_count);
    }

    if (save_result != 0) {
        fprintf(stderr, "Error writing output file: %s\n", output_path);
        free_edges(edges, edge_count);
        free(points);
        return 1;
    }

    free_edges(edges, edge_count);
    free(points);

    return 0;
}
