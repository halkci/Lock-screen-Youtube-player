package com.halkci.simpleyoutubeplayer;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by halkci on 2017/01/05.
 */

public class YoutubeSearchApi extends AsyncTask {

    // Youtube API key
    private static final String API_KEY = "Your Youtube API key";

    private static String searchKey = null;

    /**
     * YoutubeAPI検索結果
     */
    private List<SearchResult> searchResultList = null;

    private static Drawable drawable = null;

    /**
     * Global instance of Youtube object to make all API requests.
     */
    private static YouTube youtube;

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * Global instance of the max number of videos we want returned (50 = upper limit per page).
     */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    private static ArrayList<Drawable> drawables = null;

    private static ArrayList<String> videoIdList = null;

    private static ArrayList<String> channelList = null;

    public YoutubeSearchApi(String searchKey) {
        this.searchKey = searchKey;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
              /*
               * The YouTube object is used to make all API requests. The last argument is required, but
               * because we don't need anything initialized when the HttpRequest is initialized, we override
               * the interface and provide a no-op function.
               */
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("simpleyoutubeplayer").build();

            // Get query term from user.
            String queryTerm = searchKey;//getInputQuery();

            YouTube.Search.List search = youtube.search().list("id,snippet");
              /*
               * It is important to set your developer key from the Google Developer Console for
               * non-authenticated requests (found under the API Access tab at this link:
               * code.google.com/apis/). This is good practice and increased your quota.
               */
            String apiKey = API_KEY;//properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);
              /*
               * We are only searching for videos (not playlists or channels). If we were searching for
               * more, we would add them as a string like this: "video,playlist,channel".
               */
            search.setType("video");
              /*
               * This method reduces the info returned to only the fields we need and makes calls more
               * efficient.
               */
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/medium/url,snippet/channelTitle,snippet/description)");//medium,high
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();

            searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm);
            }

            synchronized (this){
                this.notifyAll();
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }


        return null;
    }

    /*
    * Prints out all SearchResults in the Iterator. Each printed line includes title, id, and
    * thumbnail.
    *
    * @param iteratorSearchResults Iterator of SearchResults to print
    *
    * @param query Search query (String)
    */
    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        drawables = new ArrayList<>();
        videoIdList = new ArrayList<>();
        channelList = new ArrayList<>();

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            if (rId.getKind().equals("youtube#video")) {

                Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("medium");

                InputStream is = null;
                try {
                    is = (InputStream) new URL(thumbnail.getUrl()).getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Drawable drawable = Drawable.createFromStream(is, "src name");
                drawables.add(drawable);
                videoIdList.add(rId.getVideoId());
                channelList.add(singleVideo.getSnippet().getChannelTitle());
            }
        }
    }

    protected ArrayList<String> getVideoTitles(){
        if(searchResultList != null){
            ArrayList<String> videoTitles = new ArrayList<>();
            Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
            while(iteratorSearchResults.hasNext()){
                SearchResult singleVideo = iteratorSearchResults.next();
                videoTitles.add(singleVideo.getSnippet().getTitle());
            }
            return videoTitles;
        }
        return null;
    }

    protected ArrayList<Drawable> getThumbnail() {
        return drawables;
    }

    protected ArrayList<String> getVideoIdList(){return videoIdList;}

    protected ArrayList<String> getChannelList(){return channelList;}
}
