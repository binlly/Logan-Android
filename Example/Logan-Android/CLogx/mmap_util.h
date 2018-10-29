

#ifndef LOGX_MMAP_MMAP
#define LOGX_MMAP_MMAP 1
#endif

#ifndef LOGX_MMAP_MEMORY
#define LOGX_MMAP_MEMORY 0
#endif

#ifndef LOGX_MMAP_FAIL
#define LOGX_MMAP_FAIL -1
#endif

#ifndef LOGX_MMAP_LENGTH
#define LOGX_MMAP_LENGTH 150 * 1024 //150k
#endif

#ifndef LOGX_MEMORY_LENGTH
#define LOGX_MEMORY_LENGTH 150 * 1024 //150k
#endif

#ifndef CLOGX_MMAP_UTIL_H
#define CLOGX_MMAP_UTIL_H

#include <stdio.h>
#include <unistd.h>
#include<sys/mman.h>
#include <fcntl.h>
#include <string.h>

int open_mmap_file_clogx(char *_filepath, unsigned char **buffer, unsigned char **cache);

#endif //CLOGX_MMAP_UTIL_H
