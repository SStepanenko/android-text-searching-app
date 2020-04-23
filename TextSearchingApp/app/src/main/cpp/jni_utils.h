//
//
//

#ifndef TEXTSEARCHINGAPP_JNI_UTILS_H
#define TEXTSEARCHINGAPP_JNI_UTILS_H

#include <string>
#include <jni.h>

class JniUtils
{
public:

    static std::string jniStringToString(JNIEnv *env, jstring jniString);
    static jstring stringToJniString(JNIEnv *env, const std::string& string);
};


#endif //TEXTSEARCHINGAPP_JNI_UTILS_H
