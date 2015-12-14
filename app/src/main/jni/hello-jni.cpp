#include "hello-jni.h"
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

JNIEXPORT jlong JNICALL Java_com_example_hellojni_HelloJni_stringFromJNI
        (JNIEnv * env, jobject obj, jobject assetManager)
{
    AAssetManager * mgr = AAssetManager_fromJava(env, assetManager);
    if (mgr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK", "error loading asset maanger");
        return 0;
    }
    AAsset * asset = AAssetManager_open(mgr, "file.txt", AASSET_MODE_STREAMING);
    if (asset == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK", "error loading file");
        return 0;
    } else {
        //long long * longTemp = new jlong[1];
        long long longTemp;

        AAsset_read (asset, &longTemp, sizeof(long long));
        AAsset_read (asset, &longTemp, sizeof(long long));
        AAsset_close(asset);

        return longTemp;
    }
};

JNIEXPORT void JNICALL Java_com_example_hellojni_MainActivity_nativDicInit
        (JNIEnv * env, jobject obj, jobject assetManager, jint lang)
{
    //Dic * dic = new Dic(env, assetManager);
    //dic.init(env, assetManager);
    track.initDic(env, assetManager, lang);
};

JNIEXPORT jboolean JNICALL Java_com_example_hellojni_MainActivity_nativFindWord
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

void Java_com_example_hellojni_MainActivity_nativTrackInit
        (JNIEnv *env, jobject obj, jlongArray _hashWords)
{
    long long * hashWords = env->GetLongArrayElements(_hashWords, 0);
    int hashWords_len = env->GetArrayLength(_hashWords);

    track.init(hashWords, hashWords_len);
}

void Java_com_example_hellojni_MainActivity_nativTrackIter
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

jlong JNICALL Java_com_example_hellojni_MainActivity_nativGetWord
        (JNIEnv * env, jobject obj)
{
    return (jlong) track.getWord();
};

jbyte JNICALL Java_com_example_hellojni_MainActivity_nativGetCharValue
        (JNIEnv *, jobject)
{
    return (jbyte) track.getCharValue();
}

jint JNICALL Java_com_example_hellojni_MainActivity_nativGetCharIndex
        (JNIEnv *, jobject)
{
    return (jint) track.getCharIndex();
}

jboolean Java_com_example_hellojni_MainActivity_nativFindWord
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
