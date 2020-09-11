package core.constants;

public enum Options {
        IGNORE_DIGITS(2),
        IGNORE_URLS(4),
        FIND_REPEAT_WORDS(8),
        IGNORE_CAPITALIZATION(512);

        private int code;

        public String getCode() {
            return String.valueOf(code);
        }

        private Options(int code) {
            this.code = code;
        }

        public static String computeOptions(Options... options) {
            int sum = 0;
            for (Options element : options) {
                sum += element.code;
            }
            return String.valueOf(sum);
        }
}
