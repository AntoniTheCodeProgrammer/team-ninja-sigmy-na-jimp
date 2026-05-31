#include "../include/algorithm.h"

#define CENTER_X 50.0
#define CENTER_Y 50.0
#define OUTER_RADIUS 40.0
#define INNER_RADIUS 24.0
#define PI 3.14159265358979323846

// ZAPIS ITERACJI
void save_iteration(FILE *file, Point *points, int num_nodes) {
    if (file == NULL) return;
    fprintf(file, "---STEP---\n");
    for (int i = 0; i < num_nodes; i++) {
        fprintf(file, "V %d %f %f\n", i, points[i].position.x, points[i].position.y);
    }
    fflush(file);

    }

// Funkcja sprawdzająca czy krawędź jest poprawna.
// Zwraca 1 (prawda), jeśli wierzchołki mieszczą się w zakresie i nie są tym samym wierzchołkiem.
static int valid_edge(Edge edge, int point_count) {
    return edge.vertex_a >= 0 && edge.vertex_a < point_count &&
           edge.vertex_b >= 0 && edge.vertex_b < point_count &&
           edge.vertex_a != edge.vertex_b;
}

// Implementacja algorytmu siłowego (Force-directed layout).
// Używana do estetycznego rozmieszczania grafów poprzez symulację sił fizycznych
// (odpychanie wierzchołków i przyciąganie przez krawędzie).
static int force_directed_layout(Point *points, Edge *edges, int point_count, int edge_count, FILE *iter_file) {
    double temperature = 1.0; // Początkowa "temperatura" kontrolująca maksymalną prędkość punktów
    Point *velocity = malloc(sizeof(Point) * point_count); // Tablica prędkości wierzchołków

    if (velocity == NULL) {
        // Zgłoszenie błędu na wyjściu błędów, jeśli brakuje pamięci operacyjnej
        fprintf(stderr, "Nie można zaalokować pamięci dla układu siłowego.\n");
        return 1; // Zwrócenie 1 oznacza błąd wykonania
    }

    // Losowe rozmieszczenie początkowe punktów na płaszczyźnie 100x100
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

                // Obliczanie wektora między dwoma wierzchołkami
                double dx = points[i].position.x - points[j].position.x;
                double dy = points[i].position.y - points[j].position.y;
                double distance = sqrt(dx * dx + dy * dy); // Odległość między wierzchołkami
                if (distance == 0.0) distance = 0.0001; // Zabezpieczenie przed dzieleniem przez 0

                // Siła odpychająca między wszystkimi wierzchołkami (jak ładunki elektryczne o tym samym znaku)
                double force = 10.0 / distance;
                velocity[i].position.x += force * dx / distance;
                velocity[i].position.y += force * dy / distance;
            }
        }

        for (int i = 0; i < edge_count; i++) {
            if (!valid_edge(edges[i], point_count)) continue;

            int a = edges[i].vertex_a;
            int b = edges[i].vertex_b;
            
            // Obliczanie wektora dla połączonych wierzchołków
            double dx = points[a].position.x - points[b].position.x;
            double dy = points[a].position.y - points[b].position.y;
            double distance = sqrt(dx * dx + dy * dy);
            if (distance == 0.0) distance = 0.0001;

            // Siła przyciągająca działająca wzdłuż krawędzi (jak sprężyna Hooke'a)
            // Uwzględnia docelową długość krawędzi określoną przez weight
            double force = (distance - edges[i].weight) * -0.1;
            velocity[a].position.x += force * dx / distance;
            velocity[a].position.y += force * dy / distance;
            velocity[b].position.x -= force * dx / distance;
            velocity[b].position.y -= force * dy / distance;
        }

        for (int i = 0; i < point_count; i++) {
            // Obliczenie aktualnej szybkości punktu
            double speed = sqrt(velocity[i].position.x * velocity[i].position.x +
                                velocity[i].position.y * velocity[i].position.y);

            // Ograniczenie prędkości temperaturą, aby zapobiec chaotycznym ruchom i ucieczce wierzchołków
            if (speed > temperature) {
                velocity[i].position.x = velocity[i].position.x / speed * temperature;
                velocity[i].position.y = velocity[i].position.y / speed * temperature;
            }

            // Zaktualizowanie pozycji wierzchołka
            points[i].position.x += velocity[i].position.x;
            points[i].position.y += velocity[i].position.y;
        }

        temperature *= 0.95; // Stopniowe "chłodzenie" (Simulated Annealing) - zmniejszanie temperatury z każdą iteracją


        // ZAPIS ITERACJI DO PLIKU
        save_iteration(iter_file, points, point_count);
    }

    free(velocity);
    return 0;
}

// Zlicza stopnie wierzchołków (liczbę incydentnych krawędzi) i określa, 
// ile z nich jest "aktywnych" (ma co najmniej jedną krawędź).
static int fill_degrees(Edge *edges, int point_count, int edge_count, int *degree, int *active_count) {
    int valid_edges = 0;

    for (int i = 0; i < edge_count; i++) {
        if (!valid_edge(edges[i], point_count)) continue;

        degree[edges[i].vertex_a]++;
        degree[edges[i].vertex_b]++;
        valid_edges++;
    }

    // Liczenie wierzchołków aktywnych - takich, które mają co najmniej jedną krawędź
    for (int i = 0; i < point_count; i++) {
        if (degree[i] > 0) (*active_count)++;
    }

    return valid_edges; // Zwracamy liczbę poprawnych krawędzi

}

// Wybiera wierzchołki, które zostaną "przytwierdzone" do zewnętrznej ramki. 
// Używane w układzie barycentrycznym jako kotwice dla reszty grafu.
static int choose_boundary_vertices(int *degree, int point_count, int active_count, int *fixed) {
    int boundary_count;

    if (active_count <= 4) {
        boundary_count = active_count;
    } else {
        boundary_count = (int)(sqrt((double)active_count) * 2.0);
        if (boundary_count < 4) boundary_count = 4;
        if (boundary_count > active_count) boundary_count = active_count;
    }

    // Wybieramy wierzchołki brzegowe równomiernie rozłożone względem ich ID. 
    // Jest to proste podejście, które zazwyczaj daje szerszą i ładniejszą ramkę
    // niż wybieranie tylko pierwszych z brzegu (np. pierwszych k wierzchołków).
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

// Rozmieszcza wierzchołki początkowe (nieprzytwierdzone) w koncentrycznych okręgach.
static void place_initial_positions(Point *points, int point_count, int *degree, int *fixed) {
    for (int i = 0; i < point_count; i++) {
        if (fixed[i] || degree[i] == 0) continue;

        double angle = 2.0 * PI * i / (point_count == 0 ? 1 : point_count);
        double radius = 8.0 + (i % 5) * (INNER_RADIUS / 5.0);
        points[i].position.x = CENTER_X + radius * cos(angle);
        points[i].position.y = CENTER_Y + radius * sin(angle);
    }
}

// Rozmieszcza wybrane wierzchołki brzegowe na zewnętrznym okręgu (tworząc równomierną wielokątną ramkę).
static void place_boundary(Point *points, int point_count, int *fixed, int boundary_count) {
    int placed = 0;

    if (boundary_count == 0) return;

    // Algorytm barycentryczny ustala na sztywno tylko wybrane 
    // wierzchołki brzegowe, układając je na zewnętrznym wielokącie (okręgu).
    for (int i = 0; i < point_count; i++) {
        if (!fixed[i]) continue;

        double angle = 2.0 * PI * placed / boundary_count;
        points[i].position.x = CENTER_X + OUTER_RADIUS * cos(angle);
        points[i].position.y = CENTER_Y + OUTER_RADIUS * sin(angle);
        placed++;
    }
}

// Umieszcza izolowane wierzchołki (bez krawędzi) w uporządkowanej siatce poza głównym rysunkiem grafu.
static void place_isolated_vertices(Point *points, int point_count, int *degree) {
    int isolated = 0;

    for (int i = 0; i < point_count; i++) {
        if (degree[i] != 0) continue;

        points[i].position.x = 10.0 + (isolated % 8) * 10.0;
        points[i].position.y = 95.0 + (isolated / 8) * 10.0;
        isolated++;
    }
}

// Algorytm barycentryczny (wzorowany na algorytmie Tutte'a). 
// Rozmieszcza wierzchołki w środku ciężkości ich sąsiadów.
// Daje dobre, estetyczne wyniki dla grafów zbliżonych do planarnych, 
// choć w tej uproszczonej wersji nie gwarantuje braku przecięć krawędzi.
static int barycentric_layout(Point *points, Edge *edges, int point_count, int edge_count, FILE *iter_file) {
    int *degree = calloc(point_count, sizeof(int));
    int *fixed = calloc(point_count, sizeof(int));
    double *sum_x = calloc(point_count, sizeof(double));
    double *sum_y = calloc(point_count, sizeof(double));
    int active_count = 0;

    if (degree == NULL || fixed == NULL || sum_x == NULL || sum_y == NULL) {
        // Komunikat błędu, jeśli nie udało się przydzielić pamięci dla tablic pomocniczych
        fprintf(stderr, "Nie można zaalokować pamięci dla układu barycentrycznego.\n");
        free(degree);
        free(fixed);
        free(sum_x);
        free(sum_y);
        return 1; // Zwracamy kod błędu
    }

    int valid_edges = fill_degrees(edges, point_count, edge_count, degree, &active_count);

    // Wypisujemy informację o stosowanym algorytmie i jego ograniczeniach
    fprintf(stderr,
            "Układ barycentryczny (styl Tutte'a): uproszczony układ inspirowany grafami planarnymi; "
            "rysowanie bez przecięć krawędzi nie jest gwarantowane.\n");

    // Sprawdzenie na podstawie wzoru Eulera pozwala wykryć grafy, które na pewno nie są planarne.
    // Wzór mówi, że dla prostych grafów planarnych zachodzi m <= 3n - 6 (gdzie m to krawędzie, n to wierzchołki).
    if (active_count >= 3 && valid_edges > 3 * active_count - 6) {
        fprintf(stderr,
                "Ostrzeżenie: m > 3n - 6, zatem ten graf na pewno nie jest planarny (będą przecięcia).\n");
    }
    // Jeśli liczba krawędzi jest mniejsza niż n - 1, graf na pewno nie jest w pełni połączony.
    if (active_count > 0 && valid_edges < active_count - 1) {
        fprintf(stderr,
                "Ostrzeżenie: graf może być niespójny; ten algorytm działa najlepiej dla jednej spójnej składowej.\n");
    }

    int boundary_count = choose_boundary_vertices(degree, point_count, active_count, fixed);
    place_initial_positions(points, point_count, degree, fixed);
    place_boundary(points, point_count, fixed, boundary_count);

    // Główna pętla relaksacji barycentrycznej.
    // Wierzchołki, które nie leżą na brzegu, przesuwają się w kierunku średniej pozycji swoich sąsiadów.
    for (int iter = 0; iter < 180; iter++) {
        for (int i = 0; i < point_count; i++) {
            sum_x[i] = 0.0;
            sum_y[i] = 0.0;
        }

        // Sumowanie współrzędnych sąsiadów
        for (int i = 0; i < edge_count; i++) {
            if (!valid_edge(edges[i], point_count)) continue;

            int a = edges[i].vertex_a;
            int b = edges[i].vertex_b;

            sum_x[a] += points[b].position.x;
            sum_y[a] += points[b].position.y;

            sum_x[b] += points[a].position.x;
            sum_y[b] += points[a].position.y;
        }

        // Aktualizacja pozycji - wierzchołki "relaksują się" w stronę średniej
        for (int i = 0; i < point_count; i++) {
            if (fixed[i] || degree[i] == 0) continue; // Pomijamy brzegowe i izolowane

            double avg_x = sum_x[i] / degree[i];
            double avg_y = sum_y[i] / degree[i];
            
            // Zachowujemy 40% starej pozycji i dodajemy 60% średniej sąsiadów,
            // co zapobiega zbyt agresywnemu zapadaniu się wierzchołków do środka.
            points[i].position.x = points[i].position.x * 0.4 + avg_x * 0.6;
            points[i].position.y = points[i].position.y * 0.4 + avg_y * 0.6;
        }
        save_iteration(iter_file, points, point_count);
    }

    place_isolated_vertices(points, point_count, degree);

    free(degree);
    free(fixed);
    free(sum_x);
    free(sum_y);
    return 0;
}

// Główna funkcja wywoływana z zewnątrz, rozdzielająca pracę do odpowiedniego algorytmu.
// algorithm_id: 1 - Force-directed layout, 2 - Barycentric layout
int algorithm(Point *points, Edge *edges, int point_count, int edge_count, int algorithm_id) {
    if (points == NULL || edges == NULL || point_count <= 0) {
        // Podstawowa walidacja wejścia - wskaźniki nie mogą być puste
        fprintf(stderr, "Wejście algorytmu jest nieprawidłowe (puste wskaźniki lub brak wierzchołków).\n");
        return 1; // 1 to w Unixie typowy kod błędu
    }

    FILE *iter_file = fopen("output/iterations.txt", "w");
    if (iter_file == NULL) {
        fprintf(stderr, "Nie mozna otworzyc pliku output/iterations.txt do zapisu iteracji.\n");
    }

    int result = 1;

    // Wybór odpowiedniego algorytmu na podstawie podanego ID (1 lub 2)
    if (algorithm_id == 1) {
        result = force_directed_layout(points, edges, point_count, edge_count, iter_file);
    } else if (algorithm_id == 2) {
        result = barycentric_layout(points, edges, point_count, edge_count, iter_file);
    } else {
        // Obsługa przypadku podania nieistniejącego ID
        fprintf(stderr, "Nieznany identyfikator algorytmu: %d\n", algorithm_id);
    }

    if (iter_file != NULL) {
        fclose(iter_file);
    }

    return result;
}
