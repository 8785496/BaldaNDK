//#include "stdafx.h"
#include <iostream>
#include <jni.h>
#include <fstream>
#include <math.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <string>
#include <sstream>
#include "Dic.h"

using namespace std;

Dic::Dic(){}

void Dic::init(JNIEnv * env, jobject assetManager, jint lang) {
    AAssetManager * mgr = AAssetManager_fromJava(env, assetManager);
    if (mgr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK", "error loading asset maanger");
    }
    AAsset * asset;
    if (lang == 1) {
        asset = AAssetManager_open(mgr, "data_en.bin", AASSET_MODE_STREAMING);
    } else {
        asset = AAssetManager_open(mgr, "data.bin", AASSET_MODE_STREAMING);
    }
    if (asset == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK", "error loading file");
    } else {
        //long long * longTemp = new jlong[1];
        long long longVar;

        AAsset_read(asset, &longVar, sizeof(long long));
        dictionary_len = (int)longVar;
        dictionary = new long long[dictionary_len];
        AAsset_read(asset, dictionary, sizeof(long long) * dictionary_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic2_more_len = (int)longVar;
        dic2_more = new long long[dic2_more_len];
        AAsset_read(asset, dic2_more, sizeof(long long) * dic2_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic3_more_len = (int)longVar;
        dic3_more = new long long[dic3_more_len];
        AAsset_read(asset, dic3_more, sizeof(long long) * dic3_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic4_more_len = (int)longVar;
        dic4_more = new long long[dic4_more_len];
        AAsset_read(asset, dic4_more, sizeof(long long) * dic4_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic5_more_len = (int)longVar;
        dic5_more = new long long[dic5_more_len];
        AAsset_read(asset, dic5_more, sizeof(long long) * dic5_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic6_more_len = (int)longVar;
        dic6_more = new long long[dic6_more_len];
        AAsset_read(asset, dic6_more, sizeof(long long) * dic6_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic7_more_len = (int)longVar;
        dic7_more = new long long[dic7_more_len];
        AAsset_read(asset, dic7_more, sizeof(long long) * dic7_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic8_more_len = (int)longVar;
        dic8_more = new long long[dic8_more_len];
        AAsset_read(asset, dic8_more, sizeof(long long) * dic8_more_len);

        AAsset_read(asset, &longVar, sizeof(long long));
        dic9_more_len = (int)longVar;
        dic9_more = new long long[dic9_more_len];
        AAsset_read(asset, dic9_more, sizeof(long long) * dic9_more_len);

        AAsset_close(asset);
    }

    std::string number; std::stringstream strstream;

    //strstream << dictionary_len; strstream >> number;
    //strstream << dic9_more_len; strstream >> number;
    __android_log_print(ANDROID_LOG_DEBUG, "BaldaNDK", "Dictionary init");
//    cout << "Dic.Dic() dic2_more_len = " << dic2_more_len << endl;
//    cout << "Dic.Dic() dic3_more_len = " << dic3_more_len << endl;
//    cout << "Dic.Dic() dic4_more_len = " << dic4_more_len << endl;
//    cout << "Dic.Dic() dic5_more_len = " << dic5_more_len << endl;
//    cout << "Dic.Dic() dic6_more_len = " << dic6_more_len << endl;
//    cout << "Dic.Dic() dic7_more_len = " << dic7_more_len << endl;
//    cout << "Dic.Dic() dic8_more_len = " << dic8_more_len << endl;
//    cout << "Dic.Dic() dic9_more_len = " << dic9_more_len << endl;

    //for (int i = 0; i < 10; i++) {
    //	cout << dic2_more[i] << endl;
    //}
}

Dic::~Dic() {
    delete [] dictionary;
    delete [] dic2_more;
    delete [] dic3_more;
    delete [] dic4_more;
    delete [] dic5_more;
    delete [] dic6_more;
    delete [] dic7_more;
    delete [] dic8_more;
    delete [] dic9_more;
    __android_log_print(ANDROID_LOG_ERROR, "BaldaNDK", "~Dic()");
}

long long Dic::charsToHash(int chars_len, unsigned char *chars) {
    long long id = 0;
    unsigned char sym;
    for (int i = 0; i < chars_len; i++) {
        sym = chars[chars_len - i - 1];
        id += sym * (long long) std::pow((double)33, i);
    }
    return id;
}

bool Dic::findWord(int chars_len, unsigned char *chars) {
    return findHash(charsToHash(chars_len, chars), dictionary, dictionary_len);
}

bool Dic::findHash(long long searchKey, long long *hash, int hash_len) {
    int lowerBound = 0;
    int upperBound = hash_len - 1;
    int curIn;
    while (true)
    {
        curIn = (int)((lowerBound + upperBound) / 2);
        if (hash[curIn] == searchKey)
            return true; // Элемент найден
        else if (lowerBound > upperBound)
            return false; // Элемент не найден
        else // Деление диапазона
        {
            if (hash[curIn] < searchKey)
                lowerBound = curIn + 1; // В верхней половине
            else
                upperBound = curIn - 1; // В нижней половине
        }
    }
}

bool Dic::findPart(int chars_len, unsigned char *chars) {
    long long req = charsToHash(chars_len, chars);
    //bool result;
    switch (chars_len) {
        case 1:
            return true;
        case 2:
            return findHash(req, dic2_more, dic2_more_len);
        case 3:
            return findHash(req, dic3_more, dic3_more_len);
        case 4:
            return findHash(req, dic4_more, dic4_more_len);
        case 5:
            return findHash(req, dic5_more, dic5_more_len);
        case 6:
            return findHash(req, dic6_more, dic6_more_len);
        case 7:
            return findHash(req, dic7_more, dic7_more_len);
        case 8:
            return findHash(req, dic8_more, dic8_more_len);
        case 9:
            return findHash(req, dic9_more, dic9_more_len);
        default:
            return false;
    }
}
