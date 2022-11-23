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