#include "../include/import.h"

Edge* import_from_file(char* filename, int* edge_count, int* point_count) {
    FILE *f;
    f = fopen(filename, "r" );
    if (!f) {
        printf("Error opening file!\n");
        *edge_count = 0;
        *point_count = 0;
        return NULL;
    }

    Edge* edges = malloc(sizeof(Edge) * 100000);  // max dlugosc 100000
    int count = 0;
    int max_id = 0;

    char temp_name[256];
    int id_A, id_B;
    double weight;
    while (fscanf(f, "%255s %d %d %lf", temp_name, &id_A, &id_B, &weight) == 4) { // max dlugosc 255 wczytywanie danych jak nie bedzie odpowiedni typ to bedzie blad 
        edges[count].name = strdup(temp_name);
        edges[count].vertex_a = id_A;
        edges[count].vertex_b = id_B;
        edges[count].weight = weight;
        count++;
        if (id_A > max_id) max_id = id_A;
        if (id_B > max_id) max_id = id_B;
    }

    
    fclose(f);
    *edge_count = count;
    *point_count = max_id + 1; // licz liczbe punktow bioroac max id jakie zczyta z pliku
    return edges; // zwraca tablice krawedzi 
}

void free_edges(Edge* edges, int edge_count) {
    if (!edges) return;
    for (int i = 0; i < edge_count; i++) {
        free(edges[i].name); // sprzatanie pamieci 
    }
    free(edges);
}
