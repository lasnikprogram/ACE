#pragma once
#include "../third_party/CLI11.hpp"
#include <functional>
#include <stdlib.h>
#include <string>

const size_t frontend_buff_size = 10000;
/*
 * output buffer for the frontend
 * generated by _frontend_print
 * */
// extern is used to prevent multiple definiton error
// when this header is included multiple times
// https://stackoverflow.com/questions/38942013/declaring-variables-in-header-files-c
extern std::string frontend_output_buff;

/*
 * return current frontend output
 * made by frontend_print
 * and clear it out
 * */
std::string frontend_pop_output();

/*
 * return current frontend output buffer
 * without clearing it
 * */
std::string frontend_peek_output();

void _frontend_print(bool print_to_stdout, const char *fmt, ...);
void frontend_print(const char *fmt, ...);

/*
 * tells frontend invalid command is sent
 * */

void frontend_invalid_command(bool print_to_stdout, const char *fmt, ...);
/*
 * mark a task has failed
 * to frontend
 * */
void frontend_mark_task_fail(const char *fmt, ...);
/*
 * mark beggining of output for
 * frontend to pick up
 * */
void frontend_mark_task_begin();

/*
 * mark end of output for
 * frontend to pick up
 * */
void frontend_mark_task_done();

/*
 * mark a progress of a certain task
 * can be called between [frontend_mark_task_begin]
 * and [frontend_mark_task_done]
 * */
void frontend_mark_progress(size_t current, size_t max);

/*
 * handle parsing error from cli and tell that error
 * to frontend
 * */
void frontend_handle_cli_parse_error(bool print_to_stdout,
                                     const CLI::ParseError &e);
