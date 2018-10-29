

#ifndef CLOGX_AES_UTIL_H
#define CLOGX_AES_UTIL_H

#include <string.h>

void aes_encrypt_clogx(unsigned char *in, unsigned char *out, int length, unsigned char *iv);

void aes_init_key_iv(const char *key, const char *iv);

void aes_inflate_iv_clogx(unsigned char *aes_iv);

#endif //CLOGX_AES_UTIL_H
