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
#define PORTNUMBER 8989
#define MAXCLIENTS 50
void *connection_handler(void *);

int networkMessageHandler(char scelta, PGconn *conn, int socket);
long writeSock(int socket, char *str);

PGconn* conn;

int main()
{
    //variabili per connessione al db o altre cose inerenti alla gestione dati da db:

    conn = dbConnection(conn);
    struct sockaddr_in serverAddr;

    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORTNUMBER);
    // Bind della socket
    if(bind(serverSocket,(struct sockaddr*)&serverAddr,sizeof(serverAddr)) == -1)
    {
        printf("Errore nel binding\n");
        return 1;
    }
    else
        printf("OK binding\n");
    // Listening socket con max 40 richieste in coda
    if (listen(serverSocket, MAXCLIENTS) == -1)
    {
        printf("Errore nell'ascolto\n");
        return 1;
    }
    else
        printf("OK listen\n");
    // Array di thread
    pthread_t tid[MAXCLIENTS];
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
        printf("Connesso un nuovo client! Thread: %lu\n", tid[i]);
        i++;
    }
}

int networkMessageHandler(char scelta, PGconn *conn, int socket)
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
    char buff[2048];
    recv(sock,buff,2048,0);

    printf("User sent: %s\n",buff);
    recv(sock,buff,2048,0);
    printf("Username: %s\n",buff);
    recv(sock,buff,2048,0);
    printf("Password: %s\n",buff);
    writeSock(sock,"Prova 1");


    fflush(stdout);
//    int sock = *(int*)socket_desc;
//    int read_size;
//    char *message , client_message[2000];
//        read_size = recv(sock , client_message , 2000 , 0);
//        //end of string marker
//        char choice = client_message[0];
//        client_message[read_size] = '\0';
//        printf("debug: %s\n", client_message);
//        strcpy(client_message, "");
//        read_size = recv(sock, client_message, 2000, 0);
//        printf("debug: %s\n", client_message);
//        client_message[read_size] = '\0';
//        char *buff = (char*)malloc(sizeof(char) * read_size);
//        strcpy(buff, client_message);
//        strcpy(client_message, "");
//
//        read_size = recv(sock, client_message, 2000, 0);
//        printf("debug: %s\n", client_message);
//        client_message[read_size] = '\0';
//
//        char *buff2 = (char*)malloc(sizeof(char) * read_size);
//        strcpy(buff2, client_message);
//        strcpy(client_message, "");
//
////        int msg = networkMessageHandler(choice, buff, buff2, conn);
//        char mess[33];
////        itoa(msg, mess);
//        fflush(stdout);
////        printf("messaggio della send: %s\n", mess);
//        int flag = 1;
//        setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, (char *) &flag, sizeof(int));
//        long bytes = send(sock, "test1", strlen("test1"), 0);
//        printf("Ho mandato %ld byte",bytes);
//        setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, (char *) &flag, sizeof(int));
//        sleep(3);
//        bytes = send(sock , "test2" , strlen("test2"), 0);
//        printf("Ho mandato %ld byte",bytes);
//        setsockopt(sock, IPPROTO_TCP, TCP_NODELAY, (char *) &flag, sizeof(int));
//        //clear the message buffer
//        memset(client_message, 0, 2000);



    return 0;
}

long writeSock(int socket, char *str)
{
    char *newstr = malloc(strlen(str) + 2);
    char *cpy = malloc(strlen(str)+2);
    int c = 0;
    strcpy(newstr, str);
    for(size_t i = 0; i < strlen(newstr); ++i)
    {
        if(newstr[i] != '\n')
        {
            cpy[c] = newstr[i];
            c++;
        }
    }
    strcat(cpy, "\n");
    long bytes = write(socket, cpy, strlen(cpy));
    free(cpy),
    free(newstr);
    return bytes;
}
