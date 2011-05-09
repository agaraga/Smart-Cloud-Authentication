package cloudapp;

import java.util.StringTokenizer;

public class StringUtils {
	public static String getValueByKey(String input, String key) {
		String ret = new String();
		StringTokenizer st = new StringTokenizer(input, "=,");
		while(st.hasMoreTokens()) {
			String curKey = st.nextToken();
			String curVal = st.nextToken();
			if (curKey.equals(key)) {
				ret = curVal;
				break;
			}
		}
		return ret;
	}
}
