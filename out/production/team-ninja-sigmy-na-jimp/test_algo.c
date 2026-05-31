#include <stdio.h>
int main() {
    double t = 1.0;
    double sum = 0.0;
    for(int i=0; i<100; i++) {
        sum += t;
        t *= 0.95;
    }
    printf("Max distance: %f\n", sum);
    return 0;
}
