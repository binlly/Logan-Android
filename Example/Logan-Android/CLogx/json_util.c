

#include <stdlib.h>
#include <string.h>
#include "json_util.h"

Json_map_logx *create_json_map_logx(void) {
    Json_map_logx *item = malloc(sizeof(Json_map_logx));
    if (NULL != item)
        memset(item, 0, sizeof(Json_map_logx));
    return item;
}

int is_empty_json_map_clogx(Json_map_logx *item) {
    Json_map_logx temp;
    memset(&temp, 0, sizeof(Json_map_logx));
    if (memcmp(item, &temp, sizeof(Json_map_logx)) == 0) {
        return 1;
    }
    return 0;
}

void add_item_string_clogx(Json_map_logx *map, const char *key, const char *value) {
    if (NULL != map && NULL != value && NULL != key && strnlen(key, 128) > 0) {
        Json_map_logx *item = map;
        Json_map_logx *temp = item;
        if (!is_empty_json_map_clogx(item)) {
            while (NULL != item->nextItem) {
                item = item->nextItem;
            }
            temp = create_json_map_logx();
            item->nextItem = temp;
        }
        if (NULL != temp) {
            temp->type = CLOGX_JSON_MAP_STRING;
            temp->key = (char *) key;
            temp->valueStr = value;
        }
    }
}

void add_item_number_clogx(Json_map_logx *map, const char *key, double number) {
    if (NULL != map && NULL != key && strnlen(key, 128) > 0) {
        Json_map_logx *item = map;
        Json_map_logx *temp = item;
        if (!is_empty_json_map_clogx(item)) {
            while (NULL != item->nextItem) {
                item = item->nextItem;
            }
            temp = create_json_map_logx();
            item->nextItem = temp;
        }
        if (NULL != temp) {
            temp->type = CLOGX_JSON_MAP_NUMBER;
            temp->key = (char *) key;
            temp->valueNumber = number;
        }
    }
}

void add_item_bool_clogx(Json_map_logx *map, const char *key, int boolValue) {
    if (NULL != map && NULL != key && strnlen(key, 128) > 0) {
        Json_map_logx *item = map;
        Json_map_logx *temp = item;
        if (!is_empty_json_map_clogx(item)) {
            while (NULL != item->nextItem) {
                item = item->nextItem;
            }
            temp = create_json_map_logx();
            item->nextItem = temp;
        }
        if (NULL != temp) {
            temp->type = CLOGX_JSON_MAP_BOOL;
            temp->key = (char *) key;
            temp->valueBool = boolValue;
        }
    }
}

void delete_json_map_clogx(Json_map_logx *map) {
    if (NULL != map) {
        Json_map_logx *item = map;
        Json_map_logx *temp = NULL;
        do {
            temp = item->nextItem;
            free(item);
            item = temp;
        } while (NULL != item);
    }
}

void inflate_json_by_map_clogx(cJSON *root, Json_map_logx *map) {
    if (NULL != root && NULL != map) {
        Json_map_logx *item = map;
        do {
            switch (item->type) {
                case CLOGX_JSON_MAP_STRING:
                    if (NULL != item->valueStr) {
                        cJSON_AddStringToObject(root, item->key, item->valueStr);
                    }
                    break;
                case CLOGX_JSON_MAP_NUMBER:
                    cJSON_AddNumberToObject(root, item->key, item->valueNumber);
                    break;
                case CLOGX_JSON_MAP_BOOL:
                    cJSON_AddBoolToObject(root, item->key, item->valueBool);
                    break;
                default:
                    break;
            }
            item = item->nextItem;
        } while (NULL != item);
    }
}
