package com.ericmguimaraes.gaso.maps;

import android.util.Log;

import java.io.InputStream;
import java.util.List;

public class GooglePlaces {

	/** Global instance of the HTTP transport. */
	//private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	// Google API Key
	private static final String API_KEY = "AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc"; // place your API key here

	// Google Places serach url's
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

	private double _latitude;
	private double _longitude;
	private double _radius;

	/**
	 * Searching places
	 * @param latitude - latitude of place
	 * @params longitude - longitude of place
	 * @param radius - radius of searchable area
	 * @return list of places
	 * */
/*	public String search(double latitude, double longitude, double radius)
			throws Exception {

		this._latitude = latitude;
		this._longitude = longitude;
		this._radius = radius;
		String types="gas_station";

		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			request.getUrl().put("key", API_KEY);
			request.getUrl().put("location", _latitude + "," + _longitude);
			request.getUrl().put("radius", _radius); // in meters
			request.getUrl().put("sensor", "false");
			request.getUrl().put("types", types);

			HttpResponse response = request.execute();
			String str = response.getContent().toString();
			return str;

		} catch (HttpResponseException e) {
			Log.e("Error:", e.getMessage());
			return null;
		}

	} */

	/**
	 * Searching single place full details
	 * @param reference - reference id of place
	 * 				   - which you will get in search api request
	 * */
	/*public PlaceDetails getPlaceDetails(String reference) throws Exception {
		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
			request.getUrl().put("key", API_KEY);
			request.getUrl().put("reference", reference);
			request.getUrl().put("sensor", "false");

			PlaceDetails place = request.execute().parseAs(PlaceDetails.class);

			return place;

		} catch (HttpResponseException e) {
			Log.e("ErrorDetails", e.getMessage());
			throw e;
		}
	} */

	/**
	 * Creating http request Factory
	 * */
	/**
	 * Creating http request Factory
	 * */
	/*public static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("AndroidHive-Places-Test");
				request.setHeaders(headers);
				JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
				request.addParser(parser);
			}
		});
	} */

}
