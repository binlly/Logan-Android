

#ifndef CLOGX_JSON_UTIL_H
#define CLOGX_JSON_UTIL_H

#include "cJSON.h"

#define CLOGX_JSON_MAP_STRING 1
#define CLOGX_JSON_MAP_NUMBER 2
#define CLOGX_JSON_MAP_BOOL 3

typedef struct json_map {
    char *key;
    const char *valueStr;
    double valueNumber;
    int valueBool;
    int type;
    struct json_map *nextItem;
} Json_map_logx;

Json_map_logx *create_json_map_logx(void);

int is_empty_json_map_clogx(Json_map_logx *item);

void add_item_string_clogx(Json_map_logx *map, const char *key, const char *value);

void add_item_number_clogx(Json_map_logx *map, const char *key, double number);

void add_item_bool_clogx(Json_map_logx *map, const char *key, int boolValue);

void delete_json_map_clogx(Json_map_logx *item);

void inflate_json_by_map_clogx(cJSON *root, Json_map_logx *map);

#endif //CLOGX_JSON_UTIL_H
