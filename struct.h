#ifndef STRUCT_H
#define STRUCT_H

struct Cordinates{
    float x;
    float y;
};

struct Vertex{
    int name;
    Cordinates position;
};

struct Egde{
    char* name;
    int vertex_a;
    int vertex_b;
    float weight;
};

#endif