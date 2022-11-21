#include <stdlib.h>
#include <string.h>
#include "group.h"
#include "pgconnection.h"


Group* getGroupsOfUsers(int user)
{
    Group *g = malloc(sizeof(Group));
    g->next = NULL;
    g->groupId = 1;
    strcpy(g->groupName, "ciccio");
    return g;
}


Group *getGroupNotOfUsers(int user)
{
    Group *g = malloc(sizeof(Group));
    g->next = NULL;
    g->groupId = 1;
    strcpy(g->groupName, "ciccio");
    return g;
}

int groupListSize(Group* head)
{
    int i = 0;
    while(head != NULL)
    {
        ++i;
        head = head->next;
    }
    return i;
}

char** getAllGroups(PGconn* conn)
{
    if(conn == NULL)
    {
        printf("Connessione con DB persa o assente\n");
        exit(0);
    }
    char **queryResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 50; i++)
        queryResult[i] = (char*)malloc(100*sizeof(char ));
    queryResult = selectdb("*", "Room", " ", conn);
    return queryResult;
}