package com.jeofferson.onclas.PackageBottomNavigationItems;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jeofferson.onclas.PackageAdapters.PostAdapter;
import com.jeofferson.onclas.PackageObjectModel.PostHolder;
import com.jeofferson.onclas.PackageOthers.MyRecyclerView;
import com.jeofferson.onclas.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.jeofferson.onclas.PackageActivities.MainActivity.paginationNumOfItemPerPage;


public class Home extends Fragment {


    private FirebaseAuth mAuth;

    private FirebaseFirestore db;
    private CollectionReference newsfeedCollection;

    private List<PostHolder> postHolderList;

    private Query newsfeedQuery;
    private ListenerRegistration listenerRegistrationNewsfeed;

    private boolean areNewsfeedFirstTimeLoaded;
    private DocumentSnapshot lastVisible;

    private SearchView searchView;

    private PostAdapter postAdapter;
    private MyRecyclerView homeMyRecyclerViewNewsfeed;
    private LinearLayout homeLinearLayoutEmptyView;

    private boolean isSocialSpinner1FirstTimeLoaded = true;
    private Spinner socialSpinner1;
    private String spinnerCurrentlySelectedItem1 = "Most Recent";


    public Home() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_navigation_view_home, container, false);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        newsfeedCollection = db.collection("Newsfeed");

        setUpAdapter(view);

        setUpSpinner1(view);

        return view;
    }

    public void setUpSpinner1(View view) {

        socialSpinner1 = view.findViewById(R.id.socialSpinner1);

        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.questionFilters, R.layout.list_spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.list_spinner_item);

        socialSpinner1.setAdapter(arrayAdapter);

        startRetrievingNewsfeedFromDatabase();

        socialSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spinnerCurrentlySelectedItem1 = adapterView.getItemAtPosition(i).toString();

                TextView selectedText = (TextView) adapterView.getChildAt(0);
                if (selectedText != null) {

                    selectedText.setTextColor(getResources().getColor(R.color.lightTextSelected));

                }

                if (!isSocialSpinner1FirstTimeLoaded) {

                    emptyRecyclerView();
                    startRetrievingNewsfeedFromDatabase();

                }

                isSocialSpinner1FirstTimeLoaded = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    public void setUpAdapter(View view) {

        postHolderList = new ArrayList<>();

        homeMyRecyclerViewNewsfeed = view.findViewById(R.id.homeMyRecyclerViewNewsfeed);
        homeLinearLayoutEmptyView = view.findViewById(R.id.homeLinearLayoutEmptyView);

        // Empty view
        homeMyRecyclerViewNewsfeed.showIfEmpty(homeLinearLayoutEmptyView);

        homeMyRecyclerViewNewsfeed.setHasFixedSize(true);
        homeMyRecyclerViewNewsfeed.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


    public void emptyRecyclerView() {

        postHolderList.clear();
        postAdapter.notifyDataSetChanged();

    }


    public void startRetrievingNewsfeedFromDatabase() {

        areNewsfeedFirstTimeLoaded = true;

        getInitialNewsfeedFromDatabase();
        homeMyRecyclerViewNewsfeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean isBottomReached = !recyclerView.canScrollVertically(1);

                if (isBottomReached) {

                    getMoreNewsfeedFromDatabase();

                }

            }
        });

    }


    public void getInitialNewsfeedFromDatabase() {

        switch (spinnerCurrentlySelectedItem1) {

            case "Most Recent":
                newsfeedQuery = newsfeedCollection
                        .whereEqualTo("postType", "Student Updates")
                        .orderBy("dateCreated", Query.Direction.DESCENDING)
                        .limit(paginationNumOfItemPerPage);
                break;

            case "Unanswered":

                Log.e("jeo2", spinnerCurrentlySelectedItem1);

                newsfeedQuery = newsfeedCollection
                        .whereEqualTo("hasAnswer", false)
                        .orderBy("dateCreated", Query.Direction.DESCENDING)
                        .limit(paginationNumOfItemPerPage);
                break;

        }

//        newsfeedQuery = newsfeedCollection
//                .whereEqualTo("postType", "Student Updates")
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .limit(paginationNumOfItemPerPage);

        getNewsfeed(0);

    }


    public void getMoreNewsfeedFromDatabase() {

        switch (spinnerCurrentlySelectedItem1) {

            case "Most Recent":
                newsfeedQuery = newsfeedCollection
                        .whereEqualTo("postType", "Student Updates")
                        .orderBy("dateCreated", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(paginationNumOfItemPerPage);
                break;

//            case "Favorites":
//                newsfeedQuery = newsfeedCollection
//                        .whereEqualTo("postType", "Student Updates")
//                        .whereEqualTo("")
//                        .orderBy("dateCreated", Query.Direction.DESCENDING)
//                        .limit(paginationNumOfItemPerPage);
//                break;

            case "Unanswered":
                newsfeedQuery = newsfeedCollection
                        .whereEqualTo("hasAnswer", "false")
                        .orderBy("dateCreated", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(paginationNumOfItemPerPage);
                break;

        }

//        newsfeedQuery = newsfeedCollection
//                .whereEqualTo("postType", "Student Updates")
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .startAfter(lastVisible)
//                .limit(paginationNumOfItemPerPage);

        getNewsfeed(1);

    }


    public void getNewsfeed(final int nthQuery) {

        listenerRegistrationNewsfeed = newsfeedQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {

                    Log.e("My Error: ", e.getMessage());
                    return;

                }

                if (!queryDocumentSnapshots.isEmpty()) {

                    if (areNewsfeedFirstTimeLoaded || nthQuery == 1) {

                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    }

                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                        switch (documentChange.getType()) {

                            case ADDED:

                                PostHolder postHolder = documentChange.getDocument().toObject(PostHolder.class);
                                postHolder.setObjectId(documentChange.getDocument().getId());

                                if (areNewsfeedFirstTimeLoaded || nthQuery == 1) {

                                    postHolderList.add(postHolder);

                                } else {

                                    postHolderList.add(0, postHolder);

                                }

                                break;

                        }

                    }

                    postAdapter = new PostAdapter(postHolderList);
                    homeMyRecyclerViewNewsfeed.setAdapter(postAdapter);
                    areNewsfeedFirstTimeLoaded = false;

                }

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        if (postAdapter != null) {

            postAdapter.notifyDataSetChanged();

        }


    }


    @Override
    public void onStop() {
        super.onStop();

        if (listenerRegistrationNewsfeed != null) {

            listenerRegistrationNewsfeed.remove();

        }

    }


}
