LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := AshmemLibrary
LOCAL_SRC_FILES := AshmemLibrary.cpp
LOCAL_LDLIBS	:= -llog

include $(BUILD_SHARED_LIBRARY)
