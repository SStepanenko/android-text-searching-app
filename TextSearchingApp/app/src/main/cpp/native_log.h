#ifndef NATIVE_LOG_H
#define NATIVE_LOG_H

#include <android/log.h>

#define  LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif //NATIVE_LOG_H
