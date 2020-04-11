package com.stardust.practices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

public class PageFragment extends Fragment {

    private static final String ARG_TITLE  = "param1";
    private static final String ARG_LAYOUT = "param2";

    private int titleId;
    private int layoutId;

    public PageFragment() {
    }

    static PageFragment newInstance(int titleId, int layoutId) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE, titleId);
        args.putInt(ARG_LAYOUT, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleId  = getArguments().getInt(ARG_TITLE);
            layoutId = getArguments().getInt(ARG_LAYOUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        ViewStub viewStub = view.findViewById(R.id.viewStub);
        viewStub.setInflatedId(R.id.invisible);
        viewStub.setLayoutResource(layoutId);
        viewStub.inflate();

        return view;
    }

}
