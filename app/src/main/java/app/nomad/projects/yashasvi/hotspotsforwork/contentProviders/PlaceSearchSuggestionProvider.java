package app.nomad.projects.yashasvi.hotspotsforwork.contentProviders;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by ygirdha on 1/20/16.
 */
public class PlaceSearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "app.nomad.projects.yashasvi.hotspotsforwork.contentProviders.PlaceSearchSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public PlaceSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
