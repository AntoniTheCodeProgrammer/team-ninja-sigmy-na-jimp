#ifndef STRUCT_H
#define STRUCT_H

typedef struct {
    double x;
    double y;
}Cordinates;

typedef struct {
    int id;
    Cordinates position;
}Point;

typedef struct{
    Point * vertex_a;
    Point * vertex_b;
    double weight;
}Edge;

#endif