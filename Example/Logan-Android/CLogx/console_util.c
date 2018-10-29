

#include <stdarg.h>
#include <stdio.h>
#include "console_util.h"

static int is_debug_logx = 0;

int printf_clogx(char *fmt, ...) {
    int cnt = 0;
    if (is_debug_logx) {
        va_list argptr;
        va_start(argptr, fmt);
        cnt = vprintf(fmt, argptr);
        va_end(argptr);
    }
    return (cnt);
}

void set_debug_clogx(int debug) {
    is_debug_logx = debug;
}
