#include <stdio.h>
#include <strings.h>
#include <string.h>
#include <sys/types.h> // system defined identifiers.
#include <netinet/in.h> // internet address structure.
#include <sys/socket.h> // Need 4 socket(), bind()
#include <unistd.h>
#include <pthread.h>
#include <stdlib.h>
#include "group.h"
#include "pgconnection.h"
#include "user.h"


void *connection_handler(void *);

int networkMessageHandler(char scelta, char str_1[], char str_2[], PGconn *conn);

void writeOnSocket(int socket, char message[], int messageLenght);

PGconn *conn = NULL;

int main(int argc, char *argv[])
{
    //variabili per connessione al db o altre cose inerenti alla gestione dati da db:

    conn = dbConnection(conn);

    int serverSocket;
    struct sockaddr_in serverAddr;
    struct sockaddr_storage serverStorage;

    socklen_t addr_size;

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(8989);
    // Bind della socket
    if(bind(serverSocket,(struct sockaddr*)&serverAddr,sizeof(serverAddr)) == -1)
    {
        printf("Errore nel binding\n");
        return 1;
    }
    else
        printf("OK binding\n");
    // Listening socket con max 40 richieste in coda
    if (listen(serverSocket, 50) == -1)
    {
        printf("Errore nell'ascolto\n");
        return 1;
    }
    else
        printf("OK listen\n");
    // Array di thread
    pthread_t tid[60];
    int i = 0;
    fflush(stdout);
    int credentialsStatus;
   while (1)
    {
        fflush(stdout);
        printf("Attendo...\n");
        fflush(stdout);
        // Estrae la prima richiesta dalla coda
        int newSocket = accept(serverSocket, NULL,NULL);
        pthread_create(&tid[i], NULL, connection_handler, &newSocket);
        printf("%d Connesso!\n", tid[i]);
        i++;
    }
}

int networkMessageHandler(char scelta, char str_1[], char str_2[], PGconn *conn)
{
    //str1 e str2 = username e password, non li ho chiamati in questo modo poiché si potrebbe scegliere anche la creazione o eliminazione di un gruppo
    switch(scelta)
    {
        case '0': //login
            return usernameAndPasswordCheck(str_1, str_2, conn); //0 se login OK, 1 altrimenti
        case '1': //registrazione
            return userRegistration(str_1, str_2, conn);
        default:
            printf("Errore network message hanlder: valore di scelta non valido\n");
    }
}


void *connection_handler(void *socket_desc)
{
    int sock = *(int*)socket_desc;
    int read_size;
    char *message , client_message[2000];

    while(read_size = recv(sock , client_message , 2000 , 0) > 0)
    {
        read_size = recv(sock , client_message , 2000 , 0);
        //end of string marker
        char choice = client_message[0];
        client_message[read_size] = '\0';
        printf("debug: %s\n", client_message);
        strcpy(client_message, "");
        read_size = recv(sock, client_message, 2000, 0);



        printf("debug: %s\n", client_message);
        client_message[read_size] = '\0';
        char *buff = (char*)malloc(sizeof(char) * read_size);
        strcpy(buff, client_message);
        strcpy(client_message, "");

        read_size = recv(sock, client_message, 2000, 0);


        printf("debug: %s\n", client_message);
        client_message[read_size] = '\0';

        char *buff2 = (char*)malloc(sizeof(char) * read_size);
        strcpy(buff2, client_message);
        strcpy(client_message, "");

        int msg = networkMessageHandler(choice, buff, buff2, conn);
        char mess[33];
        itoa(msg, mess);
        fflush(stdout);
        printf("messaggio della send: %s\n", mess);
        send(sock, "", strlen(""), 0);
        sleep(3);
        send(sock , mess , strlen(mess), 0);
        //clear the message buffer
        memset(client_message, 0, 2000);
    }

    if(read_size == 0)
    {
        puts("Client disconnected");
        fflush(stdout);
    }
    else if(read_size == -1)
    {
        perror("recv failed");
    }

    return 0;
}