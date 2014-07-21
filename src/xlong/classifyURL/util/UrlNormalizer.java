package xlong.classifyURL.util;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import gnu.inet.encoding.IDNA;

/**
 * Class to normalize URLs resUrl = UrlNormalizer.normalize(oriUrl)
 * 
 * 1. Already normalized: "http://www.test.com/test/test" ->
 * "http://www.test.com/test/test"
 * "http://www.test.com:71/test/test?lang=cn&x=y" ->
 * "http://www.test.com:71/test/test?lang=cn&x=y"
 * 
 * 2. Malformed URL: "httpwww.test.com/test/test" ->
 * "httpwww.test.com/test/test"
 * 
 * 3. Converting the scheme to lower case: "HTTP://www.example.com" ->
 * "http://www.example.com" "HttP://www.example.com/Test" ->
 * "http://www.example.com/Test"
 * 
 * 4. Converting the host to lower case: "http://www.Example.com" ->
 * "http://www.example.com" "http://www.EXAMPLE.com/Test" ->
 * "http://www.example.com/Test"
 * 
 * 5. Decoding percent-encoded octets of unreserved characters:
 * "http://www.example.com/%7Eusername" -> "http://www.example.com/~username"
 * 
 * 6. Decoding using ToUnicode according to IDNA: "http://www.xn--g5t518j.net"
 * -> "http://www.微風.net"
 * "http://www.xn--xkrp53d.net/%E6%B8%85%E5%8D%8E/%E6%B8%85%E5%8D%8E/qh%E6%B8%85%E5%8D%8Eqh"
 * -> "http://www.清华.net/清华/清华/qh清华qh"
 * "http://www.example.com/display?lang=%E4%B8%AD%E6%96%87" ->
 * "http://www.example.com/display?lang=中文"
 * 
 * 7. Removing the default port: "http://www.example.com:80/bar.html" ->
 * "http://www.example.com/bar.html"
 * 
 * 8. Normalize the path: "http://www.example.com/../a/b/../c/./d.html" ->
 * "http://www.example.com/../a/c/d.html" "http://www.example.com/foo//bar.html"
 * -> "http://www.example.com/foo/bar.html"
 * 
 * 9. Sorting the query parameters:
 * "http://www.example.com/display?lang=en&article=fred" ->
 * "http://www.example.com/display?article=fred&lang=en"
 * 
 * 10. Removing the '?' when the query is empty:
 * "http://www.example.com/display?" -> "http://www.example.com/display"
 * 
 * 11. Removing the fragment: "http://www.example.com/bar.html#section1" ->
 * "http://www.example.com/bar.html"
 * 
 * 12. [Optional] Adding “www” as the first domain label: "http://example.com"
 * -> "http://www.example.com"
 * 
 * 13. [Optional] Limiting protocols: "https://www.example.com" ->
 * "http://www.example.com"
 * 
 * 14. [Optional] Removing trailing slash: "http://www.example.com/alice/" ->
 * "http://www.example.com/alice"
 * 
 * 15. [Optional] Removing default directory index:
 * "http://www.example.com/default.asp" -> "http://www.example.com"
 * "http://www.example.com/a/index.html" -> "http://www.example.com/a"
 * 
 * 16. [Optional] Remove some query string parameters like 'utm_*' and
 * '*session*': "http://www.example.com/display?lang=en&utm_test=utm_test" ->
 * "http://www.example.com/display?lang=en"
 * "http://www.example.com/display?_session_=test&lang=en" ->
 * "http://www.example.com/display?lang=en"
 */

public class UrlNormalizer {
	public static boolean flagAddingWWW = false;
	public static boolean flagLimitingProtocols = false;
	public static boolean flagRevomingTrailingSlash = false;
	public static boolean flagRemovingDefaultDirectoryIndex = false;
	public static boolean flagRemoveSomeQueryString = true;

	/**
	 * Normalize URL
	 * 
	 * @param oriUrl
	 *            URL to normalize
	 * @return
	 */
	public static String normalize(final String oriUrl) {

		URL url;

		// decode(): Decoding percent-encoded octets of unreserved characters.
		// normalize(): Dealing with Malformed URL. Converting the scheme to
		// lower case. Removing duplicate slashes.
		try {
			url = new URI(URLDecoder.decode(oriUrl, "UTF-8")).normalize()
					.toURL();
		} catch (Exception e) {
			return oriUrl;
		}

		// Limiting protocols
		String protocol = url.getProtocol();
		if (flagLimitingProtocols)
			if (protocol.equals("https"))
				protocol = "http";

		// Converting the host to lower case
		// Decoding using ToUnicode according to IDNA
		// Adding “www” as the first domain label.
		String host = IDNA.toUnicode(url.getHost().toLowerCase());
		if (flagAddingWWW)
			if (!host.startsWith("www"))
				host = "www." + host;

		// Removing the default port
		int portInt = url.getPort();
		String port = (portInt != -1 && portInt != 80 ? ":" + portInt : "");

		// Removing default directory index
		String path = url.getPath();
		if (flagRemovingDefaultDirectoryIndex) {
			if (path.endsWith("default.asp"))
				path = path.substring(0, path.length()
						- (new String("default.asp")).length());
			if (path.endsWith("index.html"))
				path = path.substring(0, path.length()
						- (new String("index.html")).length());
		}

		// Sorting the query parameters
		// Removing the '?' when the query is empty
		// Remove some query string parameters like 'utm_*' and '*session*'
		String query = normalizeQuery(url.getQuery());

		String resUrl = protocol + "://" + host + port + path + query;

		// Removing trailing slash
		if (flagRevomingTrailingSlash)
			if (resUrl.endsWith("/"))
				resUrl = resUrl.substring(0, resUrl.length() - 1);

		return resUrl;
	}

	private static String normalizeQuery(final String query) {
		SortedMap<String, String> params = createParameterMap(query);
		if (params != null) {
			// Remove some query string parameters like 'utm_*' and '*session*'
			if (flagRemoveSomeQueryString)
				for (Iterator<String> i = params.keySet().iterator(); i
						.hasNext();) {
					final String key = i.next();
					if (key.startsWith("utm_") || key.contains("session")) {
						i.remove();
					}
				}
			return "?" + canonicalize(params);
		} else
			return "";
	}

	/**
	 * Takes a query string, separates the constituent name-value pairs, and
	 * stores them in a SortedMap ordered by lexicographical order.
	 * 
	 * @param query
	 *            string
	 * @return Null if there is no query string.
	 */
	private static SortedMap<String, String> createParameterMap(
			final String queryString) {
		if (queryString == null || queryString.isEmpty()) {
			return null;
		}
		final String[] pairs = queryString.split("&");
		final Map<String, String> params = new HashMap<String, String>(
				pairs.length);

		for (final String pair : pairs) {
			if (pair.length() < 1) {
				continue;
			}
			String[] tokens = pair.split("=", 2);
			// for (int j = 0; j < tokens.length; j++){
			// try{
			// tokens[j] = URLDecoder.decode(tokens[j], "UTF-8");
			// }
			// catch (UnsupportedEncodingException ex){
			// ex.printStackTrace();
			// }
			// }
			switch (tokens.length) {
			case 1:
				if (pair.charAt(0) == '=')
					params.put("", tokens[0]);
				else
					params.put(tokens[0], "");
				break;
			case 2:
				params.put(tokens[0], tokens[1]);
				break;
			}
		}
		return new TreeMap<String, String>(params);
	}

	/**
	 * Canonicalize the query string.
	 * 
	 * @param sortedParamMap
	 *            Parameter name-value pairs in lexicographical order.
	 * @return Canonical form of query string.
	 */
	private static String canonicalize(
			final SortedMap<String, String> sortedParamMap) {
		if (sortedParamMap == null || sortedParamMap.isEmpty()) {
			return "";
		}
		final StringBuffer sb = new StringBuffer(350);
		final Iterator<Map.Entry<String, String>> iter = sortedParamMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			final Map.Entry<String, String> pair = iter.next();
			sb.append(pair.getKey());
			sb.append('=');
			sb.append(pair.getValue());
			if (iter.hasNext())
				sb.append('&');
		}
		return sb.toString();
	}

}
