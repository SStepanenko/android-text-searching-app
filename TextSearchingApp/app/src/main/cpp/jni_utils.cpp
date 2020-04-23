//
//
//

#include "jni_utils.h"

std::string JniUtils::jniStringToString(JNIEnv *env, jstring jniString)
{
    if (env == nullptr)
    {
        throw std::invalid_argument("env argument in null");
    }
    if (jniString == nullptr)
    {
        throw std::invalid_argument("jniString argument in null");
    }

    const char* string = env->GetStringUTFChars(jniString, nullptr) ;

    if (string == nullptr)
    {
        throw std::runtime_error("Failed to convert jstring to std::string");
    }

    std::string resultString(string);
    env->ReleaseStringUTFChars(jniString, string);

    return resultString;
}

jstring JniUtils::stringToJniString(JNIEnv *env, const std::string& string)
{
    return env->NewStringUTF(string.c_str());
}
