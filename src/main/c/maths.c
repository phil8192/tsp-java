#include "jni.h"
#include "maths.h"
#include <math.h>

JNIEXPORT jdouble JNICALL 
        Java_net_parasec_tsp_impl_Maths_sqrtnat(
            JNIEnv *env, jobject obj, jdouble d) {
    return sqrt(d);    
}

