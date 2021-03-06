package eu.europeana.portal2.web.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonFormatter {

	/**
	 * Gson formatter
	 */
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * JSON parser
	 */
	private static JsonParser parser = new JsonParser();

	/**
	 * Format JSON string
	 *
	 * @param rawJsonString
	 *   The original (raw) JSON string
	 *
	 * @return
	 *   The pretty printed JSON string
	 */
	public static String format(String rawJsonString) throws JsonSyntaxException {
		JsonElement element = parser.parse(rawJsonString);
		return gson.toJson(element)
				.replace("  ", " &nbsp;")
				.replace("\n", "<br/>\n")
				.replace("\\u003d", "=")
				.replace("\\u0026", "&")
				.replace("\\u003c", "&lt;")
				.replace("\\u003e", "&gt;")
				;
	}
}
