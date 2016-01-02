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

void Java_es_hol_chernyshov_balda_MainActivity_nativDicInit
        (JNIEnv * env, jobject obj, jobject assetManager, jint lang)
{
    //Dic * dic = new Dic(env, assetManager);
    //dic.init(env, assetManager);
    track.initDic(env, assetManager, lang);
};

jboolean Java_es_hol_chernyshov_balda_MainActivity_nativFindWord
        (JNIEnv *env, jobject instance, jint chars_len, jbyteArray __chars)
{
    jbyte * _chars = env->GetByteArrayElements(__chars, 0);

    unsigned char * chars = new unsigned char[chars_len];
    for(int i=0;i<chars_len;i++){
        chars[i] = (unsigned char)_chars[i];
    }
    //return dic.findWord(chars_len, chars);
    //return track.dic.findWord(chars_len, chars);

    unsigned char arrData[25] = {
            0, 0, 0, 0, 0,
            0, 2, 0, 0, 0,
            2, 1, 12, 5, 1,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0
    };
    int coordinatesWord[2] = {6, 11};
    int coordinatesWord_len = 2;
    int cur = 12;
    int ins = 6;
    //track.findTrack(arrData, coordinatesWord, coordinatesWord_len, cur, ins);
    track.init(new long long[1] {2421013}, 1);
    track.iter(arrData, 25);

    long long word = track.getWord();
    std::string number;
    std::stringstream strstream;
    strstream << word;
    strstream >> number;
    __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK word", number.c_str());

    long long charValue = track.getCharValue();
    std::stringstream strstream1;
    strstream1 << charValue;
    strstream1 >> number;
    __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK charValue", number.c_str());

    long long charIndex = track.getCharIndex();
    std::stringstream strstream2;
    strstream2 << charIndex;
    strstream2 >> number;
    __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK charIndex", number.c_str());

    return true;
    //findTrack(unsigned char * arrData, int * coordinatesWord, int coordinatesWord_len, int cur, int ins)
}

void Java_es_hol_chernyshov_balda_MainActivity_nativTrackInit
        (JNIEnv *env, jobject obj, jlongArray _hashWords)
{
    long long * hashWords = env->GetLongArrayElements(_hashWords, 0);
    int hashWords_len = env->GetArrayLength(_hashWords);

    track.init(hashWords, hashWords_len);
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

jlongArray Java_es_hol_chernyshov_balda_MainActivity_nativHelp
        (JNIEnv *env, jobject jobject1) {
    jlongArray jlongArray1 = env->NewLongArray(1);
    jlong * jlongBuf = new jlong[1];
    jlongBuf[0] = 122;
    env->SetLongArrayRegion(jlongArray1, 0, 1, jlongBuf);
    delete[] jlongBuf;
    return jlongArray1;
    // TODO
}
