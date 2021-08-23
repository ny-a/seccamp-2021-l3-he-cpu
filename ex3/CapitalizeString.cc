#define MAX_LENGTH 32

#pragma hls_top
void CapitalizeString(char my_string[MAX_LENGTH]) {
  bool last_was_space = true;
#pragma hls_unroll yes
  for (int i = 0; i < MAX_LENGTH; i++) {
    char c = my_string[i];
    if (last_was_space && c >= 'a' && c <= 'z') {
      my_string[i] = c - ('a' - 'A');
    }
    last_was_space = (c == ' ');
  }
}
