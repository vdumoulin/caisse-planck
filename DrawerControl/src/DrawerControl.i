%include "exception.i"

/*
 * Libraries à importer dans le wrapper.
 */
%module DrawerControl
%{
#include "DrawerControl.h"
%}

/*
 * Code de gestion d'exceptions à ajouter au wrapper généré par SWIG pour interfacer le C et le Java.
 */
%javaexception("java.lang.Exception") {
    char *err;
    clear_exception();

    $action

    err = check_exception();

    if (err!=NULL) {
       SWIG_exception(SWIG_IOError,err);
    }
}

/*
 * Fonctions/Objets qu'on veut interfacer en java
 */
%include "enums.swg"
typedef enum {OPEN,CLOSED,UNKNOWN} Status;

void openCommunication(void);
void closeCommunication(void);
void openDrawer(void);
Status getStatus(void);
