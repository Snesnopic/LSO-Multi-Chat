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
#include <signal.h>
#define PORTNUMBER 8989
#define MAXCLIENTS 50

typedef void (*sighandler_t)(int);
sighandler_t signal(int signum, sighandler_t handler);
void *connection_handler(void *);

int networkMessageHandler(int scelta, PGconn *conn, int socket);
long writeSock(int socket, char *str);
int readSock(int socket, char *str);
char* readSock2(int socket, char *str);
long writeSock2(int socket, char *str);
int readSockN(int socket, char *str);
void stopServer(int signal);

PGconn* conn = NULL;
int userID = -1;
int utentiConnessi[50] = {0};
int globalConnection;
int socketArray[MAXCLIENTS] = {0};
int socketInChat[MAXCLIENTS] = {0};



int main()
{
    struct sockaddr_in serverAddr;

    conn = dbConnection(conn);
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORTNUMBER);
    // Bind della socket
    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    int bindOK = -1;

    if(bind(serverSocket,(struct sockaddr*)&serverAddr,sizeof(serverAddr)) == -1)
    {
        printf("Errore nel binding..\n");
        return 1;
    }
    // Listening socket con max 40 richieste in coda
    if (listen(serverSocket, MAXCLIENTS) == -1)
    {
        printf("Errore nell'ascolto\n");
        return 1;
    }
    
    //invio dei signal handler
    signal(SIGTSTP, stopServer);
    signal(SIGTERM, stopServer);
    signal(SIGINT, stopServer);

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
        socketArray[i] = newSocket;
        i++;
    }
}

int networkMessageHandler(int scelta, PGconn *conn, int sock)
{
    char* client_message;
    int read_size;
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
    switch(scelta) {
        case 0: //login          
            //read per username
            
            client_message = (char*)malloc(sizeof(char)*200);
            memset(client_message, 0, 200);
            buff = readSock2(sock, client_message);
            printf("Username da parte del client: %s|\n", buff);
            fflush(stdout);   
            memset(client_message, 0, 200);

            //read per password

            buff2 = readSock2(sock, client_message);
            printf("Password da parte del client: %s|\n", buff2);
            fflush(stdout);
            free(client_message);
            
            userID = usernameAndPasswordCheck(buff, buff2, conn);
            printf("Sono userID: %d\n", userID);
            free(buff);
            free(buff2);
            return userID;
            
        case 1: //registrazione
            //read per username
            client_message = (char*)malloc(sizeof(char)*200);
            buff = readSock2(sock, client_message);
            printf("Username da parte del client: %s\n", buff);
            memset(client_message, 0, 200);
            fflush(stdout);
            

            //read per password
            buff2 = readSock2(sock, client_message);
            printf("Password da parte del client: %s\n", buff2);
            free(client_message);
            fflush(stdout);

            if(userRegistration(buff, buff2, conn)) {
                userID = usernameAndPasswordCheck(buff, buff2, conn);
                free(buff);
                free(buff2);
                return 1;
            }//1 se registrazione OK, 0 altrimenti
            else {
                free(buff);
                free(buff2);
                return 0;
            }
            
            
        case 2: //ottieni tutti i gruppi di un utente
              
              printf("User id del client: %d|", userID);
              fflush(stdout);
              
              
              groupid = (char*)malloc(sizeof(char)*10);
              creatorUserId = (char*)malloc(sizeof(char)*10);
              
              groupsOfUsers = getGroupsOfUsers(userID, conn, &row); //ottiene tutti i gruppi dell'utente 

              buff = (char*)malloc(sizeof(char)*10);

              itoa(row, buff);

              writeSock(sock, buff);
              free(buff);

              for(int i = 0; i < row; i++) {
                  itoa(groupsOfUsers[i].groupId, groupid);

                  writeSock2(sock, groupid); //scrive il groupid
                  itoa(groupsOfUsers[i].creatorUserId, creatorUserId); //scrive l'userid del creatore

                  writeSock2(sock, creatorUserId);
                  writeSock2(sock, groupsOfUsers[i].groupName);
                  memset(groupid, 0, 10);
                  memset(creatorUserId, 0, 10);
              }
              
              printf("Gruppi inviati.\n");
              free(groupid);
              free(creatorUserId);
              fflush(stdout);

              return -2;
        case 3:
            printf("User id del client: %d|", userID);
            fflush(stdout);

            groupid = (char*)malloc(sizeof(char)*10);
            creatorUserId = (char*)malloc(sizeof(char)*10);

            otherGroups = getGroupsNotOfUsers(userID, conn, &row); //ottiene tutti i gruppi in cui l'utente non c'è

            buff = (char*)malloc(sizeof(char)*10);

            itoa(row, buff);

            writeSock2(sock, buff);
            
            free(buff);

            for(int i = 0; i < row; i++) {

                itoa(otherGroups[i].groupId, groupid);

                writeSock2(sock, groupid); //scrive il groupid
                itoa(otherGroups[i].creatorUserId, creatorUserId); //scrive l'userid del creatore

                writeSock2(sock, creatorUserId);

                writeSock2(sock, otherGroups[i].groupName);
                
                memset(groupid, 0, 10);
                memset(creatorUserId, 0, 10);
            }

            printf("Gruppi inviati.\n");
            free(groupid);
            free(creatorUserId);
            fflush(stdout);

            return -2;
            
        case 4: 
            client_message = (char*)malloc(10*sizeof(char));
            buff = (char*)malloc(10*sizeof(char));
            int groupID;

            groupID = readSockN(sock, client_message);
            printf("group id: %d\n", groupID); 
            messaggi = getGroupMessages(groupID, conn, &row); //ottiene tutti i messaggi del gruppo            
            memset(buff, 0, 10);
            itoa(row, buff);
                    
            writeSock2(sock, buff);
            free(buff);
            
            if(row > 0) {
            
                for(int j = 0; j < row; j++){
                    writeSock2(sock, messaggi[j].username);
                    writeSock2(sock, messaggi[j].message);
                    writeSock2(sock, messaggi[j].timestamp);  
                    
                }
                printf("Messaggi inviati.\n"); }
                else { printf("Nessun messaggio inviato su questo gruppo: %d\n", row); }
                    
            return -2;
        case 5:
            client_message = (char*)malloc(10*sizeof(char));

            int group_ID = readSockN(sock, client_message);
            printf("group id: %d\n", group_ID);
            memset(client_message, 0, 10);
            
            int user_id = readSockN(sock, client_message);
            printf("user id: %d\n", user_id);


            richieste = getGroupRequests(group_ID, user_id, conn, &row);

            itoa(row, client_message);

            writeSock2(sock, client_message);
            

            if(row > 0)
            {
              
                for(int j = 0; j < row; j++) {
                  
                   writeSock2(sock, richieste[j].username);
                   itoa(richieste[j].userId, client_message);
                   printf("DEBUG PREVETE: %d\n", richieste[j].userId);
                   writeSock2(sock, client_message);
                }
                
                free(client_message);
                printf("Richieste inviate\n");
                return -2;
            }
            else {
                free(client_message);
                printf("Nessuna richiesta per questo gruppo: %d\n", row);
                return -2;
            }
                
        case 6:   //Ottieni messaggio dal client
           printf("Sono in case 6..\n");
           fflush(stdout);
            char* message = (char*)malloc(2000*sizeof(char));
            char* time_stamp = (char*)malloc(100*sizeof(char));
            char* userName = (char*)malloc(200*sizeof(char));
            int user_ID = 0;
            int group_id = 0;

            buff = readSock2(sock, message);
            printf("Messaggio: %s\n", buff);
            fflush(stdout);
            strcpy(message, buff);
            free(buff);

            buff = readSock2(sock, userName);
            printf("username: %s\n", buff);
            fflush(stdout);
            strcpy(userName, buff);
            free(buff);

            buff = readSock2(sock, time_stamp);
            printf("timestamp messaggio: %s\n", buff);
            fflush(stdout);
            strcpy(time_stamp, buff);
            free(buff);
            
            client_message = (char*)malloc(10*sizeof(char));

            buff = readSock2(sock, client_message);
            user_ID = atoi(buff);
            printf("user id messaggio: %d\n", user_ID);
            fflush(stdout);
            free(buff);
            memset(client_message, 0, 10);

            buff = readSock2(sock, client_message);
            group_id = atoi(buff);
            printf("group id messaggio: %d\n", group_id);
            fflush(stdout);
            

            status = messaggioGruppo(message, user_ID, group_id, time_stamp, conn, &row);
            
            for(int i = 0; i < 50; i++) {
                if(socketInChat[i] != 0) {
                    writeSock2(socketInChat[i], "-777");
                    writeSock2(socketInChat[i], userName);
                    writeSock2(socketInChat[i], message);
                    writeSock2(socketInChat[i], buff); //buff sarebbe il group id
                }
            }
            
 
            free(buff);
            free(message);
            free(time_stamp);
            free(userName);
                
            return -2;

        case 7:
            client_message = (char*)malloc(200*sizeof(char));
            char* nome_gruppo = readSock2(sock, client_message);
            printf("nome del nuovo gruppo: %s\n", nome_gruppo);
            free(client_message);
            client_message = (char*)malloc(10*sizeof(char));

            char* userID = readSock2(sock, client_message);
            user_id = atoi(userID);
            printf("id del creatore del nuovo gruppo: %d\n", user_id);
            free(client_message);

            status = creaGruppo(nome_gruppo, user_id, conn);
            if(status) {
                buff = (char*)malloc(10*sizeof(char));
                itoa(status, buff);
                writeSock2(sock, buff);
                free(buff);
                free(nome_gruppo);
                free(userID);
                return 1;
            }
            else {
                writeSock2(socket, "-1");
                return -1;
            }
            
        case 8:
            char* nuovo_username = (char*)malloc(200*sizeof(char));
            client_message = (char*)malloc(200*sizeof(char));

            buff = readSock2(sock, client_message);
            strcpy(nuovo_username, buff);
            printf("nuovo username: %s\n", nuovo_username);
            memset(client_message, 0 , 200);
            free(buff);

            buff = readSock2(socket, client_message);
            user_id = atoi(buff);
            printf("id dell'utente che sta modificando il suo username: %d\n", user_id);
            free(buff);
            free(client_message);

            status = modificaUsername(nuovo_username, user_id, conn);
            free(nuovo_username);
            if(status == 1)
                return 1;
            else
                return -1;
        case 9:
            char* nuova_pass = (char*)malloc(200*sizeof(char));
            client_message = (char*)malloc(200*sizeof(char));

            buff = readSock2(sock, client_message);
            strcpy(nuova_pass, buff);
            printf("nuova password: |%s|\n", nuova_pass);
            memset(client_message, 0 , 200);

            buff = readSock2(sock, client_message);
            user_id = atoi(buff);
            printf("id dell'utente che sta modificando la sua password: %d\n", user_id);
            free(buff);
            free(client_message);

            status = modificaPassword(nuova_pass, user_id, conn);
            free(nuova_pass);
            if(status == 1)
                return 1;
            else
                return -1;
        case 10:
            char* nuovo_nomegruppo = (char*)malloc(250*sizeof(char));
            client_message = (char*)malloc(200*sizeof(char));


            buff = readSock2(sock, client_message);
            strcpy(nuovo_nomegruppo, buff);
            printf("nuovo nome gruppo: %s\n", nuovo_nomegruppo);
            free(buff);
            free(client_message);
  
            client_message = (char*)malloc(10*sizeof(char));
            buff = readSock2(sock, client_message);
            group_id = atoi(buff);
            printf("id del gruppo che sta venendo modificato: %d\n", group_id);
            free(buff);
            free(client_message);

            status = modificaPassword(nuovo_nomegruppo, group_id, conn);
            free(nuovo_nomegruppo);
            if(status == 1)
                return 1;
            else
                return -1;
                
        case 11: //ottenimemento di tutti gruppi che hanno almeno una richiesta E sono del proprietario (LE RICHIESTE TOTALI SONO ESCLUSE. RESTITUISCE **SOLO** I GRUPPI!!)
            //Il case è cursato. Non chiedere, se funziona, lasciala stare
            client_message = (char*)malloc(10*sizeof(char));
            char* creatorUserID = readSock2(sock, client_message);
            printf("Creator user id: %s|\n", creatorUserID);

            Group* requestGroups;
            getRequestGroups(conn, creatorUserID, &row, &requestGroups);
            fflush(stdout);
            for(int i = 0; i < row; i++) {
                 printf("\n*****************STAMPO IL GRUPPO IN SERVER.C*************\n\ncreatorUserID: %d\ngroupName: %s\ngroupID: %d\n\n********************************************\n", requestGroups[i].creatorUserId, requestGroups[i].groupName, requestGroups[i].groupId);

            }
            fflush(stdout);
            
            buff = (char*)malloc(200*sizeof(char));
            itoa(row, buff);
            writeSock2(sock, buff);
            memset(buff, 0, 200);
            
             
    

            if(row <= 0) {
                printf("Non sono presenti richieste in alcun gruppo.\n");
                return -2;
            }
            else {

                for(int i = 0; i < row; i++) { 
                    itoa(requestGroups[i].groupId, buff);
                    writeSock2(sock, buff);
                    printf("%s|\n", buff);
                    memset(buff, 0, 200);
                    strcpy(buff, requestGroups[i].groupName);
                    printf("%s|\n", buff);
                    writeSock2(sock, buff);
                    memset(buff, 0, 200);
                    printf("\nSono row: %d, sono i: %d\n", row, i);
                }
                
                printf("Gruppi richieste inviati.\n");
                fflush(stdout);
                return -2;
            }
            
        case 12: //manda richiesta gruppo
            client_message = (char*)malloc(10*sizeof(char));
            buff = (char*)malloc(10*sizeof(char));
            buff2 = (char*)malloc(10*sizeof(char));

            
            buff = readSock2(sock, client_message);
            memset(client_message, 0, 10);
            buff2 = readSock2(sock, client_message);
            status = richiestaGruppo(atoi(buff2), atoi(buff), conn);
            free(buff);
            free(buff2);
            if(status) return 1;
            else return -1;
            
        case 13: //elimina gruppo
        
        client_message = (char*)malloc(sizeof(char)*10);
        int case13gid = readSockN(sock, client_message);
        free(client_message);
        
        status = deleteGroup(case13gid, conn);
        if(status) return 1;
        else return -1;
         
        case 14:    //lascia gruppo
        
        client_message = (char*)malloc(sizeof(char)*10);
        int userid = readSockN(sock, client_message);
        
        int gid = readSockN(sock, client_message);
        memset(client_message, 0, 10);
        
        
        status = leaveGroup(gid, userid, conn);
        if(status) return 1;
        else return -1;
        
        case 15:  //accetta richiesta gruppo
            client_message = (char*)malloc(10*sizeof(char));
            userid = readSockN(sock, client_message);
            memset(client_message, 0, 10);
            gid = readSockN(sock, client_message);
            free(client_message);
            return addUser(userid, gid, conn);
            
        case 16:  //rifiuta richiesta gruppo
            client_message = (char*)malloc(10*sizeof(char));
            userid = readSockN(sock, client_message);
            memset(client_message, 0, 10);
            gid = readSockN(sock, client_message);
            free(client_message);
            
            return refuseUser(userid, gid, conn);
        
        
        case 999:
            int j;
            for(j = 0; j < MAXCLIENTS; j++) {
                if(socketInChat[j] == 0) break;
            }
            for(int i = 0; i < 50; i++) {
                if(sock == socketArray[i]) {
                    socketInChat[j] = socketArray[i];
                    j++;
                }
            }
            
            return -2;
        case 888:
            for(int j = 0; j < MAXCLIENTS; j++) {
                if(socketInChat[j] == sock) socketInChat[j] = 0;
            }
            
            return -2;
        default:
            printf("Errore network message hanlder: valore di scelta non valido\n");
            return -2;
    }
}


void *connection_handler(void *socket_desc)
{
    int sock = *(int*)socket_desc;
    int read_size;
    char* client_message = (char*) malloc(sizeof(char)*200);
    
    while((read_size = recv(sock , client_message , 200 , 0)) > 0)
    {
        writeSock2(sock, "-80");
        //read per intero che stabilirà cosa fare
        int scelta = atoi(client_message);
        client_message[read_size] = '\0';
        printf("Intero da parte del client: %s\n", client_message);
        memset(client_message, 0, 200);
        int msg = networkMessageHandler(scelta, conn, sock);
        char mess[33];
        itoa(msg, mess);
        fflush(stdout);
        printf("messaggio della send: %s\n", mess);
        if(msg != -2) writeSock2(sock, mess);

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
    return NULL;
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
    int i;
    
    for(i = 0; str[i] != '\n' && i < strlen(str); i++) {
        newstr[i] = str[i];
    }
    strcat(newstr, "\0");
    writeSock2(socket, "-80");
    
    fflush(stdout);
    return newstr;
}

int readSockN(int socket, char *str) {

    int read_size = read(socket, str, 10);
    read_size = atoi(str);

    writeSock2(socket, "-80");
    
    return read_size;
}


long writeSock2(int socket, char *str)
{
    long bytes = write(socket, str, strlen(str));
    write(socket, "\n", 1);
    return bytes;
}

void stopServer(int signal) {
      printf("\nHo ricevuto un segnale di terminazione. Chiudo la socket..\n");
      for(int i = 0; i < MAXCLIENTS; i++) {
            if(socketArray[i] != 0) close(socketArray[i]);
      }
      
      printf("Tutte le socket chiuse.\n");
      exit(1);
}
