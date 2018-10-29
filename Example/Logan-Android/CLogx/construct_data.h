

#ifndef CLOGX_BUILD_DATA_H
#define CLOGX_BUILD_DATA_H

typedef struct {
    char *data;
    int data_len;
} Construct_Data_cLogx;

Construct_Data_cLogx *
construct_json_data_clogx(char *log, int flag, long long local_time, char *thread_name,
                           long long thread_id, int is_main);

void construct_data_delete_clogx(Construct_Data_cLogx *data);

#endif //CLOGX_BUILD_DATA_H
