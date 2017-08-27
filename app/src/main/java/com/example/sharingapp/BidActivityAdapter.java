package com.example.sharingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 *
 */

public class BidActivityAdapter extends ArrayAdapter<Bid> {

    private LayoutInflater inflater;

    public BidActivityAdapter(Context context, ArrayList<Bid> bids) {
        super(context, 0, bids);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bid bid = getItem(position);
        BidController bid_controller = new BidController(bid);

        String bidder = "Bidder: " + bid_controller.getBidderUsername();
        String bid_amount = "Bid: " + bid_controller.getBidAmount();

        // Check if an existing view is being reused, otherwise inflate the view.
        if (convertView == null) {
            convertView = inflater.from(getContext()).inflate(R.layout.bid_item, parent, false);
        }

        TextView bidder_tv = (TextView) convertView.findViewById(R.id.bidder_tv);
        TextView bid_amount_tv = (TextView) convertView.findViewById(R.id.bid_amount_tv);

        bidder_tv.setText(bidder);
        bid_amount_tv.setText(bid_amount);

        return convertView;
    }
}
