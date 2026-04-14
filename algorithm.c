#include "algorithm.h"

void algorithm(Point *points, Edge *edges, int point_count, int edge_count){
    double temperature = 1.0;
  
    for (int i = 0; i < point_count; i++) {
        points[i].position.x = (double)rand() / RAND_MAX * 100;
        points[i].position.y = (double)rand() / RAND_MAX * 100;
    }

    for (int iter = 0; iter < 100; iter++) {
        for (int i = 0; i < point_count; i++) {
            points[i].velocity.x = 0;
            points[i].velocity.y = 0;
        }

        for (int i = 0; i < point_count; i++) {
            for (int j = 0; j < point_count; j++) {
                if (i == j) continue;

                double dx = points[i].position.x - points[j].position.x;
                double dy = points[i].position.y - points[j].position.y;
                double distance = sqrt(dx * dx + dy * dy);
                if (distance == 0.0) distance = 0.0001;

                double force = 10.0 / distance;

                points[i].velocity.x += force * dx / distance;
                points[i].velocity.y += force * dy / distance;
            }
        }

        for (int i = 0; i < edge_count; i++) {
            double dx = points[edges[i].vertex_a].position.x - points[edges[i].vertex_b].position.x;
            double dy = points[edges[i].vertex_a].position.y - points[edges[i].vertex_b].position.y;
            double distance = sqrt(dx * dx + dy * dy);
            if (distance == 0.0) distance = 0.0001;

            double force = (distance - edges[i].weight) * -0.1;

            points[edges[i].vertex_a].velocity.x += force * dx / distance;
            points[edges[i].vertex_a].velocity.y += force * dy / distance;
            points[edges[i].vertex_b].velocity.x -= force * dx / distance;
            points[edges[i].vertex_b].velocity.y -= force * dy / distance;
        }

        for (int i = 0; i < point_count; i++) {
            float v = sqrt(points[i].velocity.x * points[i].velocity.x + points[i].velocity.y * points[i].velocity.y); 
          
            if (v > temperature) {
                points[i].velocity.x = points[i].velocity.x / v * temperature;
                points[i].velocity.y = points[i].velocity.y / v * temperature;
            }
            
            points[i].position.x += points[i].velocity.x;
            points[i].position.y += points[i].velocity.y;
        }

        temperature *= 0.95;
    }
}