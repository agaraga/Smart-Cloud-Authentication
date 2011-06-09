package mobclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class QueryString extends Object {

	private String query = "";

	public QueryString(String name, String value) {
		encode(name, value);
	}

	public void add(String name, String value) {
		query += "&";
		encode(name, value);
	}

	private void encode(String name, String value) {
		try {
			query += URLEncoder.encode(name, "UTF-8");
			query += "=";
			query += URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("Broken VM does not support UTF-8");
		}
	}

	public String getQuery() {
		return query;
	}

	public String toString() {
		return getQuery();
	}
}