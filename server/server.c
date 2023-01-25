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
long writeSock2(int socket, char *str);

PGconn* conn = NULL;
int userID = -1;

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
    Group* groupsOfUsers;
    Group* otherGroups;
    int row;
    char* groupid;
    char* creatorUserId;
    GroupMessage * messaggi;
    GroupRequest * richieste;
    int status;
    switch(scelta)
    {
        case '0': //login          
            //read per username
            char* client_message2 = (char*)malloc(sizeof(char)*200);
            buff = readSock2(socket, client_message2);
            printf("Username da parte del client: %s| Socket: %d\n", buff, socket);
            fflush(stdout);

            //read per password
            printf("\nSto per leggere la password.\n");
            printf("Sono qui?\n");
            buff2 = readSock2(socket, client_message2);
            printf("Password da parte del client: %s|\n", buff2);
            fflush(stdout);

            userID = usernameAndPasswordCheck(buff, buff2, conn);
            printf("Sono userID: %d\n", userID);
            memset(buff, 0, strlen(buff));
            memset(buff2, 0, strlen(buff2));
            return userID;
        case '1': //registrazione
            //read per username
            buff = readSock2(socket, client_message);
            printf("Username da parte del client: %s\n", buff);
            fflush(stdout);

            //read per password
            buff2 = readSock2(socket, client_message);
            printf("Password da parte del client: %s\n", buff2);
            fflush(stdout);

            if(userRegistration(buff, buff2, conn)) {
                userID = usernameAndPasswordCheck(buff, buff2, conn);
                return 1;
            }//1 se registrazione OK, 0 altrimenti
            else return 0;
            
            
        case '2': //ottieni tutti i gruppi di un utente
              printf("User id del client: %d|", userID);
              fflush(stdout);
              
              groupid = (char*)malloc(sizeof(char)*200);
              creatorUserId = (char*)malloc(sizeof(char)*200);
              
              groupsOfUsers = getGroupsOfUsers(userID, conn, &row); //ottiene tutti i gruppi dell'utente 

              buff = (char*)malloc(sizeof(char)*500);

              itoa(row, buff);

              writeSock(socket, buff);

              for(int i = 0; i < row; i++) {
                  itoa(groupsOfUsers[i].groupId, groupid);
                  writeSock(socket, groupid); //scrive il groupid
                  itoa(groupsOfUsers[i].creatorUserId, creatorUserId); //scrive l'userid del creatore
                  writeSock(socket, creatorUserId);
                  writeSock(socket, groupsOfUsers[i].groupName);
                  memset(groupid, 0, 200);
                  memset(creatorUserId, 0, 200);
              }
              
              printf("Gruppi inviati.\n");

              return -2;
        case '3':
            printf("User id del client: %d|", userID);
            fflush(stdout);

            groupid = (char*)malloc(sizeof(char)*200);
            creatorUserId = (char*)malloc(sizeof(char)*200);

            otherGroups = getGroupsNotOfUsers(userID, conn, &row); //ottiene tutti i gruppi in cui l'utente non c'è

            buff = (char*)malloc(sizeof(char)*500);

            itoa(row, buff);

            writeSock(socket, buff);

            for(int i = 0; i < row; i++) {

                itoa(otherGroups[i].groupId, groupid);

                writeSock2(socket, groupid); //scrive il groupid
                itoa(otherGroups[i].creatorUserId, creatorUserId); //scrive l'userid del creatore

                writeSock2(socket, creatorUserId);

                writeSock2(socket, otherGroups[i].groupName);
                
                memset(groupid, 0, 200);
                memset(creatorUserId, 0, 200);
            }

            printf("Gruppi inviati.\n");
                          

            return -2;
            
        case '4': 
            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            int groupID = -1;

            groupID = atoi(buff);
            printf("group id: %d\n", groupID); 
            messaggi = getGroupMessages(groupID, conn, &row); //ottiene tutti i messaggi del gruppo            
            memset(buff, 0, 200);
            itoa(row, buff);
                    
            writeSock2(socket, buff);
            memset(buff, 0, 200);
            
            if(row > 0) {
            
                for(int j = 0; j < row; j++){
                    writeSock2(socket, messaggi[j].username);
                    writeSock2(socket, messaggi[j].message);
                    writeSock2(socket, messaggi[j].timestamp);  
                    memset(buff, 0, 200);
                }
                printf("Messaggi inviati.\n"); }
                else { printf("Nessun messaggio inviato su questo gruppo: %d\n", row); }
                    
            return -2;
        case '5':
            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            int group_ID = -1;
            groupID = atoi(buff);
            printf("group id: %d\n", group_ID);

            strcpy(buff, "");
            read(socket, buff, 200);
            int user_id = 0;
            user_id = atoi(buff);
            printf("user id: %d\n", user_id);

            richieste = getGroupRequests(group_ID, user_id, conn, &row);
            memset(buff, 0, 200);
            itoa(row, buff);

            writeSock2(socket, buff);
            memset(buff, 0, 200);
            if(row > 0)
            {
                for(int j = 0; j < row; j++)
                {
                    itoa(richieste->userId, user_id);
                    itoa(richieste->groupId, group_ID);
                    writeSock2(socket, user_id);
                    writeSock2(socket, group_ID);
                    memset(buff, 0, 200);
                }
                printf("Richieste inviate\n");
            }
            else
                printf("Nessuna richiesta per questo gruppo: %d\n", row);
        case '6':
            char message[250];
            strcpy(message, "");
            char time_stamp[250];
            strcpy(time_stamp, "");
            char userName[250];
            strcpy(userName, "");
            int user_ID = 0;
            int group_id = 0;

            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            printf("Messaggio: %s\n", buff);
            strcpy(message, buff);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            printf("username: %s\n", buff);
            strcpy(userName, buff);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            printf("timestamp messaggio: %s\n", buff);
            strcpy(time_stamp, buff);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            user_ID = atoi(buff);
            printf("user id messaggio: %d\n", user_ID);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            group_id = atoi(buff);
            printf("user id messaggio: %d\n", user_ID);
            memset(buff, 0 , 200);

            status = messaggioGruppo(message, user_ID, group_id, time_stamp, conn, &row);
            if(status == 1)
                return 1;
            else
                return -1;
        case '7':
            char nome_gruppo[250];
            strcpy(nome_gruppo, "");

            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            strcpy(nome_gruppo, buff);
            printf("nome del nuovo gruppo: %s\n", nome_gruppo);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            user_id = atoi(buff);
            printf("id del creatore del nuovo gruppo: %d\n", user_id);
            memset(buff, 0, 200);

            status = creaGruppo(nome_gruppo, user_id, conn);
            if(status == 1)
                return 1;
            else
                return -1;
        case '8':
            char nuovo_username[250];
            strcpy(nuovo_username, "");

            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            strcpy(nuovo_username, buff);
            printf("nuovo username: %s\n", nuovo_username);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            user_id = atoi(buff);
            printf("id dell'utente che sta modificando il suo username: %d\n", user_id);
            memset(buff, 0, 200);

            status = modificaUsername(nuovo_username, user_id, conn);
            if(status == 1)
                return 1;
            else
                return -1;
        case '9':
            char nuova_pass[250];
            strcpy(nuova_pass, "");

            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            strcpy(nuova_pass, buff);
            printf("nuova password: %s\n", nuova_pass);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            user_id = atoi(buff);
            printf("id dell'utente che sta modificando la sua password: %d\n", user_id);
            memset(buff, 0, 200);

            status = modificaPassword(nuovo_username, user_id, conn);
            if(status == 1)
                return 1;
            else
                return -1;
        case '10':
            char nuovo_nomegruppo[250];
            strcpy(nuovo_nomegruppo, "");

            buff = (char*)malloc(sizeof(char)*200);
            read(socket, buff, 200);
            strcpy(nuovo_nomegruppo, buff);
            printf("nuovo nome gruppo: %s\n", nuovo_nomegruppo);
            memset(buff, 0 , 200);

            read(socket, buff, 200);
            group_id = atoi(buff);
            printf("id del gruppo che sta venendo modificato: %d\n", user_id);
            memset(buff, 0, 200);

            status = modificaPassword(nuovo_nomegruppo, group_id, conn);
            if(status == 1)
                return 1;
            else
                return -1;
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
        if(msg != -2) writeSock(sock, mess);

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

char* readSock2(int socket, char *str) {

    int read_size = read(socket, str, 2000);
    char* newstr = (char*)malloc(sizeof(char)*strlen(str));
    
    for(int i = 0; str[i] != '\n' && i < strlen(str); i++) {
        newstr[i] = str[i];
    }
    writeSock2(socket, "-80");
    
    fflush(stdout);
    memset(str, 0, strlen(str));
    return newstr;
}

long writeSock2(int socket, char *str)
{
    long bytes = write(socket, str, strlen(str));
    write(socket, "\n", 1);
    return bytes;
}


/*
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
*/

