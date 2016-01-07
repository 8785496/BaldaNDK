#include "balda.h"
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <string>
#include <sstream>
#include "Dic.h"
#include "Track.h"

//Dic dic;
Track track;

#include <string>

jlongArray Java_es_hol_chernyshov_balda_MainActivity_nativHelp
        (JNIEnv *env, jobject instance) {
    std::vector<jlong> wordsHelp = track.getWordsHelp();
    int n = (int) wordsHelp.size();
    jlongArray jlongArray1 = env->NewLongArray(n);
    jlong * jlongBuf = new jlong[n];
    for (int i = 0; i < n; i++) {
        jlongBuf[i] = wordsHelp[i];
    }
//    jlongBuf[0] = 122L;
//    jlongBuf[1] = 123L;
    env->SetLongArrayRegion(jlongArray1, 0, n, jlongBuf);
    delete[] jlongBuf;
    return jlongArray1;
    // TODO
}

jlong Java_es_hol_chernyshov_balda_MainActivity_nativCountWordLen5
        (JNIEnv *env, jobject instance) {
    return track.dic.countWordLen5();
}

jlong Java_es_hol_chernyshov_balda_MainActivity_nativRandomWord
        (JNIEnv *env, jobject instance, jint index) {
    return track.dic.randomWord(index);
}

void Java_es_hol_chernyshov_balda_MainActivity_nativDicInit
        (JNIEnv * env, jobject obj, jobject assetManager, jint lang)
{
    //Dic * dic = new Dic(env, assetManager);
    //dic.init(env, assetManager);
    track.initDic(env, assetManager, lang);
};

void Java_es_hol_chernyshov_balda_MainActivity_nativTrackInit
        (JNIEnv *env, jobject obj, jlongArray _hashWords, jint complexity)
{
    long long * hashWords = env->GetLongArrayElements(_hashWords, 0);
    int hashWords_len = env->GetArrayLength(_hashWords);

    track.init(hashWords, hashWords_len, complexity);
}

void Java_es_hol_chernyshov_balda_MainActivity_nativTrackIter
        (JNIEnv *env, jobject obj, jbyteArray _space)
{
    jbyte * space = env->GetByteArrayElements(_space, 0);
    unsigned char * arrData = new unsigned char[25];
    for (int i = 0; i < 25; i++) {
        arrData[i] = (unsigned char) space[i];
    }
    track.iter(arrData, 25);
    delete[] arrData;
}

jlong Java_es_hol_chernyshov_balda_MainActivity_nativGetWord
        (JNIEnv * env, jobject obj)
{
    return (jlong) track.getWord();
};

jbyte Java_es_hol_chernyshov_balda_MainActivity_nativGetCharValue
        (JNIEnv *, jobject)
{
    return (jbyte) track.getCharValue();
}

jint Java_es_hol_chernyshov_balda_MainActivity_nativGetCharIndex
        (JNIEnv *, jobject)
{
    return (jint) track.getCharIndex();
}

jboolean Java_es_hol_chernyshov_balda_MainActivity_nativFindWord
        (JNIEnv *env, jobject jobject1, jbyteArray _bytes)
{
    jbyte * bytes = env->GetByteArrayElements(_bytes, 0);
    int chars_len = env->GetArrayLength(_bytes);

    unsigned char * chars = new unsigned char[chars_len];
    for (int i = 0; i < chars_len; i++) {
        chars[i] = (unsigned char) bytes[i];
    }
    jboolean result = (jboolean) track.dic.findWord(chars_len, chars);

    return result;
}

void Java_es_hol_chernyshov_balda_MainActivity_nativDestruct
        (JNIEnv *env, jobject obj) {
    track.~Track();
}
