#include <postgresql/libpq-fe.h>


typedef struct Group
{
    int groupId;
    int creatorUserId;
    char groupName[200];
}Group;

typedef struct GroupMessage
{
    int userId;
    int groupId;
    char message[1000];
    char timestamp[250];
}GroupMessage;


Group* getGroupsOfUsers(int user_id, PGconn *conn, int *row);

Group* getGroupsNotOfUsers(int user_id, PGconn *conn, int *row);

Group* getAllGroups(PGconn *conn, int *row);

void itoa(int n, char s[]);

void reverse(char s[]);

GroupMessage* getGroupMessages(int group_id, PGconn *conn, int *row);