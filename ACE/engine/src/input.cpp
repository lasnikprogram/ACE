#include "input.hpp"
#include "ACE_global.hpp"
#include "error.hpp"
#include "to_frontend.hpp"
#include <string.h>

extern "C" {
#include "../third_party/linenoise/linenoise.h"
}

void run_input_loop(std::function<E_loop_statement(std::string)> on_input,
                    std::string mode_name) {

  char input_display[200];
  snprintf(input_display, 199, "(%s) ", mode_name.c_str());

  //
  char *line;
  while ((line = linenoise(input_display)) != NULL) {
    // ================== take line input ==========
    std::string input_str = std::string(line);
    // only enter?, just continue like a normal shell would
    if (input_str == "")
      continue;
    // add to history
    linenoiseHistoryAdd(line);

    // =============================================
    // tells frontend that task has started
    // or done when gui_protocol is enabled

    if (ACE_global::use_gui_protocol)
      frontend_mark_task_begin();
    if (on_input(input_str) == E_loop_statement::break_) {
      // free resource
      linenoiseFree(line);
      break;
    }

    if (ACE_global::use_gui_protocol)
      frontend_mark_task_done();
    // free resource
    linenoiseFree(line);
  }
}
