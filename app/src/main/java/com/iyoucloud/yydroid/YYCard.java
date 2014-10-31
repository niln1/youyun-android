package com.iyoucloud.yydroid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardView;

public class YYCard extends Card {

    protected String mTitleHeader;
    protected String mTitleMain;

    public YYCard(Context context, String titleHeader, String titleMain) {
        super(context, R.layout.card_thumbnail_layout);
        this.mTitleHeader = titleHeader;
        this.mTitleMain = titleMain;
        init(context);
    }

    public YYCard(Context context, String title, int innerLayout) {
        super(context, innerLayout);
        this.mTitleHeader = title;
        init(context);
    }


    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView mTitle = (TextView) parent.findViewById(R.id.yy_thumb_card_title);
        if (mTitle != null){
            mTitle.setText(mTitleHeader);
        }
    }

    private void init(Context context) {

        CardThumbnail cardThumbnail = new CardThumbnail(mContext);
        cardThumbnail.setDrawableResource(R.drawable.default_user);
        addCardThumbnail(cardThumbnail);



        //Add ClickListener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getContext(), "Clicked on student " + mTitleHeader, Toast.LENGTH_SHORT).show();
            }
        });

        //Set the card inner text
    //    setTitle(mTitleMain);

    }
}
