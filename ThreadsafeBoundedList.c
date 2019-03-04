#include "ThreadsafeBoundedList.h"

/*******************************************************************************
 * File: ThreadsafeBoundedList.c
 * Author: Jason Egbert
 * Class: cs453-1-s19
 * Defines the functions from the interface ThreadsafeBoundedList.h that wraps
 * around an unsynchronized linked list to create a threadsafe, bounded list. 
 ******************************************************************************/

/*******************************************************************************
 * Function: struct tsb_list 
 * Parameters: none
 * This is the declaration of the struct tsb_list object, to be used in this
 * library to store our list.
 ******************************************************************************/
struct tsb_list {
	struct list *list;
	int capacity;
	Boolean stop_requested;
	pthread_mutex_t mutex;
	pthread_cond_t listNotFull;
	pthread_cond_t listNotEmpty;
};

/*******************************************************************************
 * Function: tsb_createList
 * Parameters: *equals(const void *, const void *), *(*toString)(const void *),
 * (*freeObject)(void *), capacity
 * Constructor: Allocates a new list object and initializes its members.
 * Initialize the mutex and condition variables associated with the bounded list
 * monitor. Set the capacity of the list.
 ******************************************************************************/
struct tsb_list * tsb_createList(int (*equals)(const void *, const void *),
		char * (*toString)(const void *),
		void (*freeObject)(void *),
		int capacity) {

	struct tsb_list *newList = malloc(sizeof(struct tsb_list *));
	struct list *list = malloc(sizeof(struct list *));
	
	newList->list = list;
	newList->capacity = capacity;
	newList->stop_requested = FALSE;
	pthread_mutex_init(&newList->mutex, NULL);
	pthread_cond_init(&newList->listNotFull, NULL);
	pthread_cond_init(&newList->listNotEmpty, NULL);

	return newList;
}

/*******************************************************************************
 * Function: tsb_freeList
 * Parameters: <code>list</code>
 * Frees all elements of the givel list and the <code>struct *list</code>
 * istself. Does nothing if list is <code>NULL</code>. Also frees the associated
 * mutex and condition variables and the wrapper structure.
 ******************************************************************************/ 
void tsb_freeList(struct tsb_list * list) {
	if(list->list->size != 0) {
		// TODO: FREE THE NODES!!
	}
	free(list->list);
	pthread_mutex_destroy(&list->mutex);
	pthread_cond_destroy(&list->listNotFull);
	pthread_cond_destroy(&list->listNotEmpty);
	free(list);
}

/*******************************************************************************
 * Function: tsb_getSize
 * Parameters: <code>list</code>
 * returns the size of the given list
 ******************************************************************************/ 
int tsb_getSize(struct tsb_list * list) { 
	
	pthread_mutex_lock(&list->mutex);
	int retVal = list->list->size;
	pthread_mutex_unlock(&list->mutex);

	return retVal; 
}

/*******************************************************************************
 * Function: tsb_getCapacity
 * Parameters: <code>list</code>
 * Returns the maximum capacity of the given list.
 ******************************************************************************/
int tsb_getCapacity(struct tsb_list * list) { 
	
	pthread_mutex_lock(&list->mutex);
	int retVal = list->capacity;
	pthread_mutex_unlock(&list->mutex);

	return retVal; 
}

/*******************************************************************************
 * Function: tsb_setCapacity
 * Parameters: <code>list</code>, <code>capacity</code>
 * Sets the maximum capacity of the given list
 ******************************************************************************/ 
void tsb_setCapacity(struct tsb_list * list, int capacity) { 

	pthread_mutex_lock(&list->mutex);
	list->capacity = capacity;
	pthread_mutex_unlock(&list->mutex);
}

/*******************************************************************************
 * Function: tsb_isEmpty
 * Parameters: <code>list</code>
 * Checks if the list is empty.
 ******************************************************************************/ 
Boolean tsb_isEmpty(struct tsb_list * list) { 
	Boolean retval = TRUE;
	
	pthread_mutex_lock(&list->mutex);
	int size = list->list->size;
	pthread_mutex_unlock(&list->mutex);
	
	if(size != 0)
		retval = FALSE;

	return retval;
}

/*******************************************************************************
 * Function: tsb_isFull
 * Parameters: <code>list</code>
 * Checks if the list is full.
 ******************************************************************************/ 
Boolean tsb_isFull(struct tsb_list * list) { 
	Boolean retval = TRUE;

	pthread_mutex_lock(&list->mutex);
	int size = list->list->size;
	pthread_mutex_unlock(&list->mutex);
	
	if(size != list->capacity)
		retval = FALSE;

	return retval;
}

/*******************************************************************************
 * Function: tsb_addAtFront
 * Parameters: <code>list</code>, <code>node</code>
 * Adds a node to the front of the list. After this method is called, the given
 * node will be the head of the list. (Node must be allocated before it is
 * passed to this function.) If the list and/or node are NULL, the function will
 * do nothing and return.
 ******************************************************************************/ 
void tsb_addAtFront(struct tsb_list * list, NodePtr node) {
	pthread_mutex_lock(&list->mutex);
	while(/*TODO: get size of list*/ 1 == list->capacity)
		pthread_cond_wait(&list->listNotFull, &list->mutex);
	//TODO: add to front of list
	pthread_cond_signal(&list->listNotEmpty);
	pthread_mutex_unlock(&list->mutex);
}

/*******************************************************************************
 * Function: tsb_addAtRear
 * Parameters:<code>list</code>, <code>node</code>
 * Adds a node to the rear of the list. After this method is called, the given
 * node will be the tail of the list. (Node must be allocated before it is
 * passed to this function.) If the list and/or node are NULL, the function will
 * do nothing and return.
 ******************************************************************************/ 
void tsb_addAtRear(struct tsb_list * list, NodePtr node) {
	pthread_mutex_lock(&list->mutex);
	while(/*TODO: get size of list*/ 1 == list->capacity)
		pthread_cond_wait(&list->listNotFull, &list->mutex);
	// TODO: add to rear of list
	pthread_cond_signal(&list->listNotEmpty);
	pthread_mutex_unlock(&list->mutex);
}

/*******************************************************************************
 * Function: tsb_removeFront
 * Parameters: <code>list</code>
 * Removes the node from the front of the list (the head node) and returns a
 * pointer to the node that was removed. If the list is NULL or empty, the
 * function will do nothing and return NULL;
 ******************************************************************************/ 
NodePtr tsb_removeFront(struct tsb_list * list) { 
	NodePtr *retNode = NULL;
	
	pthread_mutex_lock(&list->mutex);
	while(/*TODO: get size of list*/ 1 == 0)
		pthread_cond_wait(&list->listNotEmpty, &list->mutex);
	//TODO: remove first node
	pthread_cond_signal(&list->listNotFull);
	pthread_mutex_unlock(&list->mutex);
	
	return retNode; 
}

/*******************************************************************************
 * Function: tsb_removeRear
 * Parameters: <code>list</code>
 * Removes the node from the rear of the list (the tail node) and returns a
 * pointer to the node that was removed. If the list is NULL or empty, the
 * function will do nothing and return NULL.
 ******************************************************************************/ 
NodePtr tsb_removeRear(struct tsb_list * list) { 
	NodePtr *retNode = NULL;

	pthread_mutex_lock(&list->mutex);
	while(/*TODO: get size of list*/ 1 == 0)
		pthread_cond_wait(&list->listNotEmpty, &list->mutex);
	//TODO: remove last node
	pthread_cond_signal(&list->listNotFull);
	pthread_mutex_unlock(&list->mutex);

	return retNode; 
}

/*******************************************************************************
 * Function: tsb_removeNode
 * Parameters: <code>list</code>, <code>node</code>
 * Removes the ndoe pointed to by the given node pointer from the list and
 * returns the pointer to it. Assumes that the node is a valid node in the list.
 * If the node pointer is NULL, the function will do nothing and return NULL.
 ******************************************************************************/ 
NodePtr tsb_removeNode(struct tsb_list * list, NodePtr node) { 
	NodePtr *retNode = NULL;

	pthread_mutex_lock(&list->mutex);
	while(list->list->size == 0)
		pthread_cond_wait(&list->listNotEmpty, &list->mutex);
	//TODO: remove specified node
	pthread_cond_signal(&list->listNotFull);
	pthread_mutex_unlock(&list->mutex);

	return retNode; 
}

/*******************************************************************************
 * Function: tsb_search
 * Parameters: <code>list</code>, <code>obj</code>
 * Searches the list for a node with the given key and returns the pointer to
 * the found node.
 ******************************************************************************/ 
NodePtr tsb_search(struct tsb_list * list, const void *obj) { 
	NodePtr *retNode = NULL;

	pthread_mutex_lock(&list->mutex);
	//TODO: find specified node
	pthread_mutex_unlock(&list->mutex);

	return retNode; 
}

/*******************************************************************************
 * Function: tsb_reverseList
 * Parameters: <code>list</code>
 * Reverses the order of the given list.
 ******************************************************************************/ 
void tsb_reverseList(struct tsb_list * list) {
	struct list *tmpList = malloc(sizeof(tmpList));
	
	pthread_mutex_lock(&list->mutex);
	//TODO: create new struct list, put all elements in it using add at front
	//method
	list->list = tmpList;
	pthread_mutex_unlock(&list->mutex);
	
	free(tmpList);
}

/*******************************************************************************
 * Function: tsb_printList
 * Parameters: <code>list</code>
 * Prints the list.
 ******************************************************************************/ 
void tsb_printList(struct tsb_list * list) {
	int max = list->capacity - 1;
	
	pthread_mutex_lock(&list->mutex);
	for(int i = 0; i <= max; i++) {
		printf("%d", list->list[i]);
		if(i != max)
			printf(", ");
		else
			printf("\n");
	}
	pthread_mutex_unlock(&list->mutex);
}

/*******************************************************************************
 * Function: tsb_finishUp
 * Parameters: <code>list</code>
 * Finish up the monitor by broadcasting to all waiting threads
 ******************************************************************************/ 
void tsb_finishUp(struct tsb_list * list) {
	pthread_cond_signal(&list->listNotEmpty);
	pthread_cond_signal(&list->listNotFull);
}

