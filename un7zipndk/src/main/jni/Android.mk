LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := un7zip
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \

LOCAL_SRC_FILES := \
	D:\App\SingleShark\un7zip\src\main\jni\andun7z.cpp \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zAlloc.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zBuf.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zCrc.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zCrcOpt.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zDec.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zFile.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zIn.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zMain.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\7zStream.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\Bcj2.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\Bra.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\Bra86.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\CpuArch.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\Lzma2Dec.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\LzmaDec.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\Ppmd7.c \
	D:\App\SingleShark\un7zip\src\main\jni\src\Ppmd7Dec.c \

LOCAL_C_INCLUDES += D:\App\SingleShark\un7zip\src\main\jni
LOCAL_C_INCLUDES += D:\App\SingleShark\un7zip\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
