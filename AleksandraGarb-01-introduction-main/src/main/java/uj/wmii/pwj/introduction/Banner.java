package uj.wmii.pwj.introduction;

public class Banner {

    public static String[] toBanner(String input) {
        if (input == null) {
            return new String[0];
        }

        input = input.toUpperCase();
        String[] result = new String[7];
        for (int i = 0; i < 7; i++) {
            result[i] = "";
        }

        for (char ch : input.toCharArray()) {
            switch (ch) {
                case 'A':
                    appendLetter(result, new String[]{
                            "   #   ",
                            "  # #  ",
                            " #   # ",
                            "#     #",
                            "#######",
                            "#     #",
                            "#     #"
                    });
                    break;
                case 'B':
                    appendLetter(result, new String[]{
                            "###### ",
                            "#     #",
                            "#     #",
                            "###### ",
                            "#     #",
                            "#     #",
                            "###### "
                    });
                    break;
                case 'C':
                    appendLetter(result, new String[]{
                            " ##### ",
                            "#     #",
                            "#      ",
                            "#      ",
                            "#      ",
                            "#     #",
                            " ##### "
                    });
                    break;
                case 'D':
                    appendLetter(result, new String[]{
                            "###### ",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            "###### "
                    });
                    break;
                case 'E':
                    appendLetter(result, new String[]{
                            "#######",
                            "#      ",
                            "#      ",
                            "#####  ",
                            "#      ",
                            "#      ",
                            "#######"
                    });
                    break;
                case 'F':
                    appendLetter(result, new String[]{
                            "#######",
                            "#      ",
                            "#      ",
                            "#####  ",
                            "#      ",
                            "#      ",
                            "#      "
                    });
                    break;
                case 'G':
                    appendLetter(result, new String[]{
                            " ##### ",
                            "#     #",
                            "#      ",
                            "#  ####",
                            "#     #",
                            "#     #",
                            " ##### "
                    });
                    break;
                case 'H':
                    appendLetter(result, new String[]{
                            "#     #",
                            "#     #",
                            "#     #",
                            "#######",
                            "#     #",
                            "#     #",
                            "#     #"
                    });
                    break;
                case 'I':
                    appendLetter(result, new String[]{
                            "###",
                            " # ",
                            " # ",
                            " # ",
                            " # ",
                            " # ",
                            "###"
                    });
                    break;
                case 'J':
                    appendLetter(result, new String[]{
                            "      #",
                            "      #",
                            "      #",
                            "      #",
                            "#     #",
                            "#     #",
                            " ##### "
                    });
                    break;
                case 'K':
                    appendLetter(result, new String[]{
                            "#    #",
                            "#   # ",
                            "#  #  ",
                            "###   ",
                            "#  #  ",
                            "#   # ",
                            "#    #"
                    });
                    break;
                case 'L':
                    appendLetter(result, new String[]{
                            "#      ",
                            "#      ",
                            "#      ",
                            "#      ",
                            "#      ",
                            "#      ",
                            "#######"
                    });
                    break;
                case 'M':
                    appendLetter(result, new String[]{
                            "#     #",
                            "##   ##",
                            "# # # #",
                            "#  #  #",
                            "#     #",
                            "#     #",
                            "#     #"
                    });
                    break;
                case 'N':
                    appendLetter(result, new String[]{
                            "#     #",
                            "##    #",
                            "# #   #",
                            "#  #  #",
                            "#   # #",
                            "#    ##",
                            "#     #"
                    });
                    break;
                case 'O':
                    appendLetter(result, new String[]{
                            "#######",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#######"
                    });
                    break;
                case 'P':
                    appendLetter(result, new String[]{
                            "###### ",
                            "#     #",
                            "#     #",
                            "###### ",
                            "#      ",
                            "#      ",
                            "#      "
                    });
                    break;
                case 'Q':
                    appendLetter(result, new String[]{
                            " ##### ",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#   # #",
                            "#    # ",
                            " #### #"
                    });
                    break;
                case 'R':
                    appendLetter(result, new String[]{
                            "###### ",
                            "#     #",
                            "#     #",
                            "###### ",
                            "#   #  ",
                            "#    # ",
                            "#     #"
                    });
                    break;
                case 'S':
                    appendLetter(result, new String[]{
                            " ##### ",
                            "#     #",
                            "#      ",
                            " ##### ",
                            "      #",
                            "#     #",
                            " ##### "
                    });
                    break;
                case 'T':
                    appendLetter(result, new String[]{
                            "#######",
                            "   #   ",
                            "   #   ",
                            "   #   ",
                            "   #   ",
                            "   #   ",
                            "   #   "
                    });
                    break;
                case 'U':
                    appendLetter(result, new String[]{
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            " ##### "
                    });
                    break;
                case 'V':
                    appendLetter(result, new String[]{
                            "#     #",
                            "#     #",
                            "#     #",
                            "#     #",
                            " #   # ",
                            "  # #  ",
                            "   #   "
                    });
                    break;
                case 'W':
                    appendLetter(result, new String[]{
                            "#     #",
                            "#  #  #",
                            "#  #  #",
                            "#  #  #",
                            "#  #  #",
                            "#  #  #",
                            " ## ## "
                    });
                    break;
                case 'X':
                    appendLetter(result, new String[]{
                            "#     #",
                            " #   # ",
                            "  # #  ",
                            "   #   ",
                            "  # #  ",
                            " #   # ",
                            "#     #"
                    });
                    break;
                case 'Y':
                    appendLetter(result, new String[]{
                            "#     #",
                            " #   # ",
                            "  # #  ",
                            "   #   ",
                            "   #   ",
                            "   #   ",
                            "   #   "
                    });
                    break;
                case 'Z':
                    appendLetter(result, new String[]{
                            "#######",
                            "     # ",
                            "    #  ",
                            "   #   ",
                            "  #    ",
                            " #     ",
                            "#######"
                    });
                    break;
                case ' ':
                    appendLetter(result, new String[]{
                            "  ",
                            "  ",
                            "  ",
                            "  ",
                            "  ",
                            "  ",
                            "  "
                    });
                    break;
                default:
                    break;
            }
        }

        return result;
    }


    private static void appendLetter(String[] result, String[] letter) {
        for (int i = 0; i < 7; i++) {
            result[i] += letter[i] + " ";
        }
    }
}


