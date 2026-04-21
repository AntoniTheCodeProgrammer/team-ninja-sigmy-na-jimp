#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../include/struct.h"
#include "../include/import.h"
#include "../include/algorithm.h"
#include "../include/extraction.h"


int main(int argc, char **argv){
    // zmienne
    Edge *edges;
    Point *points;
    int edge_count;
    int point_count;

    // sprawdzanie czy podano pliki
    if (argc < 3) {
        fprintf(stderr, "Usage: %s <input_file> <output_file> [text|binary]\n", argv[0]);
        return 1;
    }

    // sprawdzanie czy z podanej sciezki odczytac czy dodac foldery
    char input_path[512];
    if (strchr(argv[1], '/') == NULL) {
        snprintf(input_path, sizeof(input_path), "input/%s", argv[1]);
    } else {
        strncpy(input_path, argv[1], sizeof(input_path));
    }

    char output_path[512];
    if (strchr(argv[2], '/') == NULL) {
        snprintf(output_path, sizeof(output_path), "output/%s", argv[2]);
    } else {
        strncpy(output_path, argv[2], sizeof(output_path));
    }

    // importowanie danych
    edges = import_from_file(input_path, &edge_count, &point_count);
    if (!edges) {
        // Obliczanie bledu lub brak pliku
        return 1;
    }
    
    points = malloc(sizeof(Point) * point_count);
    for (int i = 0; i < point_count; i++) points[i].id = i;

    // algorytm
    algorithm(points, edges, point_count, edge_count);

    // zapisywanie danych
    if (argc >= 4 && strcmp(argv[3], "text") == 0){
        save_to_text(output_path, points, point_count);
    }
    else{
        save_to_binary(output_path, points, point_count);
    }

    // zwalnianie pamięci
    free_edges(edges, edge_count);
    free(points);

    return 0;
}