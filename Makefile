CC = gcc
CFLAGS = -Wall -Wextra -Iinclude
LDFLAGS = -lm

TARGET = program
SRCS = src/main.c src/import.c src/algorithm.c src/extraction.c
HEADERS = include/struct.h include/import.h include/algorithm.h include/extraction.h

all: $(TARGET)

$(TARGET): $(SRCS) $(HEADERS)
	$(CC) $(CFLAGS) -o $(TARGET) $(SRCS) $(LDFLAGS)

clean:
	rm -f $(TARGET) $(TARGET).exe *.o

.PHONY: all clean
