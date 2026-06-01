#include "../include/extraction.h"

int save_to_text(char *filename, Point *points, int count) {
    FILE *file;

    if (filename == NULL || points == NULL) { // sprawdza czy puste
        return 1;
    }

    file = fopen(filename, "w");
    if (file == NULL) { // czy sie otworzyło
        return 1;
    }

    for (int i = 0; i < count; i++) {
        if (fprintf(file, "%d %.2g %.2g\n", points[i].id, points[i].position.x, points[i].position.y) < 0) { //jezeli mniejsze od 0 to mogło zabraknąć miejsca lub brakuje uprawnień
            fclose(file);
            return 1;
        }
    }

    if (fclose(file) != 0) {
        return 1;
    }

    return 0;
}

int save_to_binary(char *filename, Point *points, int count) {
    FILE *file;

    if (filename == NULL || points == NULL) { //sprawdzenie czy nie są puste
        return 1;
    }

    file = fopen(filename, "wb");
    if (file == NULL) { //czy napewno sie otworzyło
        return 1;
    }

    for (int i = 0; i < count; i++) { // lokalizacjza pointera, rozmiar, ilość elementó, plik
        if (fwrite(&points[i].id, sizeof(points[i].id), 1, file) != 1 ||
            fwrite(&points[i].position.x, sizeof(points[i].position.x), 1, file) != 1 ||
            fwrite(&points[i].position.y, sizeof(points[i].position.y), 1, file) != 1) {
            fclose(file);
            return 1;
        }
    }

    if (fclose(file) != 0) {
        return 1;
    }

    return 0;
}
