#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "import.h"

Edge* import_from_file(char* filename, int* edge_count) {
    FILE *f;
    f = fopen("filename", "r" );
    if (!f) {
        printf("Error opening file!\n");
        edge_count = 0;
        return NULL;
    }

    Edge* edges = malloc(sizeof(Edge) * 100000);  // sprawdzic czy ejst limit dlugosic w 
    int count = 0;

    char temp_name;
    int id_A, id_B;
    double weight;
    while (fscanf(f, "%s %d %d %lf", temp_name, &id_A, &id_B, &weight) == 4) {
        edges[count].name = strdup(&temp_name);
        edges[count].vertex_a = malloc(sizeof(Point));
        edges[count].vertex_b = malloc(sizeof(Point));
        edges[count].vertex_a->id = id_A;
        edges[count].vertex_b->id = id_B;
        edges[count].weight = weight;
        count++;
    }

    fclose(f);
    *edge_count = count;
    return edges;
}
