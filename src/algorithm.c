#include "../include/algorithm.h"

#define CENTER_X 50.0
#define CENTER_Y 50.0
#define OUTER_RADIUS 40.0
#define INNER_RADIUS 24.0
#define PI 3.14159265358979323846

static int valid_edge(Edge edge, int point_count) {
    return edge.vertex_a >= 0 && edge.vertex_a < point_count &&
           edge.vertex_b >= 0 && edge.vertex_b < point_count &&
           edge.vertex_a != edge.vertex_b;
}

static int force_directed_layout(Point *points, Edge *edges, int point_count, int edge_count) {
    double temperature = 1.0;
    Point *velocity = malloc(sizeof(Point) * point_count);

    if (velocity == NULL) {
        fprintf(stderr, "Cannot allocate memory for force-directed layout.\n");
        return 1;
    }

    // Algorithm 1: the original simple force-directed layout.
    for (int i = 0; i < point_count; i++) {
        points[i].position.x = (double)rand() / RAND_MAX * 100.0;
        points[i].position.y = (double)rand() / RAND_MAX * 100.0;
    }

    for (int iter = 0; iter < 100; iter++) {
        for (int i = 0; i < point_count; i++) {
            velocity[i].position.x = 0.0;
            velocity[i].position.y = 0.0;
        }

        for (int i = 0; i < point_count; i++) {
            for (int j = 0; j < point_count; j++) {
                if (i == j) continue;

                double dx = points[i].position.x - points[j].position.x;
                double dy = points[i].position.y - points[j].position.y;
                double distance = sqrt(dx * dx + dy * dy);
                if (distance == 0.0) distance = 0.0001;

                double force = 10.0 / distance;
                velocity[i].position.x += force * dx / distance;
                velocity[i].position.y += force * dy / distance;
            }
        }

        for (int i = 0; i < edge_count; i++) {
            if (!valid_edge(edges[i], point_count)) continue;

            int a = edges[i].vertex_a;
            int b = edges[i].vertex_b;
            double dx = points[a].position.x - points[b].position.x;
            double dy = points[a].position.y - points[b].position.y;
            double distance = sqrt(dx * dx + dy * dy);
            if (distance == 0.0) distance = 0.0001;

            double force = (distance - edges[i].weight) * -0.1;
            velocity[a].position.x += force * dx / distance;
            velocity[a].position.y += force * dy / distance;
            velocity[b].position.x -= force * dx / distance;
            velocity[b].position.y -= force * dy / distance;
        }

        for (int i = 0; i < point_count; i++) {
            double speed = sqrt(velocity[i].position.x * velocity[i].position.x +
                                velocity[i].position.y * velocity[i].position.y);

            if (speed > temperature) {
                velocity[i].position.x = velocity[i].position.x / speed * temperature;
                velocity[i].position.y = velocity[i].position.y / speed * temperature;
            }

            points[i].position.x += velocity[i].position.x;
            points[i].position.y += velocity[i].position.y;
        }

        temperature *= 0.95;
    }

    free(velocity);
    return 0;
}

static int fill_degrees(Edge *edges, int point_count, int edge_count, int *degree, int *active_count) {
    int valid_edges = 0;

    for (int i = 0; i < edge_count; i++) {
        if (!valid_edge(edges[i], point_count)) continue;

        degree[edges[i].vertex_a]++;
        degree[edges[i].vertex_b]++;
        valid_edges++;
    }

    for (int i = 0; i < point_count; i++) {
        if (degree[i] > 0) (*active_count)++;
    }

    return valid_edges;
}

static int choose_boundary_vertices(int *degree, int point_count, int active_count, int *fixed) {
    int boundary_count;

    if (active_count <= 4) {
        boundary_count = active_count;
    } else {
        boundary_count = (int)(sqrt((double)active_count) * 2.0);
        if (boundary_count < 4) boundary_count = 4;
        if (boundary_count > active_count) boundary_count = active_count;
    }

    // Pick boundary vertices spread across IDs. This is simple and gives a wider frame
    // than taking only the first few vertices.
    for (int k = 0; k < boundary_count; k++) {
        int target_rank = boundary_count == 1 ? 0 : k * (active_count - 1) / (boundary_count - 1);
        int rank = 0;

        for (int i = 0; i < point_count; i++) {
            if (degree[i] == 0) continue;
            if (rank == target_rank) {
                fixed[i] = 1;
                break;
            }
            rank++;
        }
    }

    return boundary_count;
}

static void place_initial_positions(Point *points, int point_count, int *degree, int *fixed) {
    for (int i = 0; i < point_count; i++) {
        if (fixed[i] || degree[i] == 0) continue;

        double angle = 2.0 * PI * i / (point_count == 0 ? 1 : point_count);
        double radius = 8.0 + (i % 5) * (INNER_RADIUS / 5.0);
        points[i].position.x = CENTER_X + radius * cos(angle);
        points[i].position.y = CENTER_Y + radius * sin(angle);
    }
}

static void place_boundary(Point *points, int point_count, int *fixed, int boundary_count) {
    int placed = 0;

    if (boundary_count == 0) return;

    // Algorithm 2 fixes only selected boundary vertices on a convex outer polygon.
    for (int i = 0; i < point_count; i++) {
        if (!fixed[i]) continue;

        double angle = 2.0 * PI * placed / boundary_count;
        points[i].position.x = CENTER_X + OUTER_RADIUS * cos(angle);
        points[i].position.y = CENTER_Y + OUTER_RADIUS * sin(angle);
        placed++;
    }
}

static void place_isolated_vertices(Point *points, int point_count, int *degree) {
    int isolated = 0;

    for (int i = 0; i < point_count; i++) {
        if (degree[i] != 0) continue;

        points[i].position.x = 10.0 + (isolated % 8) * 10.0;
        points[i].position.y = 95.0 + (isolated / 8) * 10.0;
        isolated++;
    }
}

static int barycentric_layout(Point *points, Edge *edges, int point_count, int edge_count) {
    int *degree = calloc(point_count, sizeof(int));
    int *fixed = calloc(point_count, sizeof(int));
    double *sum_x = calloc(point_count, sizeof(double));
    double *sum_y = calloc(point_count, sizeof(double));
    int active_count = 0;

    if (degree == NULL || fixed == NULL || sum_x == NULL || sum_y == NULL) {
        fprintf(stderr, "Cannot allocate memory for planar-friendly layout.\n");
        free(degree);
        free(fixed);
        free(sum_x);
        free(sum_y);
        return 1;
    }

    int valid_edges = fill_degrees(edges, point_count, edge_count, degree, &active_count);

    fprintf(stderr,
            "Barycentric Tutte-style layout: simplified planar-inspired layout; "
            "crossing-free drawings are not guaranteed.\n");

    // This Euler check can only detect some definitely non-planar simple graphs.
    if (active_count >= 3 && valid_edges > 3 * active_count - 6) {
        fprintf(stderr,
                "Warning: m > 3n - 6, so a simple version of this graph cannot be planar.\n");
    }
    if (active_count > 0 && valid_edges < active_count - 1) {
        fprintf(stderr,
                "Warning: graph may be disconnected; the layout works best on one component.\n");
    }

    int boundary_count = choose_boundary_vertices(degree, point_count, active_count, fixed);
    place_initial_positions(points, point_count, degree, fixed);
    place_boundary(points, point_count, fixed, boundary_count);

    // Non-boundary vertices move toward neighbor averages, but relaxation prevents
    // them from collapsing as aggressively as pure averaging.
    for (int iter = 0; iter < 180; iter++) {
        for (int i = 0; i < point_count; i++) {
            sum_x[i] = 0.0;
            sum_y[i] = 0.0;
        }

        for (int i = 0; i < edge_count; i++) {
            if (!valid_edge(edges[i], point_count)) continue;

            int a = edges[i].vertex_a;
            int b = edges[i].vertex_b;

            sum_x[a] += points[b].position.x;
            sum_y[a] += points[b].position.y;

            sum_x[b] += points[a].position.x;
            sum_y[b] += points[a].position.y;
        }

        for (int i = 0; i < point_count; i++) {
            if (fixed[i] || degree[i] == 0) continue;

            double avg_x = sum_x[i] / degree[i];
            double avg_y = sum_y[i] / degree[i];
            points[i].position.x = points[i].position.x * 0.4 + avg_x * 0.6;
            points[i].position.y = points[i].position.y * 0.4 + avg_y * 0.6;
        }
    }

    place_isolated_vertices(points, point_count, degree);

    free(degree);
    free(fixed);
    free(sum_x);
    free(sum_y);
    return 0;
}

int algorithm(Point *points, Edge *edges, int point_count, int edge_count, int algorithm_id) {
    if (points == NULL || edges == NULL || point_count <= 0) {
        fprintf(stderr, "Algorithm input is invalid.\n");
        return 1;
    }

    if (algorithm_id == 1) {
        return force_directed_layout(points, edges, point_count, edge_count);
    }
    if (algorithm_id == 2) {
        return barycentric_layout(points, edges, point_count, edge_count);
    }

    fprintf(stderr, "Unknown algorithm id: %d\n", algorithm_id);
    return 1;
}
