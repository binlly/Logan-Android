

#ifndef ANDROID_NOVA_LOGAN_CLOGAN_PROTOCOL_H_H
#define ANDROID_NOVA_LOGAN_CLOGAN_PROTOCOL_H_H
#ifdef __cplusplus
extern "C"
{
#endif

#include <jni.h>
#include <clogan_core.h>

/**
 * JNI write interface
 */
JNIEXPORT jint JNICALL
Java_com_dianping_logan_CLoganProtocol_clogan_1write(JNIEnv *env, jobject instance,
                                                          jint flag, jstring log_,
                                                          jlong local_time, jstring thread_name_,
                                                          jlong thread_id, jint ismain);
/**
 * JNI init interface
 */
JNIEXPORT jint JNICALL
Java_com_dianping_logan_CLoganProtocol_clogan_1init(JNIEnv *env, jobject instance,
                                                         jstring cache_path_,
                                                         jstring dir_path_, jint max_file,
                                                         jstring encrypt_key16_,
                                                         jstring encrypt_iv16_);

/**
 * JNI open interface
 */
JNIEXPORT jint JNICALL
Java_com_dianping_logan_CLoganProtocol_clogan_1open(JNIEnv *env, jobject instance,
                                                         jstring file_name_);

/**
 * JNI flush interface
 */
JNIEXPORT void JNICALL
Java_com_dianping_logan_CLoganProtocol_clogan_1flush(JNIEnv *env, jobject instance);

/**
 * JNI debug interface
 */
JNIEXPORT void JNICALL
Java_com_dianping_logan_CLoganProtocol_clogan_1debug(JNIEnv *env, jobject instance,
                                                          jboolean is_debug);


#ifdef __cplusplus
}
#endif

#endif //ANDROID_NOVA_LOGAN_CLOGAN_PROTOCOL_H_H
