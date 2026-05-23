# Dokumentacja implementacyjna części Java

## A. Przegląd architektury Java

Część Java projektu jest aplikacją Swing. Odpowiada za graficzny interfejs użytkownika, wczytywanie danych do modelu, wyświetlanie grafu oraz obsługę myszy. Algorytmy wyznaczania układu grafu nie są wykonywane w Javie; aktywna ścieżka GUI uruchamia skompilowany program C.

Podział odpowiedzialności:

- `Main` - uruchamia aplikację Swing,
- `GraphApp` - zarządza oknem, menu, paskiem narzędzi i akcjami użytkownika,
- `GraphPanel` - rysuje graf i obsługuje interakcje myszy,
- `GraphReader` - wczytuje i zapisuje pliki,
- `Graph` - przechowuje dane grafu.

## B. Opis modułów i klas

### `Main`

Rola:

- punkt startowy aplikacji Java,
- tworzy obiekt `GraphApp`,
- uruchamia GUI przez `SwingUtilities.invokeLater`, aby interfejs działał w wątku zdarzeń Swing.

Kluczowa metoda:

- `main(String[] args)` - start programu.

### `Graph`

Rola:

- prosty model danych grafu,
- przechowuje wierzchołki i krawędzie,
- nie zawiera logiki algorytmów układu.

Kluczowe pola:

- `Map<String, Node> nodes` - mapa wierzchołków według identyfikatora,
- `List<Edge> edges` - lista krawędzi.

Kluczowe metody:

- `getOrCreateNode(String id)` - zwraca istniejący wierzchołek albo tworzy nowy,
- `addEdge(String name, String srcId, String tgtId, double weight)` - dodaje krawędź i potrzebne wierzchołki,
- `clear()` - usuwa aktualny graf.

### `Graph.Node`

Rola:

- reprezentuje pojedynczy wierzchołek.

Pola:

- `String id` - identyfikator wierzchołka,
- `double x`, `double y` - aktualne współrzędne wierzchołka.

### `Graph.Edge`

Rola:

- reprezentuje krawędź pomiędzy dwoma wierzchołkami.

Pola:

- `String name` - nazwa krawędzi,
- `Node source` - wierzchołek początkowy,
- `Node target` - wierzchołek końcowy,
- `double weight` - waga krawędzi.

### `GraphReader`

Rola:

- obsługa plików grafu i współrzędnych,
- walidacja formatu danych,
- zgłaszanie prostych, czytelnych wyjątków.

Kluczowe metody:

- `loadGraphTxt(File file, Graph graph)` - wczytuje graf z tekstowej listy krawędzi,
- `loadCoordsTxt(File file, Graph graph)` - wczytuje współrzędne TXT,
- `loadCoordsBinary(File file, Graph graph)` - opcjonalnie wczytuje binarne współrzędne zgodne z zapisem C,
- `saveCoordsTxt(File file, Graph graph)` - zapisuje aktualne współrzędne do TXT.

Interakcje:

- modyfikuje obiekt `Graph`,
- jest wywoływany przez `GraphApp`,
- nie rysuje i nie uruchamia algorytmów.

### `GraphPanel`

Rola:

- własny komponent Swing do rysowania grafu,
- obsługuje powiększanie, przesuwanie widoku i przeciąganie wierzchołków.

Kluczowe pola:

- `Graph graph` - rysowany model grafu,
- `scale`, `offsetX`, `offsetY` - parametry widoku,
- `draggedNode` - aktualnie przeciągany wierzchołek,
- `showLabels`, `showWeights` - ustawienia widoczności opisów.

Kluczowe metody:

- `paintComponent(Graphics g)` - rysowanie grafu,
- `fitGraphToView()` - dopasowanie widoku do współrzędnych wierzchołków,
- `resetView()` - reset widoku przez dopasowanie grafu,
- `setShowLabels(boolean)` i `setShowWeights(boolean)` - ustawienia prezentacji,
- `screenToWorldX/Y(...)` i `worldToScreenX/Y(...)` - przeliczanie układu ekranu i układu grafu.

Ważna decyzja implementacyjna:

- pozycje grafu są skalowane, ale promień wierzchołków, grubość linii i rozmiar tekstu pozostają w praktyce stałe w pikselach ekranu. Dzięki temu zoom nie powiększa nadmiernie etykiet i kółek.

### `GraphApp`

Rola:

- główne okno aplikacji,
- tworzy menu, pasek narzędzi, panel grafu i pasek statusu,
- obsługuje akcje użytkownika,
- uruchamia program C przez `ProcessBuilder`.

Kluczowe pola:

- `Graph graph` - model danych,
- `GraphPanel graphPanel` - komponent wizualizacji,
- `statusLabel` - pasek informacji,
- `currentGraphFile` - aktualnie wczytany plik grafu,
- `pendingCoordinateFile` - plik współrzędnych wybrany przed wczytaniem grafu,
- elementy menu i przyciski uruchamiające akcje.

Kluczowe metody:

- `loadGraphTxt()` - wybór i wczytanie grafu,
- `loadCoordsTxt()` i `loadCoordsBinary()` - wybór i wczytanie współrzędnych,
- `saveCoordsTxt()` - zapis współrzędnych,
- `runCLayout(int algorithmId, String algorithmName)` - uruchomienie algorytmu C,
- `findProjectRoot()` - znalezienie katalogu projektu,
- `resolveExecutable(Path projectRoot)` - znalezienie `program.exe` albo `program`,
- `updateStatus(String action)` - aktualizacja paska statusu.

### Klasy układów Java jako element historyczny

W katalogu mogą istnieć klasy `CircularLayout`, `ForceDirectedLayout` i `LayoutAlgorithm`. Nie są one częścią aktywnego przepływu GUI i nie są używane jako główne algorytmy z poziomu interfejsu użytkownika. Aktualna architektura zakłada, że algorytmy układu są wykonywane przez program C, a Java tylko wyświetla wynik i pozwala na interakcję.

## C. Główne przepływy danych

### 1. Wczytanie grafu TXT

1. `GraphApp` otwiera `JFileChooser`.
2. Użytkownik wybiera plik grafu.
3. `GraphReader.loadGraphTxt(...)` czyta linie i sprawdza format.
4. `Graph` zostaje wyczyszczony i wypełniony nowymi wierzchołkami oraz krawędziami.
5. `GraphPanel` dopasowuje widok i odświeża rysunek.
6. `GraphApp` aktualizuje pasek statusu.

### 2. Wczytanie współrzędnych TXT/BIN

1. `GraphApp` wybiera plik współrzędnych.
2. Jeżeli graf jest już wczytany, `GraphReader` przypisuje współrzędne do istniejących wierzchołków.
3. Jeżeli graf nie jest jeszcze wczytany, `GraphApp` zapamiętuje plik jako oczekujący.
4. Po późniejszym wczytaniu grafu aplikacja automatycznie próbuje zastosować oczekujące współrzędne.
5. Po poprawnym wczytaniu `GraphPanel.fitGraphToView()` dopasowuje widok.

### 3. Uruchomienie algorytmu C

1. Użytkownik wybiera akcję `Force-directed layout` albo `Barycentric Tutte-style Layout`.
2. `GraphApp` sprawdza, czy graf jest wczytany.
3. `GraphApp` znajduje katalog projektu i plik wykonywalny C.
4. `ProcessBuilder` uruchamia program C z argumentami wejścia, wyjścia, trybu `text` i numeru algorytmu.
5. Program C zapisuje współrzędne do `output/java_generated_coords.txt`.
6. Java wczytuje wygenerowane współrzędne przez `GraphReader.loadCoordsTxt(...)`.
7. `GraphPanel` dopasowuje widok i odświeża rysunek.

### 4. Ręczne przeciąganie wierzchołka

1. `GraphPanel` odbiera `mousePressed`.
2. Komponent sprawdza, czy kursor trafił w wierzchołek.
3. Jeśli tak, zapamiętuje wierzchołek jako `draggedNode`.
4. Podczas `mouseDragged` aktualizuje `x` i `y` tego wierzchołka.
5. Po każdej zmianie wykonywane jest `repaint()`.
6. `mouseReleased` kończy przeciąganie.

### 5. Zoom, pan i reset widoku

- Zoom działa przez kółko myszy i zmienia `scale`.
- Pan działa przez przeciąganie pustego tła i zmienia `offsetX`, `offsetY`.
- Reset widoku wywołuje `fitGraphToView()`, które liczy zakres współrzędnych wierzchołków i centruje graf w panelu.

## D. Obsługa błędów

Kod używa dwóch głównych typów wyjątków:

- `IOException` - błędy systemowe i plikowe, np. brak dostępu do pliku,
- `IllegalArgumentException` - błędy zawartości pliku, np. zła liczba pól albo niepoprawna liczba.

Takie rozróżnienie jest prostsze i czytelniejsze niż szerokie `throws Exception`. Dzięki temu GUI może pokazywać użytkownikowi komunikaty typu `File error`, `Invalid graph file` albo `Invalid coordinate file`.

Komunikaty są wyświetlane w `GraphApp` przy pomocy `JOptionPane.showMessageDialog(...)`.

## E. Implementacja Swing

Interfejs składa się z:

- paska menu (`JMenuBar`),
- paska narzędzi (`JPanel` z przyciskami),
- centralnego panelu rysowania (`GraphPanel`),
- dolnego paska statusu (`JLabel`).

`GraphPanel` nadpisuje `paintComponent(Graphics g)` i używa `Graphics2D`. Włączone jest wygładzanie krawędzi przez antialiasing. Najpierw rysowane są krawędzie, potem wierzchołki, aby wierzchołki nie ginęły pod liniami.

Opcje `Labels` i `Weights` sterują widocznością etykiet wierzchołków oraz wag krawędzi.

## F. Integracja Java z C

Java uruchamia część C przez `ProcessBuilder`. Nie używa JNI ani JNA, ponieważ byłyby zbyt złożone dla tego projektu i utrudniałyby prezentację.

Najważniejsze elementy integracji:

- katalog projektu jest wyszukiwany przez sprawdzenie obecności `src/main.c`,
- program wykonywalny to `program.exe` w Windows albo `program` w innych systemach,
- Java uruchamia C z trybem wyjścia `text`,
- standardowe wyjście i wyjście błędów są przechwytywane,
- po sukcesie Java wczytuje wygenerowany plik współrzędnych,
- po błędzie użytkownik dostaje komunikat z informacją z procesu C.

Ograniczenie: program C musi być wcześniej skompilowany.

## G. Uwagi konserwacyjne

- Nowe akcje menu najlepiej dodawać w `GraphApp.setupMenu()`.
- Nowe przyciski paska narzędzi należy dodawać w `GraphApp.setupToolbar()`.
- Rozmiary wierzchołków, kolory i rozmiary tekstu są w `GraphPanel`.
- Obsługiwane formaty plików znajdują się w `GraphReader`.
- Nowe walidacje plików również powinny trafiać do `GraphReader`.
- Nie należy dodawać logiki algorytmów układu do `GraphPanel` ani `GraphApp`.
- Jeśli zostaną dodane nowe algorytmy, powinny być wybierane przez wywołanie programu C z odpowiednim numerem lub parametrem, a nie przez implementowanie layoutu w Javie.
