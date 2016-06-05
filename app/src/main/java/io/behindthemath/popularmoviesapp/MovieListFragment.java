package io.behindthemath.popularmoviesapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by BehindTheMath on 3/21/2016.
 */
public class MovieListFragment extends Fragment {
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private MovieRecyclerViewAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private final String LOG_TAG = getClass().toString();
    private static final String BUNDLE_RECYCLER_STATE = "MovieListFragment.mRecyclerView.state";
    private int index = -1;
    private int top = -1;
    //TODO: https://github.com/codepath/android_guides/wiki/Implementing-Pull-to-Refresh-Guide#recyclerview-with-swiperefreshlayout
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    private Unbinder unbinder;
    private boolean mReadyForInitialization = false;
    private MovieList mMovieList;

    public MovieListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        setHasOptionsMenu(true);

        Log.i(LOG_TAG, "onCreateView");
        unbinder = ButterKnife.bind(this, view);

        mMovieList = ((MainActivity) getActivity()).getMovieList();

        mRecyclerView.setHasFixedSize(true);

        //TODO: add logic for various widths based on screen size
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initializeRecyclerView();
        mReadyForInitialization = true;

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeRefreshLayout.setRefreshing(false)
                // once the network request has completed successfully.
                if(!(mMovieList.getMovies())){
                    getActivity().finish();
                }
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "onResume");

        if(index != RecyclerView.NO_POSITION) {
            mLayoutManager.scrollToPositionWithOffset(index, top);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //read current recyclerview position
        index = mLayoutManager.findFirstVisibleItemPosition();
        View view = mRecyclerView.getChildAt(0);
        top = (view == null) ? 0 : (view.getTop() - mRecyclerView.getPaddingTop());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (swipeRefreshLayout != null) {
            outState.putParcelable(BUNDLE_RECYCLER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.i(LOG_TAG, "onViewStateRestored");
        if (savedInstanceState != null){
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);
            if (mRecyclerView != null) {
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
                } else {
                    mLayoutManager = new GridLayoutManager(getContext(), 2);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    initializeRecyclerView();
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            SortDialogFragment sortDialogFragment = new SortDialogFragment();
            sortDialogFragment.setOnSortChangedListener((MainActivity) getActivity());
            sortDialogFragment.setTargetFragment(this, 0);
            sortDialogFragment.show(fragmentManager, "fragment_sort_dialog");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onGetMoviesCompleted(boolean reSorted){
        Log.i(LOG_TAG, "onGetMoviesCompleted");
        if (swipeRefreshLayout != null) { swipeRefreshLayout.setRefreshing(false); }
        if (mReadyForInitialization || reSorted) {
            mAdapter.updateList();
            mAdapter.notifyDataSetChanged();
        } else {
            mReadyForInitialization = true;
        }
    }

    private void initializeRecyclerView() {
        //TODO: minimize usage of this method
        mReadyForInitialization = false;

        mAdapter = new MovieRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(((MainActivity) getActivity()));
    }
}
