/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.maps;

import android.util.Log;

import com.ericmguimaraes.gaso.model.Location;
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
	private static final String API_KEY = "AIzaSyD75hEMtsbrsokY3ypMLgbrauQT0T7uZ_g";

	// Google Places serach url's
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

	private double _latitude;
	private double _longitude;
	private double _radius;

	private GooglePlacesParser parser;

	public GooglePlaces(){
		parser = new GooglePlacesParser();
	}

	public List<Station> getStationsList(Location location, String nextPageToken) {

		double latitude = location.getLat();
		double longitude = location.getLng();

		this._latitude = latitude;
		this._longitude = longitude;
		this._radius = 10*1000; //m
		String types="gas_station";

		try {

			HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			request.getUrl().put("types", types);
			request.getUrl().put("rankby", "distance");
			request.getUrl().put("location", _latitude + "," + _longitude);
			request.getUrl().put("sensor", "false");
//			request.getUrl().put("radius", _radius); // in meters
			request.getUrl().put("key", API_KEY);

			Log.e("url usado", String.valueOf(request.getUrl()));

			if(nextPageToken!=null && !nextPageToken.isEmpty())
				request.getUrl().put("pagetoken",nextPageToken);

			HttpResponse response = request.execute();
			String str = response.parseAsString();

			return parser.listFromJson(str);

		} catch (java.io.IOException e) {
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

	public GooglePlacesParser getParser() {
		return parser;
	}
}
