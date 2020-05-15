LOCAL_PATH := $(call my-dir)

# Python-for-Android paths

PYTHON_FOR_ANDROID_PATH := $(LOCAL_PATH)/../../../../../python-for-android
GUEST_PYTHON_PATH := $(PYTHON_FOR_ANDROID_PATH)/build/other_builds/python3
PYTHON_PATH := $(GUEST_PYTHON_PATH)/armeabi-v7a__ndk_target_21/python3


# Build libpybridge.so

include $(CLEAR_VARS)
LOCAL_MODULE    := pybridge
LOCAL_SRC_FILES := pybridge.c
LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := python3.8m
include $(BUILD_SHARED_LIBRARY)


# Include libpython3.8m.so

include $(CLEAR_VARS)
LOCAL_MODULE    := python3.8m
LOCAL_SRC_FILES := $(PYTHON_PATH)/android-build/libpython3.8m.so
LOCAL_EXPORT_CFLAGS := -I $(PYTHON_PATH)/Include
include $(PREBUILT_SHARED_LIBRARY)
