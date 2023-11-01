#ifndef KLIBX_HACKS
#define KLIBX_HACKS

#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#include <pthread.h>
#undef _GNU_SOURCE
#else
#include <pthread.h>
#endif

#define FEATURE_NOT_SUPPORTED -123

int klibx_pthread_getattr_np(pthread_t thread, pthread_attr_t *attr);

int klibx_pthread_getname_np(pthread_t thread, char *name, size_t len);
int klibx_pthread_setname_np(pthread_t thread, const char *name);

int klibx_pthread_get_sched_priority(pthread_attr_t *attr);
void klibx_pthread_set_sched_priority(pthread_attr_t *attr, int priority);

#endif