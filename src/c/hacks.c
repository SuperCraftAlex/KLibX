#include "hacks.h"

int klibx_pthread_getattr_np(pthread_t thread, pthread_attr_t *attr) {
    return pthread_getattr_np(thread, attr);
}