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



int areCredentialsTrue(int id, char* password)
{

    return 1;
}



void clientThread(int clientSocket)
{
    int success = 0;
    int userID;
    while(success == 0) //login
    {
        char buffer[80];
        read(clientSocket,buffer,4);
        userID = atoi(buffer);
        read(clientSocket,buffer,32);
        success = areCredentialsTrue(userID,buffer);
        write(clientSocket,&buffer,4);
    }
    //Group* groups = getGroupsOfUsers(userID);



}

int main(int argc, char *argv[])
{   /*
    //variabili per connessione al db o altre cose inerenti alla gestione dati da db:
    PGconn *conn;
    Group *groups = NULL;
    groups = (Group*)malloc(100 * sizeof (Group));
    int rows;
    conn = dbConnection(conn);
    groups = getGroupsNotOfUsers(0, conn, &rows);
    for(int i = 0; i < rows; i++)
    {
        printf("Riga %d:\n Group ID: %d\n", i, groups[i].groupId);
        printf("Group name: %s\n", groups[i].groupName);
        printf("Creator User ID: %d\n", groups[i].creatorUserId);
        printf("--------\n");
    }
    */


    int serverSocket;
    struct sockaddr_in serverAddr;
    struct sockaddr_storage serverStorage;

    socklen_t addr_size;


    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(8989);
    printf("giorgio\n");
    // Bind della socket
    if(bind(serverSocket,(struct sockaddr*)&serverAddr,sizeof(serverAddr)) == -1)
    {
        printf("Errore nel binding\n");
        return 1;
    }
    else
    {
        printf("OK binding\n");
    }
    printf("giorgio\n");
    // Listening socket con max 40 richieste in coda
    if (listen(serverSocket, 50) == -1)
    {
        printf("Errore nell'ascolto\n");
        return 1;
    }
    else
    {
        printf("OK listen");
    }
    printf("giorgio\n");
    // Array di thread
    pthread_t tid[60];
    int i = 0;
    printf("giorgio\n");
    fflush(stdout);
   while (1)
    {
        fflush(stdout);
        printf("Attendo...");
        fflush(stdout);
        // Estrae la prima richiesta dalla coda
        int newSocket = accept(serverSocket, NULL,NULL);
        int choice = 0;
        pthread_create(&tid[i], NULL, (void *(*)(void *)) clientThread, &newSocket);
        i++;
        printf("Connesso\n");
    }
    //free(groups);
    //dbDeconnection(conn);
    return 0;
}
