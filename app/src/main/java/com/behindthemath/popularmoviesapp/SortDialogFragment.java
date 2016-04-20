package com.behindthemath.popularmoviesapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.behindthemath.popularmoviesapp.MovieListFragment.SortType;

/**
 * Created by aryeh on 3/30/2016.
 */
public class SortDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {
    RadioButton mMostPopularRadioButton;
    RadioButton mHighestRatedRadioButton;
    RadioGroup mRadioGroup;
    public final String LOG_TAG = this.getClass().getName();

    public SortDialogFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sort_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set title
        getDialog().setTitle("Select Sort Order");

        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        // Get fields from view
        mMostPopularRadioButton = (RadioButton) view.findViewById(R.id.most_popular_radiobutton);
        mMostPopularRadioButton.setOnClickListener(this);
        mHighestRatedRadioButton = (RadioButton) view.findViewById(R.id.highest_rated_radiobutton);
        mHighestRatedRadioButton.setOnClickListener(this);

        switch(MovieListFragment.mSortOrder){
            case SORT_MOST_POPULAR:
                mRadioGroup.check(R.id.most_popular_radiobutton);
                break;
            case SORT_HIGHEST_RATED:
                mRadioGroup.check(R.id.highest_rated_radiobutton);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        SortType sortOrder;

        switch (view.getId()){
            case R.id.most_popular_radiobutton:
                sortOrder = SortType.SORT_MOST_POPULAR;
                break;
            case R.id.highest_rated_radiobutton:
                sortOrder = SortType.SORT_HIGHEST_RATED;
                break;
            default:
                sortOrder = SortType.SORT_MOST_POPULAR;
        }
        sendBackResult(sortOrder);
    }

    public void sendBackResult(SortType sortOrder) {
        ((MovieListFragment) getTargetFragment()).OnSortSelected(sortOrder);
        dismiss();
    }
}

