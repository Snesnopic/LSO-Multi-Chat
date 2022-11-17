#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <sys/types.h> // system defined identifiers.
#include <netinet/in.h> // internet address structure.
#include <sys/socket.h> // Need 4 socket(), bind()
#include <arpa/inet.h>
#include <semaphore.h>
#include "group.h"

//variabili per semafori (tipo quello fuori via claudio che ci fa aspettare 30 minuti DioP)
sem_t x, y;
pthread_t tid;
pthread_t writerthreads[100];
pthread_t readerthreads[100];
int readercount = 0;


void* reader(void* param)
{
    sem_wait(&x);
    readercount++;

    if (readercount == 1)
        sem_wait(&y);

    sem_post(&x);

    printf("\n%d lettore è dentro", readercount);

    sleep(5);

    sem_wait(&x);
    readercount--;

    if (readercount == 0)
    {
        sem_post(&y);
    }

    sem_post(&x);

    printf("\n%d lettore sta abbandonando", readercount + 1);
    pthread_exit(NULL);
}


void* writer(void* param)
{
    printf("\nscrittore sta cercando di entrare...");
    sem_wait(&y);
    printf("\nscrittore e' entrato");
    sem_post(&y);
    printf("\nscrittore sta lasciando...");
    pthread_exit(NULL);
}


int main(int argc, char *argv[])
{
    int serverSocket, newSocket;
    struct sockaddr_in serverAddr;
    struct sockaddr_storage serverStorage;

    socklen_t addr_size;
    sem_init(&x, 0, 1);
    sem_init(&y, 0, 1);

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(8989);

    // Bind della socket
    bind(serverSocket,(struct sockaddr*)&serverAddr,sizeof(serverAddr));
    // Listening socket con max 40 richieste in coda
    if (listen(serverSocket, 50) == 0)
            printf("Ascolto...\n");
    else
        printf("Errore nell'ascolto\n");

    // Array di thread
    pthread_t tid[60];
    int i = 0;

    while (1)
    {
        addr_size = sizeof(serverStorage);

        // Estrae la prima richiesta dalla coda
        newSocket = accept(serverSocket, (struct sockaddr*) &serverStorage, &addr_size);
        int choice = 0;
        recv(newSocket, &choice, sizeof(choice), 0);
        if (choice == 1)
        {
            // Creazione thread lettori
            if (pthread_create(&readerthreads[i++], NULL, reader, &newSocket) != 0)
                printf("Errore creazione thread\n");
        }
        else if (choice == 2)
        {
            // Creazione thread scrittori
            if (pthread_create(&writerthreads[i++], NULL, writer, &newSocket) != 0)
                printf("Errore creazione thread\n");
        }

        if (i >= 50)
        {
            i = 0;

            while (i < 50)
            {
                pthread_join(writerthreads[i++], NULL);
                pthread_join(readerthreads[i++], NULL);
            }
            i = 0;
        }
    }
    return 0;
}
