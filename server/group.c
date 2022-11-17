#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "group.h"


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
