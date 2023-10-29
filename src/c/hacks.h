#ifndef KLIBX_HACKS
#define KLIBX_HACKS

#include <pthread.h>

int klibx_pthread_getattr_np(pthread_t thread, pthread_attr_t *attr);

#endif