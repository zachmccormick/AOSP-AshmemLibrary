#include <jni.h>
#include <android/log.h>
#include <sys/mman.h>
#include "ashmem-dev.c"

#define INFO_TAG "[INFO]"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, INFO_TAG, __VA_ARGS__)

extern "C" {

jint throwException(JNIEnv *env, const char *message) {
	const char *className = "java/lang/Error";
	jclass exClass = env->FindClass(className);
	return env->ThrowNew(exClass, message);
}

JNIEXPORT jint JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_allocateMemory(
		JNIEnv * env, jclass clazz, jstring name, jint pages) {
	const char *nativeString = env->GetStringUTFChars(name, 0);
	return ashmem_create_region(nativeString, pages);
}

JNIEXPORT void JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_writeByte(
		JNIEnv * env, jclass clazz, jint fd, jint pages, jint offset,
		jbyte byte) {
	char *map = (char*) mmap(NULL, PAGE_SIZE * pages, PROT_READ | PROT_WRITE,
			MAP_SHARED, fd, 0);
	if (map == MAP_FAILED) {
		const char* message = "mmap failed";
		throwException(env, message);
		return;
	}
	map[offset] = byte;
	munmap(map, PAGE_SIZE * pages);
}

JNIEXPORT jbyte JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_readByte(
		JNIEnv * env, jclass clazz, jint fd, jint pages, jint offset) {
	char *map = (char*) mmap(NULL, PAGE_SIZE * pages, PROT_READ | PROT_WRITE,
			MAP_SHARED, fd, 0);
	if (map == MAP_FAILED) {
		const char* message = "mmap failed";
		return throwException(env, message);
	}
	jbyte b = map[offset];
	munmap(map, PAGE_SIZE * pages);
	return b;
}

JNIEXPORT jint JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_getPageSize(
		JNIEnv* env, jclass clazz) {
	return PAGE_SIZE;
}

JNIEXPORT jobject JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_getFileDescriptor(
		JNIEnv* env, jclass clazz, jint fd) {
	jclass filedescriptor = env->FindClass("java/io/FileDescriptor");
	jmethodID constructor = env->GetMethodID(filedescriptor, "<init>", "()V");
	jobject ret = env->NewObject(filedescriptor, constructor);
	jfieldID nfd = env->GetFieldID(filedescriptor, "fd", "I");
	env->SetIntField(ret, nfd, fd);
	return ret;

}

JNIEXPORT jbyteArray JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_readBytes(
		JNIEnv * env, jclass clazz, jint fd, jint pages, jint offset, jint length) {
	char *map = (char*) mmap(NULL, PAGE_SIZE * pages, PROT_READ | PROT_WRITE,
			MAP_SHARED, fd, 0);
	if (map == MAP_FAILED) {
		const char* message = "mmap failed";
		throwException(env, message);
		return NULL;
	}
	jbyteArray bArray = env->NewByteArray(length);
	env->SetByteArrayRegion(bArray,0,length, (const signed char*) (map + offset));
	munmap(map, PAGE_SIZE * pages);
	return bArray;
}

JNIEXPORT void JNICALL Java_edu_vanderbilt_mccormick_ashmemlibrary_AshmemTool_writeBytes(
		JNIEnv * env, jclass clazz, jint fd, jint pages, jint offset,
		jbyteArray bytes) {
	char *map = (char*) mmap(NULL, PAGE_SIZE * pages, PROT_READ | PROT_WRITE,
			MAP_SHARED, fd, 0);
	if (map == MAP_FAILED) {
		const char* message = "mmap failed";
		throwException(env, message);
		return;
	}
	jint length = env->GetArrayLength(bytes);
	env->GetByteArrayRegion(bytes, 0, length, (signed char* ) map + offset);
	munmap(map, PAGE_SIZE * pages);
}

}
