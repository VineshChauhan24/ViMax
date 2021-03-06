package com.sonphan12.vimax;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sonphan12.vimax.ui.albumlist.AlbumFragment;
import com.sonphan12.vimax.ui.base.BaseFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DummyFragment extends BaseFragment {

    public DummyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dummy, container, false);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        AlbumFragment albumFragment = new AlbumFragment();

        transaction.replace(R.id.fragmentDummy, albumFragment);

        transaction.commit();
        // Inflate the layout for this fragment
        return v;
    }

}
