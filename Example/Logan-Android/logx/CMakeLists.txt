cmake_minimum_required(VERSION 3.4.1)

SET (CMAKE_C_FLAGS_DEBUG          "-g")
SET (CMAKE_C_FLAGS_RELEASE        "-O2 -DNDEBUG")

set(EXTERN_DIR ../CLogx)

add_subdirectory(${EXTERN_DIR} clogx.out)

include_directories(${EXTERN_DIR})

link_directories(clogx.out)


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

add_library(logx SHARED src/main/jni/clogx_protocol.c)
target_link_libraries(logx ${log-lib} z clogx)
