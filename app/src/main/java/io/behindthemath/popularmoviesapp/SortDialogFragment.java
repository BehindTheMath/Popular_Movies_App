package io.behindthemath.popularmoviesapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.behindthemath.popularmoviesapp.MovieList.SortType;

/**
 * Created by BehindTheMath on 3/30/2016.
 */
public class SortDialogFragment extends AppCompatDialogFragment /*implements View.OnClickListener*/ {
    @BindView(R.id.most_popular_radiobutton) RadioButton mMostPopularRadioButton;
    @BindView(R.id.highest_rated_radiobutton) RadioButton mHighestRatedRadioButton;
    @BindView(R.id.radio_group) RadioGroup mRadioGroup;
    private final String LOG_TAG = this.getClass().getName();
    private Unbinder unbinder;
    private OnSortChangedListener mOnSortChangedListener;
    private SortType mCurrentSortOrder;

    public SortDialogFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sort_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        // Set title
        getDialog().setTitle("Select Sort Order");

        mCurrentSortOrder = MovieList.getSortOrder();
        switch(mCurrentSortOrder){
            case SORT_MOST_POPULAR:
                mRadioGroup.check(R.id.most_popular_radiobutton);
                break;
            case SORT_HIGHEST_RATED:
                mRadioGroup.check(R.id.highest_rated_radiobutton);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.most_popular_radiobutton, R.id.highest_rated_radiobutton})
    public void onClick(View view) {
        SortType newSortOrder;

        switch (view.getId()){
            case R.id.most_popular_radiobutton:
                newSortOrder = SortType.SORT_MOST_POPULAR;
                break;
            case R.id.highest_rated_radiobutton:
                newSortOrder = SortType.SORT_HIGHEST_RATED;
                break;
            default:
                newSortOrder = SortType.SORT_MOST_POPULAR;
        }

        if(newSortOrder != mCurrentSortOrder) {
            mOnSortChangedListener.onSortChanged(newSortOrder);
        }
        dismiss();
    }

    interface OnSortChangedListener {
        void onSortChanged(SortType sortOrder);
    }

    public void setOnSortChangedListener(OnSortChangedListener onSortChangedListener){
        mOnSortChangedListener = onSortChangedListener;
    }
}

