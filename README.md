# team-ninja-sigmy-na-jimp

link do google docs na specyfikacje:

https://docs.google.com/document/d/13AEUVEHptZfyam7YQsp_-H12sAjNgtQraDFNYTf3fv4/edit?usp=sharing

co ja zrozumialem ze mamy zrobic jakub:

mamy zrobic teraz kod na parsowanie i wyjsciowy
zapsial to tak
1) sypecyfikacja funkcjonalna

pasrowanie     -       algorytm        - wysciowy-> potem w javie wizualizacja

robimy na nastepny pon tylko parsowanie i wyjsciowy 

2) wstepne struktury danych



### Opis zadania projektowego
Zadanie polega na stworzeniu dwóch aplikacji: jednej w języku C, a drugiej w Javie. Każda z aplikacji ma wyznaczać współrzędne węzłów dla ,,ładnej" wizualizacji grafu planarnego podanego w postaci listy krawędzi.

Uwaga: istnieje kilka sposobów wyznaczania współrzędnych wierzchołków. Każdy zespół powinien zaimplementować, przetestować i porównać co najmniej dwa wybrane algorytmy.
Przykłady algorytmów: Fruchterman–Reingold / Eades, Spectral layout, triangulacja, Twierdzenie Tutte o sprężynach (https://en.wikipedia.org/wiki/Tutte_embedding),... poniżej można znaleźć dwie przykładowe przeglądowe pozycje literatury (pliki PDF dołączone do tej sekcji).

Aplikacja w języku C ma być sterowana tylko z linii poleceń, przez argumenty i opcje. Aplikacja napisana w języku Java powinna być sterowana zdarzeniami przez graficzny interfejs użytkownika.

Przykładowe grafy będą plikami tekstowymi o postaci

<nazwa_krawędzi> <wierzchołek_A> <wierzchołek_B> <waga_krawędzi>
...
Przykład:

AB   1  2  1
BC   2  3  1
CD   3  4  1
DB   4  2  1.407
Wynikiem działania programu ma być plik tekstowy lub binarny (w zależności od wyboru użytkownika) zawierający listę współrzędnych wierzchołków:

<wierzchołek> <współrzędna_x> <współrzędna_y>
...
Przykład:

1 0.0 0.0
2 1.0 0.0
3 1.0 1.0
4 0.0 1.0

### Dokumentacja projektu C
Projekt w języku C musi zawierać następujące rodzaje dokumentacji:

Dokumentacja funkcjonalna opisująca działanie projektu
Dokumentacja implementacyjna przedstawiająca szczegóły implementacyjne programu, specyfikacje wejścia oraz wyjścia, plików wyjściowych, etc.
Końcowa dokumentacja projektu
Część 1 oraz 2 dokumentacji zostanie przekazana innemu zespołowi, którego to zadaniem będzie na tej podstawie implementacja części w języku JAVA wczytującej pliki wyjściowe tworzone przez program w języku C
