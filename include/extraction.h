#ifndef EXTRACTION_H
#define EXTRACTION_H

#include "struct.h"
#include <stdio.h>

int save_to_text(char *filename, Point *points, int count);
int save_to_binary(char *filename, Point *points, int count);

#endif
