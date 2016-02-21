package app.nomad.projects.yashasvi.hotspots.contentProviders;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by yashasvi on 1/20/16.
 */
public class PlaceSearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    private final static String AUTHORITY = "app.nomad.projects.yashasvi.hotspotsforwork.contentProviders.PlaceSearchSuggestionProvider";
    private final static int MODE = DATABASE_MODE_QUERIES;

    public PlaceSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
