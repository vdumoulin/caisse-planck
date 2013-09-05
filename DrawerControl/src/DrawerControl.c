/*
 * Ce code doit être distribué sous les termes du GPL, car c'est un dérivé de libhd, distribué sous GPL
 * Fait par Vincent Aymong, 22 septembre 2011.
 *
 * Ce programme prends 1 argument:
 * "open": Ouvrir le tiroir. Retourne "ok" si le message d'ouverture a été envoyé.
 * "status": Détermine l'état du tiroir. Retourne "open" ou "closed" selon le cas.
 *
 * S'il y a erreur, retourne une brève explication.
 *
 * ATTENTION: NE FONCTIONNE QUE SI LE TIROIR EST À l'ADDRESSE 0.
 * DE PLUS, COMPORTEMENT INDÉFINI SI PLUSIEURS TIROIRS SONT CONNECTÉS EN MÊME TEMPS.
 */

#include "DrawerControl.h"

/*
 * CONSTANTES
 */
//Concernant la communication avec le tiroir
const HIDInterfaceMatcher DEVICE_ID = { 0x06e5, 0x0001, NULL, NULL, 0 }; //Identificateur unique du tiroir

//Concernant l'envoi de messages
const int SEND_PATH[]            = {0xffa00001,0xffa00002,0xffa10006}; //Adresse d'envoi de messages
const unsigned int SENDPATH_SIZE = sizeof(SEND_PATH)/2;			       //Taille de l'adresse, en int (et un int est 2 octets, donc il faut diviser en 2)
const char OPEN_DRAWER_COMMAND[] = {0xff,0x00};                        //Commande pour ouvrir le tiroir
const unsigned int COMMAND_SIZE  = sizeof(OPEN_DRAWER_COMMAND);        //Taille du message/commande, en octets.

//Concernant la réception de messages
const char RECIEVE_ADDRESS       = 0x81; //Adresse d'envoi de reception de message
const char DRAWER_IS_CLOSED_MSG  = 0x00; //Message recu si le tiroir est fermé
const char DRAWER_IS_OPEN_MSG    = 0x80; //Message recu si le tiroir est ouvert
const unsigned int RESPONSE_SIZE = 2;    //Taille d'un message recu de la caisse (en octets)
const unsigned int TIMEOUT       = 1000; //Temps maximal d'attente d'une réponse du tiroir. (en ms)

//Réponses possibles du programme
const char IS_OPEN_RESPONSE[]   = "open\n";   //Le tiroir est ouvert
const char IS_CLOSED_RESPONSE[] = "closed\n"; //Le tiroir est fermé
const char SUCCESS_RESPONSE[]   = "ok\n";     //Le message d'ouverture du tiroir a été envoyé

//Arguments possibles du programme
const char GET_STATUS_ARGUMENT[]   = "status";   //Le tiroir est ouvert
const char OPEN_DRAWER_ARGUMENT[] = "open"; //Le tiroir est fermé

/*
 * VARIABLES GLOBALE
 */
static HIDInterface* hid = NULL; //Pointeur représentant le communicateur HID/USB;

/*
 * FONCTIONS
 */

//Ouvre la communication avec le tiroir
void openCommunication(){
	if(hid == NULL){
		hid_return ret;

		//Initialise la librairie du communicateur HID/USB
		ret = hid_init();
		if (ret != HID_RET_SUCCESS) {
			throw_exception("hid_init failed with return code %d\n",ret);
			return;
		}

		//Crée un objet communicateur
		hid = hid_new_HIDInterface();
		if (hid == 0) {
			throw_exception("hid_new_HIDInterface() failed, out of memory?\n",-1);
			return;
		}

		//Ouvre la communication avec le tiroir
		ret = hid_force_open(hid, 0, &DEVICE_ID, 3);
		if (ret != HID_RET_SUCCESS) {
			throw_exception("hid_force_open failed with return code %d\n",ret);
			return;
		}
	}else{
		throw_exception("communication already opened\n",-1);
		return;
	}
}

//Ferme la communication avec le tiroir
//Ici, un échec n'envoie pas le message d'erreur sur stdio, car la fonction principale du programme
//   a déja été accomplie avec succès.
void closeCommunication(){
	if(hid == NULL){
		throw_exception("communication not opened\n",-1);
		return;
	}else{
		hid_return ret;

		ret = hid_close(hid);
		if (ret != HID_RET_SUCCESS) {
			throw_exception("hid_close failed with return code %d\n",ret);
			return;
		}

		hid = NULL;
	
		hid_delete_HIDInterface(&hid);
	
		ret = hid_cleanup();
		if (ret != HID_RET_SUCCESS) {
			throw_exception("hid_cleanup failed with return code %d\n",ret);
			return;
		}


	}	
}

void openDrawer(){
	if(hid == NULL){
		throw_exception("communication not opened\n",-1);
		return;
	}else{
		hid_return ret;

		//Si la commande est d'ouvrir le tiroir, envoie le message approprié.
		ret = hid_set_output_report(hid, SEND_PATH, SENDPATH_SIZE, OPEN_DRAWER_COMMAND, COMMAND_SIZE);
		if (ret != HID_RET_SUCCESS) {
			throw_exception("hid_set_output_report failed with return code %d\n",ret);
			return;
		}
	}
}

Status getStatus(){
	if(hid == NULL){
		throw_exception("communication not opened\n",-1);
		return(UNKNOWN);
	}else{
		hid_return ret;
		char packet[RESPONSE_SIZE];
	
		//Si l'action est de déterminer l'état du tiroir, recoit un message du tiroir...
		ret = hid_interrupt_read(hid,RECIEVE_ADDRESS,packet,RESPONSE_SIZE,TIMEOUT);
		if (ret != HID_RET_SUCCESS) {
			throw_exception("hid_get_input_report failed with return code %d\n",ret);
			return(UNKNOWN);
		}
	
		//... et détermine l'état du tiroir avec le message recu.
		if(packet[0]==DRAWER_IS_CLOSED_MSG) return(CLOSED);
		else if(packet[0]==DRAWER_IS_OPEN_MSG) return(OPEN);
		else{
			throw_exception("bad message from drawer: %xd",packet[0]);
			return(UNKNOWN);
		}
	}
}

/*
 * MAIN pour exécution standalone
 */

int main(int argc, char *argv[]){
   //Vérifie le nombre d'arguments
   if(argc<2) throw_exception("Not enough arguments: Second argument must be \"open\" or \"status\"\n", -1);
   enforceException();

   //Ouvre la communication
   openCommunication();
   enforceException();

   //Choisi une action selon l'argument
   if(strcmp(argv[1],OPEN_DRAWER_ARGUMENT)==0){
      //Si on doit ouvrir le tiroir, envoie le commande
      openDrawer();
      enforceException();

      //Et confirme
      printf(SUCCESS_RESPONSE);
   }else if(strcmp(argv[1],GET_STATUS_ARGUMENT)==0){
	//Si on doit retourner l'état du tiroir, demande au tiroir son état:
	Status st = getStatus();
	enforceException();
	 
	//Et on print le résultat
	switch(st){
	   case OPEN:
	      printf(IS_OPEN_RESPONSE);
	   break;
	   case CLOSED:
	      printf(IS_CLOSED_RESPONSE);
	   break;
	   default:
	      throw_exception("Bad drawer status\n", -1);
	      enforceException();
	   }
   }else{
      //Si argument-commande invalide; on affiche une erreur
      throw_exception("Bad argument: Second argument must be \"open\" or \"status\"\n", -1);
      enforceException();
   }

   //Enfin, on ferme la communication après l'action
   closeCommunication();
   enforceException();

   exit(0);   
}

/*
 * Gestions d'exceptions pour SWIG/Java
 */

//Buffer d'erreur
static char error_message[256];
static int error_status = 0;

//Lancer une exception
void throw_exception(char *msg, int errcode) {
	snprintf(error_message,256,msg,errcode);
	error_status = 1;
}

//Réinitialiser l'état d'éxception
void clear_exception() {
	error_status = 0;
}

//Vérifie si une exception est survenue; et si oui, retroune l'adresse mémoire de son message explicatif. Sinon, retourne NULL
char* check_exception() {
	if (error_status) return error_message;
	else return NULL;
}

//Fallback pour un code purement C. Puisqu'il n'y a pas de gestion d´exception automatisée, print l'exception sur STDERR, et quitte le programme.
//A invoquer après un call à toute fonction pouvant appeler throw_exception();
void enforceException(){
   char *msg;

   if((msg = check_exception()) != NULL){
	fprintf(stderr,"%s",msg);
	exit(EXIT_FAILURE);
   }
}
