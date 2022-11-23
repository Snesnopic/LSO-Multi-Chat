#include <stdlib.h>
#include <string.h>
#include "pgconnection.h"
#include "group.h"

/*Group* getGroupsOfUsers(int user)
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

int groupListSize(Group head)
{
    int i = 0;
    while(head != NULL)
    {
        ++i;
        head = head->next;
    }
    return i;
}
*/

int stringsArraySize(char ** str)
{
    int i = 0;
    while(str[i] != NULL)
    {
        i++;
    }
    return i;
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
    queryResult = selectdb("*", "Room", "", conn, row);
    Group *gruppi = (Group*) malloc(*row * sizeof(Group));
    int j = 0;
    for(int i = 0; i < *row * 2; i = i + 2)
    {
        gruppi[i].groupId = atoi(queryResult[j+1]);
        strcpy(gruppi[i].groupName, queryResult[j]);
        j = j + 2;
    }
    return gruppi;
}