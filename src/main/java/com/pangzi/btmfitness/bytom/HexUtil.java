package com.pangzi.btmfitness.bytom;

/**
 * @author zxw
 * @date 2018-11-11
 */
public class HexUtil {

    private final static String OP_PUSHDATA1 = "6a4c";
    private final static String OP_PUSHDATA2 = "6a4d";
    private final static String OP_PUSHDATA4 = "6a4e";

    /**
     * 字符串转16进制Hex
     *
     * @param data
     * @return
     */
    public static String StringToHex(String data) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder stringBuilder = new StringBuilder("");
        byte[] bytes = data.getBytes();
        int bit;
        for (int i = 0; i < bytes.length; i++) {
            bit = (bytes[i] & 0x0f0) >> 4;
            stringBuilder.append(chars[bit]);
            bit = bytes[i] & 0x0f;
            stringBuilder.append(chars[bit]);
        }
        String result = stringBuilder.toString().trim();
        return result;
    }

    /**
     * 16进制转字符串
     *
     * @param hex
     * @return
     */
    public static String HexToString(String hex) {
        String str = "0123456789abcdef";
        byte[] baKeyword = new byte[hex.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hex.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            // UTF-16le:Not
            String result = new String(baKeyword, "utf-8");
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String decodingRetireProgram(String program) {
        if (program.startsWith(OP_PUSHDATA1)) {
            program = program.substring(6, program.length());
        } else if (program.startsWith(OP_PUSHDATA2)) {
            program = program.substring(8, program.length());
        } else if (program.startsWith(OP_PUSHDATA4)) {
            program = program.substring(10, program.length());
        } else {
            program = program.substring(4, program.length());
        }
        String str = HexToString(program);
        if (str == null) {
            str = "retire解析出错";
        }
        return str;
    }
}
