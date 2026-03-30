#include "Extraction.h"

int save_to_text(char *filename, Point *points, int count) {
    FILE *file;

    if (filename == NULL || points == NULL) {
        return 0;
    }

    file = fopen(filename, "w");
    if (file == NULL) {
        return 0;
    }

    for (int i = 0; i < count; i++) {
        if (fprintf(file, "%d %.2g %.2g\n", points[i].id, points[i].position.x, points[i].position.y) < 0) {
            fclose(file);
            return 0;
        }
    }

    if (fclose(file) != 0) {
        return 0;
    }

    return 1;
}

int save_to_binary(char *filename, Point *points, int count) {
    FILE *file;

    if (filename == NULL || points == NULL) {
        return 0;
    }

    file = fopen(filename, "wb");
    if (file == NULL) {
        return 0;
    }

    for (int i = 0; i < count; i++) {
        if (fwrite(&points[i].id, sizeof(points[i].id), 1, file) != 1 ||
            fwrite(&points[i].position.x, sizeof(points[i].position.x), 1, file) != 1 ||
            fwrite(&points[i].position.y, sizeof(points[i].position.y), 1, file) != 1) {
            fclose(file);
            return 0;
        }
    }

    if (fclose(file) != 0) {
        return 0;
    }

    return 1;
}
