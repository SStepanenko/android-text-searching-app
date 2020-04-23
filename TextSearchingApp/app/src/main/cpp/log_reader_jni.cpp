#include <jni.h>

#include "native_log.h"
#include "log_reader.h"
#include "jni_utils.h"

static const char* LOG_TAG = "LogReaderJni";

#ifdef __cplusplus
extern "C" {
#endif

class LogReaderContext final
{
public:

    static LogReaderContext& instance()
    {
        return sInstance;
    }

    static LogReader* toPtr(jlong logReaderPtr)
    {
        return reinterpret_cast<LogReader*>(logReaderPtr);
    }

    void initialize(JNIEnv* env, jclass clazz);

    void callOnNewStringFoundMethod(JNIEnv* env, jobject obj, jstring newString);

private:

    LogReaderContext();

    void checkInitialization();

    static LogReaderContext sInstance;

    bool mInitialized;

    jmethodID mOnNewStringFoundMethodId;
};

LogReaderContext LogReaderContext::sInstance = LogReaderContext();

LogReaderContext::LogReaderContext()
{
    mInitialized = false;
    mOnNewStringFoundMethodId = nullptr;
}

void LogReaderContext::initialize(JNIEnv* env, jclass clazz)
{
    if (mInitialized)
    {
        return;
    }
    mOnNewStringFoundMethodId = env->GetMethodID(clazz, "onNewStringFound", "(Ljava/lang/String;)V");
    mInitialized = true;
}

void LogReaderContext::callOnNewStringFoundMethod(JNIEnv* env, jobject object, jstring newString)
{
    checkInitialization();
    env->CallVoidMethod(object, mOnNewStringFoundMethodId, newString);
}

void LogReaderContext::checkInitialization()
{
    if (!mInitialized)
    {
        throw std::runtime_error("LogReaderContext not initialized, initialize() wasn't called");
    }
}

JNIEXPORT
void JNICALL Java_com_sstepanenko_textsearchingapp_domain_LogReader_nativeClassInit(JNIEnv* env, jclass clazz)
{
    LOGD("nativeClassInit");
    LogReaderContext::instance().initialize(env, clazz);
}

JNIEXPORT
jlong JNICALL Java_com_sstepanenko_textsearchingapp_domain_LogReader_nativeCreate(JNIEnv* env, jclass clazz)
{
    LOGD("nativeCreate");
    auto pLogReader = new LogReader();
    return reinterpret_cast<jlong>(pLogReader);
}

JNIEXPORT
void JNICALL Java_com_sstepanenko_textsearchingapp_domain_LogReader_nativeDestroy(JNIEnv* env, jclass clazz, jlong logReaderPtr)
{
    LOGD("nativeDestroy");
    LogReader* pLogReader = LogReaderContext::toPtr(logReaderPtr);
    delete pLogReader;
}

JNIEXPORT
jboolean JNICALL Java_com_sstepanenko_textsearchingapp_domain_LogReader_nativeSetFilter(JNIEnv* env, jclass clazz, jlong logReaderPtr,
        jstring filter)
{
    try
    {
        LOGD("nativeSetFilter");
        LogReader* pLogReader = LogReaderContext::toPtr(logReaderPtr);

        std::string filterString = JniUtils::jniStringToString(env, filter);

        pLogReader->setFilter(filterString);
    }
    catch(std::exception& exception)
    {
        LOGE("nativeSetFilter error: %s", exception.what());
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT
jboolean JNICALL Java_com_sstepanenko_textsearchingapp_domain_LogReader_nativeReadLine(JNIEnv* env, jobject object, jlong logReaderPtr,
                                                                                       jstring line)
{
    try
    {
        LOGD("nativeReadLine");
        LogReader* pLogReader = LogReaderContext::toPtr(logReaderPtr);

        std::string lineString = JniUtils::jniStringToString(env, line);

        if(pLogReader->readLine(lineString))
        {
            LogReaderContext::instance().callOnNewStringFoundMethod(env, object, line);
        }
    }
    catch(std::exception& exception)
    {
        LOGE("nativeReadLine error: %s", exception.what());
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

#ifdef __cplusplus
}
#endif
