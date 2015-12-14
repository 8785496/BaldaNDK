#pragma once
#include <iostream>
#include <vector>
#include "Dic.h";

class Track
{
public:
    Track();
    virtual ~Track();
    void init(long long * hashWords, int hashWords_len);
    void initDic(JNIEnv *env, jobject assetManager, jint lang);
    void findTrack(unsigned char * arrData, int * coordinatesWord, int coordinatesWord_len, int cur, int ins);
    void iter(unsigned char * arr, int arr_len);
    long long getWord();
    long long getCharValue();
    long long getCharIndex();
    Dic dic;
private:
    bool arraySearch(int * coordinatesWord, int coordinatesWord_len, int cur);
    bool vectorSearch(std::vector<long long> * vecs, long long vec);

    std::vector<long long> wordsAnswer;
    int lastWord_len = 0;
    unsigned char charValue;
    int charIndex;
    long long word;
    void arrayCopy(int * arrTo, int * arrFrom, int arr_len);
    void arrayCopy(unsigned char * arrTo, unsigned char * arrFrom, int arr_len);
};
