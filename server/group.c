#include <stdlib.h>
#include <string.h>
#include "pgconnection.h"
#include "group.h"


void reverse(char s[])
{
    int i, j;
    char c;

    for (i = 0, j = strlen(s)-1; i<j; i++, j--) {
        c = s[i];
        s[i] = s[j];
        s[j] = c;
    }
}


void itoa(int n, char s[])
{
    int i, sign;

    if ((sign = n) < 0)  /* record sign */
        n = -n;          /* make n positive */
    i = 0;
    do {       /* generate digits in reverse order */
        s[i++] = n % 10 + '0';   /* get next digit */
    } while ((n /= 10) > 0);     /* delete it */
    if (sign < 0)
        s[i++] = '-';
    s[i] = '\0';
    reverse(s);
}


Group* getGroupsOfUsers(int user_id, PGconn *conn, int *row)
{
    if(conn == NULL)
    {
        printf("Connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char));
    char where_condition[200] = "";
    char buffer[33];
    strcat(where_condition, " userid = ");
    itoa(user_id, buffer);
    strcat(where_condition, buffer);
    strcat(where_condition, " AND roomusers.roomid = room.roomid");
    queryResult = selectdb("DISTINCT roomusers.roomid, room.roomname, room.creatoruserid", "roomusers, room ", where_condition, conn, row, 3);
    Group *gruppi = (Group*) malloc(*row * sizeof(Group));
    int j = 0;
    for(int i = 0; i < *row; i = i + 1)
    {
        gruppi[i].groupId = atoi(queryResult[j+2]);
        strcpy(gruppi[i].groupName, queryResult[j+1]);
        gruppi[i].creatorUserId = atoi(queryResult[j]);
        j = j + 3;
    }
    return gruppi;
}

Group* getGroupsNotOfUsers(int user_id, PGconn *conn, int *row)
{
    if(conn == NULL)
    {
        printf("Connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char));
    char where_condition[1000] = "";
    char buffer[33];
    itoa(user_id, buffer);
    strcat(where_condition, " roomid NOT IN (SELECT DISTINCT room.roomid FROM roomusers, room WHERE roomusers.roomid = room.roomid AND userid = ");
    strcat(where_condition, buffer);
    strcat(where_condition, ")");
    queryResult = selectdb(" DISTINCT roomid, roomname, creatoruserid ", " room ", where_condition, conn, row, 3);

    Group *gruppi = (Group*) malloc(*row * sizeof(Group));
    int j = 0;
    for(int i = 0; i < *row; i = i + 1)
    {
        gruppi[i].groupId = atoi(queryResult[j+2]);
        strcpy(gruppi[i].groupName, queryResult[j+1]);
        gruppi[i].creatorUserId = atoi(queryResult[j]);
        j = j + 3;
    }
    return gruppi;
}


Group* getAllGroups(PGconn* conn, int *row)
{
    if(conn == NULL)
    {
        printf("Connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char));
    queryResult = selectdb("*", "Room", "", conn, row, 3);
    Group *gruppi = (Group*) malloc(*row * sizeof(Group));
    int j = 0;
    for(int i = 0; i < *row; i = i + 1)
    {
        gruppi[i].groupId = atoi(queryResult[j+2]);
        strcpy(gruppi[i].groupName, queryResult[j+1]);
        gruppi[i].creatorUserId = atoi(queryResult[j]);
        j = j + 3;
    }
    return gruppi;
}

GroupMessage* getGroupMessages(int group_id, PGconn *conn, int *row)
{
    if (conn == NULL)
    {
        printf("Connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char));
    char whereCondition[250] = " roomid = ";
    char buffer[33];
    itoa(group_id, buffer);
    strcat(whereCondition, buffer);
    queryResult = selectdb("messagetext, timestampdata, userid", "MessageData", whereCondition, conn, row, 3);
    GroupMessage *messaggi = (GroupMessage *) malloc(*row * sizeof(GroupMessage));
    int j = 0;
    for(int i = 0; i < *row; i = i + 1)
    {
        strcpy(messaggi[i].message, queryResult[j]);
        strcpy(messaggi[i].timestamp, queryResult[j+1]);
        messaggi[i].groupId = group_id;
        messaggi[i].userId = atoi(queryResult[j+2]);
        j = j + 3;
    }
    return messaggi;
}

int deleteGroup(char groupname[], PGconn* conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char where_condition[100];
    strcpy(where_condition, " roomname = ");
    strcat(where_condition, groupname);
    if(delete("room", where_condition, conn) == 0)
    {
        printf("Gruppo non esistente\n");
        return 0;
    }
    else
    {
        printf("Cancellazione avvenuta\n");
        return 1;
    }
}

GroupRequest* getGroupRequests(int group_id, int user_id, PGconn *conn, int *row)
{
    if (conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char));
    char whereCondition[250] = " roomid = ";
    char buffer[33];
    itoa(group_id, buffer);
    strcat(whereCondition, buffer);
    strcpy(buffer, "");
    strcat(whereCondition, " AND userid = ");
    itoa(user_id, buffer);
    strcat(whereCondition, buffer);
    queryResult = selectdb("room, userid", "joinrequest", whereCondition, conn, row, 2);
    GroupRequest *richieste = (GroupRequest*)malloc(*row * sizeof (GroupRequest));
    int j = 0;
    for(int i = 0; i < *row; i = i + 1)
    {
        richieste[i].groupId = atoi(queryResult[j]);
        richieste[i].userId = atoi(queryResult[j+1]);
        j = j + 2;
    }
    return richieste;
}

int richiestaGruppo(char group_name, int user_id, PGconn *conn, int *row)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char));
    char whereCondition[100] = "roomname = ";
    strcat(whereCondition, group_name);
    queryResult = selectdb("roomid", "room", whereCondition, conn, row, 1);
    char insertData[250];
    strcpy(insertData, "");
    strcat(insertData, queryResult[0]);
    strcat(insertData, ", ");
    char userid[250];
    strcpy(userid, "");
    itoa(user_id, userid);
    strcat(insertData, userid);
    return insert("joinrequest(roomid, userid)", insertData, conn);
}

int messaggioGruppo(char message, int user_id, int group_id, PGconn *conn, int *row)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char insertData[250];
    strcpy(insertData, "");
    strcat(insertData, message);
    strcat(insertData, ", ");
    char userid[250];
    char groupid[250];
    itoa(group_id, groupid);
    itoa(user_id, userid);
    strcpy(groupid, "");
    strcpy(userid, "");
    strcat(insertData, groupid);
    strcat(insertData, ", ");
    strcat(insertData, userid);
    return insert("messagedata(messagetext, roomid, userid)", insertData, conn);
}


int creaGruppo(char group_name, int creatorUserId, PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char userid[250];
    strcpy(userid, "");
    itoa(user_id, userid);
    char insertData[250];
    strcpy(insertData, "");
    strcat(insertData, group_name);
    strcat(insertData, ", ");
    itoa(user_id, userid);
    return insert("room(roomname, creatoruserid)", insertData, conn);
}