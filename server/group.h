#include <postgresql/libpq-fe.h>

typedef struct Group
{
    int groupId;
    char groupName[200];
    struct Group *next;
}Group;

Group* getGroupsOfUsers(int user);

Group* getGroupsNotOfUsers(int user);

int groupListSize(Group* head);

char** getAllGroups(PGconn *conn);