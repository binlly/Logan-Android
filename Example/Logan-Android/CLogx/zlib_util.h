

#ifndef CLOGX_ZLIB_UTIL_H
#define CLOGX_ZLIB_UTIL_H

#include "logx_config.h"
#include <zlib.h>
#include <stdlib.h>
#include <string.h>

#define LOGX_CHUNK 16384

//定义Logx_zlib的状态类型

#define LOGX_ZLIB_NONE 0
#define LOGX_ZLIB_INIT 1
#define LOGX_ZLIB_ING  2
#define LOGX_ZLIB_END  3
#define LOGX_ZLIB_FAIL 4

int init_zlib_clogx(cLogx_model *model); //初始化Logx

void clogx_zlib_compress(cLogx_model *model, char *data, int data_len); //压缩文件

void clogx_zlib_end_compress(cLogx_model *model); //压缩结束

void clogx_zlib_delete_stream(cLogx_model *model); //删除初始化的z_stream

#endif //CLOGX_ZLIB_UTIL_H
