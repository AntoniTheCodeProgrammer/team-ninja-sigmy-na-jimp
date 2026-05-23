# Końcowa dokumentacja projektu

## 1. Cel projektu
Wyznaczenie współrzędnych wierzchołków grafu (2D), w najlepszym wypadku planarnie (algorytm nie gwarantuje planarności rysunku grafu). Implementacja w C generuje plik wyjściowy (tekstowy lub binarny) zawierający współrzędne wierzchołków; plik ten może być przetworzony/zwizualizowany przez aplikację w Javie.

## 2. Zrealizowane elementy
- Parser pliku wejściowego w formacie listy krawędzi.
- Implementacja prostego algorytmu siłowego (force-directed) do wyznaczania współrzędnych.
- Zapis wyników w formacie tekstowym i binarnym.

## 3. Implementowany algorytm
- Typ: siłowy (inspirowany Fruchterman–Reingold / Eades), uproszczona wersja.
- Główne cechy:
  - Inicjalizacja losowych pozycji w obszarze [0,100]x[0,100].
  - Repulsja między wszystkimi parami punktów: siła ~ 10.0 / distance.
  - Przyciąganie wzdłuż krawędzi: siła ~ (distance - weight) * -0.1.
  - Ograniczanie wartości przesunięcia do `temperature` i ochładzanie temperatury o współczynnik 0.95 każdą iterację.
  - Liczba iteracji: 100 (wartość stała w kodzie).

## 4. Testy i wyniki
- Testy manualne wykonane na `in_file_example.txt` → wynik `out_file_example.txt` (w katalogu projektu).
— wynik zapisany jako `graph_visualization.png`.
- Uwaga: z racji losowego startu układ może się różnić między uruchomieniami; dodać ziarno RNG, jeśli potrzebna deterministyczność.

## 5. Problemy napotkane / ograniczenia
- Stały rozmiar bufora na krawędzie (100000) — ryzyko overflow dla bardzo dużych grafów.
- Brak walidacji wejścia (brak sprawdzania zakresu identyfikatorów wierzchołków).
- Brak zwalniania tablicy `velocities` w `algorithm.c`.
- Format binarny jest zależny od platformy (endianness, rozmiar `int`), co utrudnia przenoszenie binariów między różnymi architekturami.

## 6. Wnioski i rekomendacje
- Dla stabilnej integracji z Javą zalecany jest format tekstowy.
- Zaimplementować dynamiczne alokowanie tablicy krawędzi (np. `realloc`) i walidację zakresów ID.
- Dodać opcje liniowe konfigurowalne (liczba iteracji, stałe sił, tryb zapisu, seed RNG).
- Dodać jednostkowe testy parsera i zapisu oraz testy regresji wyników.

