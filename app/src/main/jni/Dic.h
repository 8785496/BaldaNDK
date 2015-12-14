#pragma once
class Dic
{
public:
    Dic();
    virtual ~Dic();
    void init(JNIEnv*, jobject, jint);
    bool findWord(int, unsigned char *);
    bool findPart(int chars_len, unsigned char * chars);
    long long charsToHash(int chars_len, unsigned char * chars);
private:
    long long *dictionary, *dic2_more, *dic3_more, *dic4_more, *dic5_more,
            *dic6_more, *dic7_more, *dic8_more, *dic9_more;
    int dictionary_len, dic2_more_len, dic3_more_len, dic4_more_len, dic5_more_len,
            dic6_more_len, dic7_more_len, dic8_more_len, dic9_more_len;
    bool findHash(long long, long long *, int);

};
