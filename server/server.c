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


void clientThread(int clientSocket)
{
    /*
    int success = 0;
    int userID;
    while(success == 0) //login
    {
        char buffer[80];
        read(clientSocket,buffer,4);
        userID = atoi(buffer);
        read(clientSocket,buffer,32);
        //success = areCredentialsTrue(userID,buffer);
        write(clientSocket,&buffer,4);
    }
    //Group* groups = getGroupsOfUsers(userID);
     */
}

void *connection_handler(void *);


int main(int argc, char *argv[])
{
    //variabili per connessione al db o altre cose inerenti alla gestione dati da db:
    PGconn *conn = NULL;
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

void *connection_handler(void *socket_desc)
{
    int sock = *(int*)socket_desc;
    int read_size;
    char *message , client_message[2000];

    message = "Giorgio sei un piscione\n";
    write(sock , message , strlen(message));

    while( (read_size = recv(sock , client_message , 2000 , 0)) > 0 )
    {
        //end of string marker
        client_message[read_size] = '\0';

        //Send the message back to client
        write(sock , client_message , strlen(client_message));

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