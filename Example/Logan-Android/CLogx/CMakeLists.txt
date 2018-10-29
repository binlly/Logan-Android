cmake_minimum_required(VERSION 3.5)

set(MBEDTSL_INCLUDE ../mbedtls/include)
set(MBEDTSL_LIBRARY ../mbedtls/library)

include_directories(${MBEDTSL_INCLUDE})

add_subdirectory(${MBEDTSL_LIBRARY} clogx.out)

set(SOURCE_FILES
        clogx_core.c
        mmap_util.c
        cJSON.c
        construct_data.c
        zlib_util.c
        logx_config.h
        json_util.c
        aes_util.c
        clogx_status.h
        directory_util.c
        base_util.c
        console_util.c)

add_library(clogx ${SOURCE_FILES})
target_link_libraries(clogx mbedcrypto)
