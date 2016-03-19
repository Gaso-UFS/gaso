package com.ericmguimaraes.gaso.maps;

import android.util.Log;

import com.ericmguimaraes.gaso.model.Station;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.List;

public class GooglePlaces {

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	// Google API Key
	private static final String API_KEY = "AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc";

	// Google Places serach url's
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

	private double _latitude;
	private double _longitude;
	private double _radius;

	public List<Station> getStationsList(double latitude, double longitude)
			throws Exception {

		this._latitude = latitude;
		this._longitude = longitude;
		this._radius = 100*1000; //m
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
			String str = response.parseAsString();

			GooglePlacesParser parser = new GooglePlacesParser();
			return parser.listFromJson(str);

		} catch (HttpResponseException e) {
			Log.e("Error:", e.getMessage());
			return null;
		}
	}

	public Station getStationDetails(Station station) throws Exception {
		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
			request.getUrl().put("key", API_KEY);
			request.getUrl().put("reference", station.getReference());
			request.getUrl().put("sensor", "false");

			String place = request.execute().parseAsString();

			GooglePlacesParser parser = new GooglePlacesParser();
			return parser.stationFromJson(station, place);

		} catch (HttpResponseException e) {
			Log.e("ErrorDetails", e.getMessage());
			throw e;
		}
	}

	/**
	 * Creating http request Factory
	 * */
	/**
	 * Creating http request Factory
	 * */
	public static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				request.setHeaders(headers);
			}
		});
	}

}
