package cn.westlan.coding.scan;

public class ProcessData {
    private static byte[] bufread = new byte[20];
    private static byte[] bufread1 = new byte[1];
    private static byte count = (byte) 1;
    private static int count1 = 0;
    private static int ii = 0;
    private static byte sign = (byte) 0;
    private static byte sign00 = (byte) 0;
    private static byte sign01 = (byte) 0;
    private static byte sign02 = (byte) 0;
    private static byte sign03 = (byte) 0;
    private static byte sign1 = (byte) 0;

    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            String s4;
            int chi = s.charAt(i) & 255;
            if (chi < 16) {
                s4 = new StringBuilder(String.valueOf('0')).append(Integer.toHexString(chi)).toString();
            } else {
                s4 = Integer.toHexString(chi);
            }
            str = new StringBuilder(String.valueOf(str)).append(s4).append(' ').toString();
        }
        return str;
    }

    public static String StringToByte(String s) {
        String str = "";
        int enable = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (enable == 1 && ch != ' ') {
                str = new StringBuilder(String.valueOf(str)).append(ch).toString();
            }
            if (ch == '\n') {
                str = "";
                enable = 1;
            }
        }
        return str;
    }

    public static String StringCutToString(String s) {
        String str = "";
        int enable = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                enable = i;
            }
        }
        return s.substring(0, enable);
    }

    public static String StringCutToString1(String s) {
        String str = "";
        int i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (ch == '\n' && ((i - 1) * 4) + 5 == s.length()) {
                break;
            }
            str = new StringBuilder(String.valueOf(str)).append(ch).toString();
            i++;
        }
        return str;
    }

    public static String StringToNul(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch != ' ') {
                str = new StringBuilder(String.valueOf(str)).append(ch).toString();
            }
        }
        return str;
    }

    public static String toHexbyte(byte[] bytein, int count) {
        String str = "";
        for (int i = 0; i < count; i++) {
            String s4;
            int chi = bytein[i] & 255;
            if (chi < 16) {
                s4 = new StringBuilder(String.valueOf('0')).append(Integer.toHexString(chi)).toString();
            } else {
                s4 = Integer.toHexString(chi);
            }
            str = new StringBuilder(String.valueOf(str)).append(s4).append(' ').toString();
        }
        return str;
    }

    public static byte[] StrToHexbyte(String stringin) {
        int count = stringin.length();
        byte[] bArr = new byte[count];
        bArr = stringin.getBytes();
        byte[] data1 = new byte[(count / 2)];
        for (int i = 0; i < bArr.length / 2; i++) {
            byte ch = bArr[i * 2];
            byte cl = bArr[(i * 2) + 1];
            int chh = ch & 255;
            if (chh >= 97) {
                chh -= 87;
            } else if (65 <= chh) {
                chh -= 55;
            } else {
                chh -= 48;
            }
            int chl = cl & 255;
            if (chl >= 97) {
                chl -= 87;
            } else if (65 <= chl) {
                chl -= 55;
            } else {
                chl -= 48;
            }
            data1[i] = (byte) ((chh << 4) | chl);
        }
        return data1;
    }

    public static byte[] calreseive(byte[] data2) {
        for (byte b : data2) {
            if (sign03 == (byte) 1) {
                sign00 = (byte) 0;
                bufread[ii + 3] = b;
                ii++;
                if (ii == count - 3) {
                    ii = 0;
                    sign03 = (byte) 0;
                    byte[] bufback = new byte[count];
                    System.arraycopy(bufread, 0, bufback, 0, count);
                    return bufback;
                }
            }
            if (sign02 == (byte) 1 && sign03 == (byte) 0) {
                sign03 = (byte) 1;
                sign02 = (byte) 0;
                bufread[2] = b;
                switch (b) {
                    case (byte) 3:
                        count = (byte) 5;
                        break;
                    case (byte) 4:
                        count = (byte) 6;
                        break;
                    case (byte) 5:
                        count = (byte) 7;
                        break;
                    case (byte) 7:
                        count = (byte) 10;
                        break;
                    case (byte) 8:
                        count = (byte) 10;
                        break;
                    case (byte) 12:
                        count = (byte) 15;
                        break;
                }
            }
            if (sign01 == (byte) 1 && b == (byte) -2) {
                sign02 = (byte) 1;
                sign03 = (byte) 0;
                bufread[1] = (byte) -2;
            }
            if (sign00 == (byte) 0 && b == (byte) -1) {
                sign01 = (byte) 1;
                bufread[0] = (byte) -1;
            }
        }
        return bufread1;
    }

    public static int toint(byte[] bytein) {
        return (bytein[0] * 256) + (bytein[1] & 255);
    }
}