package com.dotengine.linsir.morepeoplevoiceroom;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 *  Created by linSir 
 *  date at 2017/5/25.
 *  describe: quickAdapter
 */

public class QuickAdapter extends BaseQuickAdapter<Img, BaseViewHolder> {

    public QuickAdapter() {
        super(R.layout.item);
    }

    @Override protected void convert(BaseViewHolder helper, Img item) {
        Glide.with(mContext).load(item.getUrl()).into((ImageView) helper.getView(R.id.user));

        helper.setVisible(R.id.talking, !item.isTalking());

    }

}
