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
int readSock(int socket, char *str);
char* readSock2(int socket, char *str);

PGconn* conn = NULL;

int main()
{

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
    // Listening socket con max 40 richieste in coda
    if (listen(serverSocket, MAXCLIENTS) == -1)
    {
        printf("Errore nell'ascolto\n");
        return 1;
    }

    // Array di thread
    pthread_t tid[MAXCLIENTS];
    int i = 0;
    fflush(stdout);
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
    char* client_message = (char*)malloc(sizeof(char)*2000);
    int read_size;
    int msg;
    char *buff;
    char *buff2;
    switch(scelta)
    {
        case '0': //login          
            //read per username
            buff = readSock2(socket, client_message);
            printf("Username da parte del client: %s|\n", buff);
            fflush(stdout);

            //read per password
            buff2 = readSock2(socket, client_message);         
            printf("Password da parte del client: %s|\n", buff2);
            fflush(stdout);

            msg = usernameAndPasswordCheck(buff, buff2, conn);
            memset(buff, 0, strlen(buff));
            memset(buff2, 0, strlen(buff2));
            return msg;
        case '1': //registrazione
            //read per username
            buff = readSock(socket, client_message);
            printf("Username da parte del client: %s\n", buff);
            fflush(stdout);


            //read per password
            buff2 = readSock(socket, client_message);
            printf("Password da parte del client: %s\n", buff2);
            fflush(stdout);


            return userRegistration(buff, buff2, conn); //1 se registrazione OK, 0 altrimenti
        default:
            printf("Errore network message hanlder: valore di scelta non valido\n");
    }
}


void *connection_handler(void *socket_desc)
{
    int sock = *(int*)socket_desc;
    int read_size;
    char* client_message = (char*) malloc(sizeof(char)*200);
    
    while((read_size = recv(sock , client_message , 200 , 0)) > 0)
    {
        //read per intero che stabilirà cosa fare
        char choice = client_message[0];
        client_message[read_size] = '\0';
        printf("Intero da parte del client: %s\n", client_message);
        memset(client_message, 0, 200);
        int msg = networkMessageHandler(choice, conn, sock);
        char mess[33];
        itoa(msg, mess);
        fflush(stdout);
        printf("messaggio della send: %s\n", mess);
        writeSock(sock, mess);

        if(read_size == 0)
        {
            puts("Client disconnected");
            fflush(stdout);
        }
        else if(read_size == -1)
        {
            perror("recv failed");
        }
    }
    //clear the message buffer
    memset(client_message, 0, 200);
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
    free(cpy);
    free(newstr);
    return bytes;
}

int readSock(int socket, char *str)
{
    int read_size = read(socket, str, sizeof(str)-1);
    char *newstr = (char*) malloc(sizeof(char)*read_size);
    char *cpy = (char*) malloc(sizeof(char)*read_size);
    int c = 0;

    for(size_t i = 0; i < strlen(newstr); ++i)
    {
        if(newstr[i] != '\n')
        {
            cpy[c] = newstr[i];
            c++;
        }
    }
    strcat(cpy, "\0");
    strcpy(str, cpy);
    free(cpy);
            free(newstr);
    return read_size;
}

char* readSock2(int socket, char *str) {

    int read_size = read(socket, str, 2000);
    printf("grandezza strlen: %d\n", strlen(str));
    char* newstr = (char*)malloc(sizeof(char)*strlen(str));
    
    for(int i = 0; str[i] != '\0' && i < strlen(str); i++) {newstr[i] = str[i];}
    
    fflush(stdout);
    memset(str, 0, strlen(str));
    return newstr;
}

