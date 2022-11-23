#include <postgresql/libpq-fe.h>


typedef struct Group
{
    int groupId;
    char groupName[200];
}Group;




Group* getGroupsOfUsers(int user);

Group* getGroupsNotOfUsers(int user);

int groupListSize(Group head);

Group* getAllGroups(PGconn *conn, int *row);

int stringArraySize(char ** str);