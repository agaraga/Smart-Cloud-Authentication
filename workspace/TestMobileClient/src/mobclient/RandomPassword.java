package mobclient;

import java.security.SecureRandom;

public class RandomPassword {
	private static SecureRandom random = new SecureRandom();
    private static final String charset =
    	"!?=-~#^+@$%&*()/\\0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generate(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }
}
