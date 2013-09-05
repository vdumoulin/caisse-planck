/*
 * Librairies
 */
#include <stdio.h>
#include <string.h>
#include <hid.h>

/*
 * Typedefs
 */
typedef enum {OPEN,CLOSED,UNKNOWN} Status;

/*
 * Prototypes
 */
void openCommunication(void);
void closeCommunication(void);
void openDrawer(void);
Status getStatus(void);

void throw_exception(char*,int);
void clear_exception(void);
char* check_exception(void);
void enforceException(void);
