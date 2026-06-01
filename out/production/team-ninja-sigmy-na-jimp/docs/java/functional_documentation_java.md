# Dokumentacja funkcjonalna części Java

## A. Cel projektu

Celem aplikacji Java jest wygodna wizualizacja grafów oraz współrzędnych wierzchołków wyznaczanych poza częścią graficzną programu. Program działa jako interfejs użytkownika: pozwala wczytać strukturę grafu, wczytać lub wygenerować współrzędne, obejrzeć graf, zmienić widok oraz ręcznie poprawić położenie wierzchołków.

Aplikacja Java nie jest głównym miejscem implementacji algorytmów rozmieszczania grafu. Algorytmy układu są realizowane przez program w języku C, a Java pełni rolę warstwy wizualizacji i interakcji.

## B. Zakres funkcji programu

Program Java umożliwia:

- wczytanie grafu z pliku tekstowego,
- wczytanie współrzędnych wierzchołków z pliku tekstowego,
- opcjonalne wczytanie współrzędnych z pliku binarnego utworzonego przez część C,
- zapis aktualnych współrzędnych do pliku tekstowego,
- uruchomienie algorytmu 1 z części C: `Force-directed layout`,
- uruchomienie algorytmu 2 z części C: `Barycentric Tutte-style Layout`,
- wyświetlenie grafu w centralnym panelu,
- powiększanie i pomniejszanie widoku kółkiem myszy,
- przesuwanie widoku przez przeciąganie pustego tła,
- dopasowanie widoku do całego grafu przez `Reset view`,
- pokazywanie i ukrywanie etykiet wierzchołków,
- pokazywanie i ukrywanie wag krawędzi,
- przeciąganie pojedynczych wierzchołków w celu ręcznej zmiany ich współrzędnych.

## C. Instrukcja użytkownika

1. Uruchom aplikację Java, startując klasę `etap2_java.Main`.
2. Wybierz `File -> Load graph (TXT)` albo przycisk `Load graph`, a następnie wskaż plik grafu.
3. Po wczytaniu grafu wybierz jedną z opcji:
   - uruchom `Force-directed layout`,
   - uruchom `Barycentric Tutte-style Layout`,
   - albo wczytaj gotowy plik współrzędnych TXT/BIN.
4. Oglądaj graf w centralnym panelu. Krawędzie są rysowane przed wierzchołkami, dzięki czemu wierzchołki pozostają widoczne.
5. Używaj kółka myszy do przybliżania i oddalania widoku.
6. Przeciągaj puste tło, aby przesuwać widok.
7. Kliknij i przeciągnij wierzchołek, aby ręcznie zmienić jego położenie.
8. Użyj `Reset view`, aby dopasować cały graf do okna.
9. Użyj opcji `Labels` i `Weights`, aby pokazać lub ukryć etykiety wierzchołków i wagi krawędzi.
10. Wybierz `File -> Save coordinates (TXT)` albo przycisk `Save coordinates`, aby zapisać aktualne pozycje wierzchołków.

Jeżeli użytkownik wybierze plik współrzędnych przed wczytaniem grafu, program zapamięta ten wybór i spróbuje zastosować współrzędne po późniejszym wczytaniu grafu. Jest to potrzebne, ponieważ plik współrzędnych nie zawiera krawędzi ani wag.

## D. Akceptowane formaty wejściowe

### Plik grafu TXT

Każda niepusta linia pliku grafu powinna mieć postać:

```text
<edge_name> <vertex_A> <vertex_B> <weight>
```

Przykład:

```text
AB 1 2 1.0
BC 2 3 2.5
CD 3 4 7.0
```

Znaczenie pól:

- `edge_name` - nazwa krawędzi,
- `vertex_A` - identyfikator pierwszego wierzchołka,
- `vertex_B` - identyfikator drugiego wierzchołka,
- `weight` - waga krawędzi jako liczba.

Java nie obsługuje binarnego formatu wejściowego grafu. Graf należy wczytywać z pliku tekstowego.

### Plik współrzędnych TXT

Każda niepusta linia pliku współrzędnych powinna mieć postać:

```text
<vertex_id> <x> <y>
```

Przykład:

```text
1 10.0 20.0
2 40.0 20.0
3 25.0 60.0
```

Identyfikatory wierzchołków muszą odpowiadać identyfikatorom wcześniej wczytanego grafu.

### Plik współrzędnych BIN

Opcjonalny format binarny odpowiada formatowi zapisu z programu C. Plik składa się z powtarzanych rekordów:

```text
int id
double x
double y
```

Jeden rekord ma 20 bajtów przy założeniu: `int` = 4 bajty, `double` = 8 bajtów. Format binarny nie ma nagłówka, liczby rekordów ani informacji o wersji. Jest zależny od platformy, rozmiaru typów i kolejności bajtów, dlatego format TXT jest zalecany do zwykłej pracy.

## E. Format wyjściowy

Aplikacja Java zapisuje współrzędne w formacie tekstowym:

```text
<vertex_id> <x> <y>
```

Zapis obejmuje aktualne położenia wierzchołków, również te zmienione ręcznie przez przeciąganie w panelu.

Po uruchomieniu algorytmu C z poziomu GUI program C zapisuje plik:

```text
output/java_generated_coords.txt
```

Następnie aplikacja Java automatycznie wczytuje ten plik i odświeża widok grafu.

## F. Wybór algorytmu z poziomu GUI

Menu `Layout` oraz pasek narzędzi zawierają dwie akcje:

- `Force-directed layout` - uruchamia algorytm 1 z części C,
- `Barycentric Tutte-style Layout` - uruchamia algorytm 2 z części C.

W skróconym przycisku paska narzędzi algorytm 2 jest opisany jako `Barycentric Tutte`.

Przed uruchomieniem algorytmów należy zbudować program C. Java uruchamia już skompilowany plik `program.exe` w systemie Windows albo `program` w innych systemach.

Koncepcyjnie Java uruchamia polecenia podobne do:

```text
program.exe <input_file> <output_file> text 1
program.exe <input_file> <output_file> text 2
```

gdzie `1` oznacza algorytm siłowy, a `2` oznacza uproszczony algorytm Barycentric Tutte-style.

## G. Sytuacje wyjątkowe i komunikaty

| Sytuacja | Przykładowy komunikat | Znaczenie | Co zrobić |
|---|---|---|---|
| Pusty plik grafu | `Graph file is empty or contains no valid edges.` | Plik nie zawiera poprawnych krawędzi. | Wybierz poprawny plik grafu TXT. |
| Niepoprawna linia grafu | `Line 3: expected 4 values: <edge_name> <source> <target> <weight>` | Linia nie ma czterech wymaganych pól. | Popraw format linii w pliku. |
| Niepoprawna waga | `Line 5: invalid edge weight "abc".` | Waga krawędzi nie jest liczbą. | Wpisz wagę jako liczbę, np. `1.0`. |
| Współrzędne bez grafu | `Load a graph before loading or saving coordinates.` | Nie ma grafu, do którego można przypisać współrzędne. | Wczytaj graf albo wybierz współrzędne jako plik oczekujący. |
| Niepoprawna linia współrzędnych | `Line 2: expected 3 values: <vertex_id> <x> <y>` | Linia współrzędnych ma złą liczbę pól. | Popraw plik współrzędnych TXT. |
| Niepoprawna wartość współrzędnej | `Line 4: invalid x coordinate "abc".` | Współrzędna nie jest liczbą. | Wpisz poprawną liczbę. |
| Nieznany wierzchołek | `Line 6: unknown vertex "9" in coordinate file.` | Plik współrzędnych odnosi się do wierzchołka, którego nie ma w grafie. | Użyj współrzędnych pasujących do aktualnego grafu. |
| Brak współrzędnych | `Missing coordinates for vertex "3".` | Dla jednego z wierzchołków grafu nie podano pozycji. | Uzupełnij plik współrzędnych. |
| Zły rozmiar pliku BIN | `Binary coordinate file size must be divisible by 20 bytes.` | Plik BIN nie pasuje do oczekiwanego formatu rekordów. | Użyj poprawnego pliku BIN albo formatu TXT. |
| Brak programu C | `C executable not found. Build the C program first with gcc or make.` | Java nie znalazła skompilowanego programu C. | Skompiluj część C. |
| Błąd procesu C | `C algorithm failed` | Program C zakończył się błędem. | Sprawdź wejście, plik wykonywalny i komunikat błędu. |
| Próba uruchomienia algorytmu bez grafu | `Load a graph TXT file before running a layout algorithm.` | Nie wybrano grafu wejściowego. | Wczytaj graf TXT. |
