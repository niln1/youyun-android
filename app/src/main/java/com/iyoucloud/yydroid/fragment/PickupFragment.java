package com.iyoucloud.yydroid.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYCard;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class PickupFragment extends Fragment {

    public PickupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pickup, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCards();
    }

    private void initCards() {

        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < 5; i++) {
            YYCard card = new YYCard(getActivity(), "Student name" + i, R.layout.card_content);
            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.yy_card_list);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
    }

}