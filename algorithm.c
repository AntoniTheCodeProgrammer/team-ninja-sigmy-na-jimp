#include "algorithm.h"

void algorithm(Point *points, Edge *edges, int point_count, int edge_count) {
    // Siła zmiany
    double temperature = 1.0;
    Point *velocities = malloc(sizeof(Point) * point_count);

    // losowe rozmieszczenie punktów
    for (int i = 0; i < point_count; i++) {
        points[i].position.x = (double)rand() / RAND_MAX * 100;
        points[i].position.y = (double)rand() / RAND_MAX * 100;
    }

    // iteracje
    for (int iter = 0; iter < 100; iter++) {
        // zerowanie prędkości
        for (int i = 0; i < point_count; i++) {
            velocities[i].position.x = 0;
            velocities[i].position.y = 0;
        }

        // siły odpychania
        for (int i = 0; i < point_count; i++) {
            for (int j = 0; j < point_count; j++) {
                if (i == j) continue;

                double dx = points[i].position.x - points[j].position.x;
                double dy = points[i].position.y - points[j].position.y;
                // odległość ponktow od siebie
                double distance = sqrt(dx * dx + dy * dy);
                if (distance == 0.0) distance = 0.0001;

                // siła odpychania
                double force = 10.0 / distance;

                // zmiana wektora siły
                velocities[i].position.x += force * dx / distance;
                velocities[i].position.y += force * dy / distance;
            }
        }

        // siły przyciągania
        for (int i = 0; i < edge_count; i++) {
            double dx = points[edges[i].vertex_a].position.x - points[edges[i].vertex_b].position.x;
            double dy = points[edges[i].vertex_a].position.y -
                        points[edges[i].vertex_b].position.y;

            // odległość ponktow od siebie
            double distance = sqrt(dx * dx + dy * dy);
            if (distance == 0.0) distance = 0.0001;

            // siła przyciągania
            double force = (distance - edges[i].weight) * -0.1;

            // zmiana wektora siły
            velocities[edges[i].vertex_a].position.x += force * dx / distance;
            velocities[edges[i].vertex_a].position.y += force * dy / distance;
            velocities[edges[i].vertex_b].position.x -= force * dx / distance;
            velocities[edges[i].vertex_b].position.y -= force * dy / distance;
        }

        // aktualizacja pozycji punktów
        for (int i = 0; i < point_count; i++) {
            // prędkość punktu
            float v = sqrt(velocities[i].position.x * velocities[i].position.x + velocities[i].position.y * velocities[i].position.y); 
          
            // ograniczenie prędkości
            if (v > temperature) {
                velocities[i].position.x = velocities[i].position.x / v * temperature;
                velocities[i].position.y = velocities[i].position.y / v * temperature;
            }
            
            // aktualizacja pozycji punktu
            points[i].position.x += velocities[i].position.x;
            points[i].position.y += velocities[i].position.y;
        }

        // zmniejszanie siły zmiany
        temperature *= 0.95;
    }
}