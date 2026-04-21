#ifndef IMPORT_H 
#define IMPORT_H 

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "struct.h"

Edge* import_from_file(char* filename, int* edge_count, int* point_count);
void free_edges(Edge *edges, int edge_count);

#endif