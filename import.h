#ifndef IMPORT_H 
#define IMPORT_H 

#include "struct.h"

Edge* import_from_file(char* filename, int* edge_count);

void free_edges(Edge* edges, int edge_count);
#endif
