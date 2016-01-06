/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class es_hol_chernyshov_balda_MainActivity */

#ifndef _Included_es_hol_chernyshov_balda_MainActivity
#define _Included_es_hol_chernyshov_balda_MainActivity
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativCountWordLen5
        (JNIEnv *env, jobject instance);

JNIEXPORT jlong JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativRandomWord
        (JNIEnv *env, jobject instance, jint index);

JNIEXPORT jlongArray JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativHelp
        (JNIEnv *, jobject);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativDestruct
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativDestruct
  (JNIEnv *, jobject);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativDicInit
 * Signature: (Ljava/lang/Object;I)V
 */
JNIEXPORT void JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativDicInit
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativTrackInit
 * Signature: ([J)V
 */
JNIEXPORT void JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativTrackInit
  (JNIEnv *, jobject, jlongArray);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativTrackIter
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativTrackIter
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativGetWord
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativGetWord
  (JNIEnv *, jobject);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativGetCharValue
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativGetCharValue
  (JNIEnv *, jobject);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativGetCharIndex
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativGetCharIndex
  (JNIEnv *, jobject);

/*
 * Class:     es_hol_chernyshov_balda_MainActivity
 * Method:    nativFindWord
 * Signature: ([B)Z
 */
JNIEXPORT jboolean JNICALL Java_es_hol_chernyshov_balda_MainActivity_nativFindWord
  (JNIEnv *, jobject, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
