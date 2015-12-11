#include <algorithm>
#include <iostream>
#include <jni.h>
#include <android/log.h>
#include "Track.h"


Track::Track() {
}

Track::~Track() {
}

void Track::init(long long * hashWords, int hashWords_len) {
    wordsAnswer.clear();
    for (int i = 0; i < hashWords_len; i++) {
        wordsAnswer.push_back(hashWords[i]);
    }
    //__android_log_print(ANDROID_LOG_ERROR, "BaldaNDK", "Track Init");
}

void Track::initDic(JNIEnv * env, jobject assetManager) {
    dic.init(env, assetManager);
}

void Track::findTrack(unsigned char * arrData, int * coordinatesWord, int coordinatesWord_len, int cur, int ins) {
    if (arrData[cur] == 0) // если текущая ячейка пустая
        return;
    // провер¤ем не пересекаетс¤ ли путь
    if (arraySearch(coordinatesWord, coordinatesWord_len, cur))
        return;
    // добавл¤ем текущую ¤чейку в путь
    int * coordinatesWord_new = new int[coordinatesWord_len + 1];
    for (int k = 0; k < coordinatesWord_len; k++) {
        coordinatesWord_new[k] = coordinatesWord[k];
    }
    coordinatesWord_new[coordinatesWord_len] = cur;
    coordinatesWord_len++;
    // ищем слова в словаре, начина¤ с 2 букв
    if (coordinatesWord_len > 1) {
        unsigned char * chars = new unsigned char[coordinatesWord_len];
        for (int k = 0; k < coordinatesWord_len; k++) {
            int j = coordinatesWord_new[k];
            chars[k] = arrData[j];
        }
        // если слово длиннее ранее найденного
        if (coordinatesWord_len > lastWord_len) {
            // если путь содержит добавленную букву
            if (arraySearch(coordinatesWord_new, coordinatesWord_len, ins)) {
                if (dic.findWord(coordinatesWord_len, chars)) {
                    // если слово не использовали в ответах
                    long long hash = dic.charsToHash(coordinatesWord_len, chars);
                    if (!vectorSearch(&wordsAnswer, hash)) {
                        word = hash;
                        charValue = arrData[ins];
                        charIndex = ins;
                        lastWord_len = coordinatesWord_len;
                    }
                }
            }
        }
        // проверяем нужно ли продолжать искать слова
        if (!dic.findPart(coordinatesWord_len, chars)) {
            delete[] chars;
            delete[] coordinatesWord_new;
            return;
        }
        delete chars;
    }
    // рекурсивный вызов в 4 направлени¤х
    if (cur < 20) {
        int * coordinatesWord_new_1 = new int[coordinatesWord_len];
        arrayCopy(coordinatesWord_new_1, coordinatesWord_new, coordinatesWord_len);
        findTrack(arrData, coordinatesWord_new_1, coordinatesWord_len, cur + 5, ins);
        delete[] coordinatesWord_new_1;
    }
    if (cur > 4) {
        int * coordinatesWord_new_2 = new int[coordinatesWord_len];
        arrayCopy(coordinatesWord_new_2, coordinatesWord_new, coordinatesWord_len);
        findTrack(arrData, coordinatesWord_new_2, coordinatesWord_len, cur - 5, ins);
        delete[] coordinatesWord_new_2;
    }
    if ((cur % 5) < 4) {
        int * coordinatesWord_new_3 = new int[coordinatesWord_len];
        arrayCopy(coordinatesWord_new_3, coordinatesWord_new, coordinatesWord_len);
        findTrack(arrData, coordinatesWord_new_3, coordinatesWord_len, cur + 1, ins);
        delete[] coordinatesWord_new_3;
    }

    if ((cur % 5) > 0) {
        int * coordinatesWord_new_4 = new int[coordinatesWord_len];
        arrayCopy(coordinatesWord_new_4, coordinatesWord_new, coordinatesWord_len);
        findTrack(arrData, coordinatesWord_new_4, coordinatesWord_len, cur - 1, ins);
        delete[] coordinatesWord_new_4;
    }
    delete[] coordinatesWord_new;
}

long long Track::getWord() {
    return word;
}

long long Track::getCharValue() {
    return (long long)charValue;
}

long long Track::getCharIndex() {
    return (long long)charIndex;
}

bool Track::arraySearch(int * coordinatesWord, int coordinatesWord_len, int cur) {
    for (int i = 0; i < coordinatesWord_len; i++) {
        if (cur == coordinatesWord[i])
            return true;
    }
    return false;
}

bool Track::vectorSearch(std::vector<long long> * vecs, long long vec) {
    int length = (int)vecs->size();
    for (int i = 0; i < length; i++) {
        if (vec == vecs->at(i))
        return true;
    }
    return false;
}

void Track::arrayCopy(int * arrTo, int * arrFrom, int arr_len) {
    for (int i = 0; i < arr_len; i++) {
        arrTo[i] = arrFrom[i];
    }
}

void Track::arrayCopy(unsigned char * arrTo, unsigned char * arrFrom, int arr_len) {
    for (int i = 0; i < arr_len; i++) {
        arrTo[i] = arrFrom[i];
    }
}

void Track::iter(unsigned char * arr, int arr_len) {
    //wordsAnswer = words;
    word = NULL;
    lastWord_len = NULL;
    charIndex = -1;
    // цикл подстановок
    for (int i = 0; i < arr_len; i++) {
        // ¤чейка не пуста¤
        if (arr[i] != 0)
            continue;
        // у ¤чейки есть смежные заполненные ¤чееки
        if ((i < 20 && arr[i + 5] != 0)
            || (i > 5 && arr[i - 5] != 0)
            || (i % 5 < 4 && arr[i + 1] != 0)
            || (i % 5 > 0 && arr[i - 1] != 0)) {
            // подставл¤ем буквы из строки chars
            for (int k = 1; k < 34; k++) {
                unsigned char * arrTemp = new unsigned char[arr_len];
                arrayCopy(arrTemp, arr, arr_len);
                arrTemp[i] = k;
                // ищем пути
                for (int j = 0; j < arr_len; j++) {
                    // начина¤ с непустых ¤чеек
                    if (arrTemp[j] != 0) {
                        int * arrIntTemp = new int[0];
                        findTrack(arrTemp, arrIntTemp, 0, j, i);
                        delete[] arrIntTemp;
                    }
                }
                delete[] arrTemp;
            }
        }
    }
    //return new Word(gWord, gCharString, gCharInt);
}