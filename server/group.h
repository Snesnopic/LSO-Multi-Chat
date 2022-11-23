#include <postgresql/libpq-fe.h>


typedef struct Group
{
    int groupId;
    int creatorUserId;
    char groupName[200];
}Group;




Group* getGroupsOfUsers(int user_id, PGconn *conn, int *row);

Group* getGroupsNotOfUsers(int user_id, PGconn *conn, int *row);

Group* getAllGroups(PGconn *conn, int *row);

void itoa(int n, char s[]);

void reverse(char s[]);