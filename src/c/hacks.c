#include "hacks.h"

int klibx_pthread_getattr_np(pthread_t thread, pthread_attr_t *attr) {
    return pthread_getattr_np(thread, attr);
}

int pthread_getname_np (pthread_t __target_thread, char *__buf, size_t __buflen)
    __attribute__((weak));

int pthread_setname_np (pthread_t __target_thread, const char *__name)
    __attribute__((weak));

int klibx_pthread_getname_np(pthread_t thread, char *name, size_t len) {
    if (pthread_getname_np) {
        return pthread_getname_np(thread, name, len);
    }
    return FEATURE_NOT_SUPPORTED;
}

int klibx_pthread_setname_np(pthread_t thread, const char *name) {
    if (pthread_setname_np) {
        return pthread_setname_np(thread, name);
    }
    return FEATURE_NOT_SUPPORTED;
}

int klibx_pthread_get_sched_priority(pthread_attr_t *attr) {
    struct sched_param param;
    pthread_attr_getschedparam(attr, &param);
    return param.sched_priority;
}

void klibx_pthread_set_sched_priority(pthread_attr_t *attr, int priority) {
    struct sched_param param;
    pthread_attr_getschedparam(attr, &param);
    param.sched_priority = priority;
    pthread_attr_setschedparam(attr, &param);
}