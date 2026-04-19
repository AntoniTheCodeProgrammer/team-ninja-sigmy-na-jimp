# Dokumentacja implementacyjna

Autor: Jakub Madejski Antoni Żmłudzin Piotr Sikorski

## 1. Cel dokumentu
Dokumentacja implementacyjna opisuje szczegóły wewnętrzne programu, formaty plików wejściowych i wyjściowych, API modułów oraz uwagi potrzebne do implementacji części projektu w języku Java.

## 2. Kompilacja i uruchomienie
- Kompilacja: `make` 
- Uruchomienie: `./program <input_file> <output_file> [text|binary]`
  - `text` : zapis w formacie tekstowym.
  - domyślnie : zapis binarny.

Przykład:

```
make
./program in_file_example.txt out_file_example.txt text
```

## 3. Format wejściowy (specyfikacja)
- Każda linia: `<nazwa_krawedzi> <vertex_A> <vertex_B> <weight>`
  - `nazwa_krawedzi` — string (bez spacji), do ~255 znaków.
  - `vertex_A`, `vertex_B` — int (nieujemne ID wierzchołków).
  - `weight` — double (waga/pożądana odległość krawędzi).

Przykład linii:

```
AB 0 1 1
```

Uwagi:
- Program ustala `point_count = max_id + 1` na podstawie największego występującego ID. Jeśli używane są ID 1-based, otrzymamy dodatkowy wierzchołek 0 — zalecane użycie 0-based.

## 4. Format wyjściowy

- Tekstowy: każda linia: `<vertex_id> <x> <y>` (wartości x i y zaokrąglone są do 2 miejsc po przecinku).
- Binarny: dla każdego wierzchołka zapisane kolejno: `int id`, `double x`, `double y`.

Zalecenie: używaj formatu tekstowego do wymiany między różnymi językami/platformami.

## 5. Struktury danych (`struct.h`)

```
typedef struct { double x; double y; } Cordinates;
typedef struct { int id; Cordinates position; } Point;
typedef struct { int vertex_a; int vertex_b; double weight; char* name; } Edge;
```

## 6. Moduły i funkcje

- `Edge* import_from_file(char* filename, int* edge_count, int* point_count)`
  - Wczytuje plik krawędzi, alokuje tablicę `Edge` (domyślnie rozmiar 100000), zwraca liczbę krawędzi i punktów.
  - Przy błędzie zwraca `NULL` i ustawia `edge_count` i `point_count` na 0.

- `void free_edges(Edge* edges, int edge_count)`
  - Zwalnia pamięć zaalokowaną przez `import_from_file`.

- `void algorithm(Point *points, Edge *edges, int point_count, int edge_count)`
  - Modyfikuje tablicę `points` (ustawia pozycje). Wejściowo `points` powinno mieć rozmiar `point_count`.

- `int save_to_text(char *filename, Point *points, int count)` i `int save_to_binary(char *filename, Point *points, int count)`
  - Funkcje zapisu wyników; zwracają 1 przy sukcesie, 0 przy błędzie.

## 7. Szczegóły implementacyjne algorytmu
- Inicjalizacja losowych pozycji w zakresie [0,100].
- Pętla 100 iteracji:
  - Zerowanie wektorów prędkości.
  - Obliczanie sił odpychania dla każdej pary punktów: `force_rep = K_rep / distance` (w kodzie `K_rep = 10.0`).
  - Obliczanie sił przyciągania dla każdej krawędzi: `force_attr = (distance - weight) * C_attr` (w kodzie `C_attr = -0.1`).
  - Ograniczanie prędkości do `temperature` i aktualizacja pozycji.
  - Schładzanie: `temperature *= 0.95`.

## 8. Walidacja i błędy
- `import_from_file` zwraca `NULL` przy błędzie otwarcia pliku lub braku danych.
- Funkcje zapisu zwracają 0 w przypadku błędów plikowych.

