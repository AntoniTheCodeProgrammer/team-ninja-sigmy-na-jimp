#ifndef STRUCT_H
#define STRUCT_H

typedef struct {
    double x;
    double y;
}Cordinates;

typedef struct {
    int id;
    Cordinates position;
    Cordinates velocity;
}Point;

typedef struct{
    int vertex_a;
    int vertex_b;
    double weight;
    char* name;
}Edge;

#endif