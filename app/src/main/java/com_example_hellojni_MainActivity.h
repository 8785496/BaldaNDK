/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_hellojni_MainActivity */

#ifndef _Included_com_example_hellojni_MainActivity
#define _Included_com_example_hellojni_MainActivity
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_hellojni_MainActivity
 * Method:    nativDicInit
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_example_hellojni_MainActivity_nativDicInit
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_example_hellojni_MainActivity
 * Method:    nativTrackInit
 * Signature: ([J)V
 */
JNIEXPORT void JNICALL Java_com_example_hellojni_MainActivity_nativTrackInit
  (JNIEnv *, jobject, jlongArray);

/*
 * Class:     com_example_hellojni_MainActivity
 * Method:    nativTrackIter
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_com_example_hellojni_MainActivity_nativTrackIter
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     com_example_hellojni_MainActivity
 * Method:    nativGetWord
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_example_hellojni_MainActivity_nativGetWord
  (JNIEnv *, jobject);

/*
 * Class:     com_example_hellojni_MainActivity
 * Method:    nativGetCharValue
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_com_example_hellojni_MainActivity_nativGetCharValue
  (JNIEnv *, jobject);

/*
 * Class:     com_example_hellojni_MainActivity
 * Method:    nativGetCharIndex
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_hellojni_MainActivity_nativGetCharIndex
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif