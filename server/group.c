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
        gruppi[i].groupId = atoi(queryResult[j]);
        strcpy(gruppi[i].groupName, queryResult[j+1]);
        gruppi[i].creatorUserId = atoi(queryResult[j+2]);
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
        gruppi[i].groupId = atoi(queryResult[j]);
        strcpy(gruppi[i].groupName, queryResult[j+1]);
        gruppi[i].creatorUserId = atoi(queryResult[j+2]);
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
    char whereCondition[250] = " MessageData.roomid = ";
    char buffer[33];
    itoa(group_id, buffer);
    strcat(whereCondition, buffer);
    strcat(whereCondition, " AND MessageData.userid = UserData.userid");
    queryResult = selectdb("MessageData.messagetext, MessageData.timestampdata, UserData.userName", "MessageData, UserData", whereCondition, conn, row, 3);
    int mem = *row;
    GroupMessage *messaggi = (GroupMessage *) malloc((mem+1)* sizeof(GroupMessage));

    int j = 0;
    for(int i = 0; i < *row; i = i + 1)
    {
        strcpy(messaggi[i].message, queryResult[j]);
        strcpy(messaggi[i].timestamp, queryResult[j+1]);
        messaggi[i].groupId = group_id;
        strcpy(messaggi[i].username, queryResult[j+2]);
        j = j + 3;
    }
    return messaggi;
}

int deleteGroup(int groupID, PGconn* conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char* where_condition = (char*)malloc(250*sizeof(char));
    char* roomID = (char*)malloc(10*sizeof(char));
    itoa(groupID, roomID);
    memset(where_condition, 0, 250);
    strcpy(where_condition, " roomid = ");
    strcat(where_condition, roomID);
    puts(where_condition);
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

int leaveGroup(int groupID, int userID, PGconn* conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char* where_condition = (char*)malloc(250*sizeof(char));
    char* roomID = (char*)malloc(10*sizeof(char));
    char* userid = (char*)malloc(10*sizeof(char));
    memset(where_condition, 0, 250);
    memset(roomID, 0, 10);
    memset(userid, 0, 10);
    
    itoa(userID, userid);
    itoa(groupID, roomID);

    strcpy(where_condition, " roomid = ");
    strcat(where_condition, roomID);
    strcat(where_condition, " AND userid = ");
    strcat(where_condition, userid);
    puts(where_condition);
    if(delete("roomusers", where_condition, conn) == 0)
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
        queryResult[i] = (char*)malloc(200*sizeof(char));
    char whereCondition[250] = " j.roomid = ";
    char buffer[33];
    itoa(group_id, buffer);
    strcat(whereCondition, buffer);
    strcpy(buffer, "");
    strcat(whereCondition, " AND j.userid = u.userid");
    queryResult = selectdb("u.username, j.userid, j.roomid", "joinrequest AS j, USERDATA AS u", whereCondition, conn, row, 3);
    GroupRequest *richieste = (GroupRequest*)malloc(*row * sizeof (GroupRequest));
    int j = 0;

    for(int i = 0; i < *row; i = i + 1)
    {
        richieste[i].username = queryResult[j];
        j++;
        richieste[i].groupId = atoi(queryResult[j]);
        j++;
        richieste[i].userId = atoi(queryResult[j]);
        j++;
    }
    return richieste;
}

int richiestaGruppo(int roomid, int user_id, PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char* roomID = (char*)malloc(sizeof(char)*10);
    char* userID = (char*)malloc(sizeof(char)*10);
    itoa(roomid, roomID);
    itoa(user_id, userID);
    char insertData[250];
    
    strcpy(insertData, "");
    strcat(insertData, roomID);
    strcat(insertData, ", ");
    strcat(insertData, userID);
    return insert("joinrequest(roomid, userid)", insertData, conn);
}

int messaggioGruppo(char *message, int user_id, int group_id, char *timestamp, PGconn *conn, int *row)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    for(int i = 0; i < strlen(timestamp); i++) {
        if(timestamp[i] == 'T') timestamp[i] = ' ';
    }
    char insertData[3000];
    strcpy(insertData, " '");
    strcat(insertData, message);
    strcat(insertData, "', ");
    char userid[250];
    char groupid[250];
    itoa(group_id, groupid);
    itoa(user_id, userid);
    strcat(insertData, groupid);
    strcat(insertData, ", ");
    strcat(insertData, userid);
    strcat(insertData, ", '");
    strcat(insertData, timestamp);
    strcat(insertData, "'");
    return insert("messagedata(messagetext, roomid, userid, timestampdata)", insertData, conn);
}


int creaGruppo(char *group_name, int creatorUserId, PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char userid[250];
    strcpy(userid, "");
    itoa(creatorUserId, userid);
    char insertData[250];
    strcpy(insertData, "'");
    strcat(insertData, group_name);
    strcat(insertData, "', ");
    itoa(creatorUserId, userid);
    strcat(insertData, userid);

    if(insert("room(roomname, creatoruserid)", insertData, conn)) {
        char* whereCondition = (char*)malloc(sizeof(char)*2000);
        char* userID = (char*)malloc(200*sizeof(char));
        int row;
        strcpy(whereCondition, "roomname = '");
        strcat(whereCondition, group_name);
        strcat(whereCondition, "' AND creatoruserid = ");
        itoa(creatorUserId, userID);
        strcat(whereCondition, userID);
        char **res = selectdb("roomid", "room", whereCondition, conn, &row, 1);
        return atoi(res[0]);
    }
    
    return -1;
}

int modificaNomeGruppo(char newGroupName[], int group_id, PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char groupid[250];
    strcpy(groupid, "");
    itoa(group_id, groupid);
    char whereCondition[250];
    strcpy(whereCondition, "");
    strcat(whereCondition, "roomid = ");
    strcat(whereCondition, groupid);
    return update("room", "roomname", whereCondition, conn);
}

void getRequestGroups(PGconn* conn, char* creatorUserID, int *row, Group** gruppi) {
    if(conn == NULL)
    {
        printf("Connessione con DB persa o assente\n");
        exit(0);
    }
    
    char **queryResult = (char**)malloc(50 * sizeof(char*));
    for(int i = 0; i < 100; i++)
        queryResult[i] = (char*)malloc(1000*sizeof(char));
        
    char* whereCond = (char*)malloc(200*sizeof(char));
    strcat(whereCond, "creatoruserid = ");
    strcat(whereCond, creatorUserID);
    strcat(whereCond, " AND roomid in (select roomid from joinrequest)");
        
    queryResult = selectdb("*", "Room", whereCond, conn, row, 3);
    *gruppi = (Group*)malloc(*row * sizeof(Group));

    free(whereCond);

    int j = 0;

    for(int i = 0; i < (*row); i++)
    {
        gruppi[i]->creatorUserId = atoi(queryResult[j]);
        
        j++;
        strcpy(gruppi[i]->groupName, queryResult[j]);
        
      
        j++;
        gruppi[i]->groupId = atoi(queryResult[j]);
        
        j++;
        
        printf("\n*****************STAMPO IL GRUPPO*************\n\ncreatorUserID: %d\ngroupName: %s\ngroupID: %d\n\n***********************************************\n", gruppi[i]->creatorUserId, gruppi[i]->groupName, gruppi[i]->groupId);
        fflush(stdout);

    }
    
    printf("Gruppi inviati\n");
    fflush(stdout);

}
