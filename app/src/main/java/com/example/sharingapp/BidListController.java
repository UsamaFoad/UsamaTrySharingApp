package com.example.sharingapp;

import android.content.Context;

import java.util.ArrayList;

/**
 * BidListController is responsible for all communication between views and BidList object
 */
public class BidListController {
    private BidList bid_list;

    public BidListController(BidList bid_list) {
        this.bid_list = bid_list;
    }

    public void setBids(ArrayList<Bid> bid_list) {
        this.bid_list.setBids(bid_list);
    }

    public ArrayList<Bid> getBids() {
        return bid_list.getBids();
    }

    public boolean addBid(Bid bid, Context context){
        AddBidCommand add_bid_command = new AddBidCommand(bid_list, bid, context);
        add_bid_command.execute();
        return add_bid_command.isExecuted();
    }

    public void removeBid(Bid bid) {
        this.bid_list.removeBid(bid);
    }

    public void removeItemBids(String id) {
        this.bid_list.removeItemBids(id);
    }

    public Bid getBid(int index) {
        return bid_list.getBid(index);
    }

    public boolean isEmpty() {
        return bid_list.isEmpty();
    }

    public int getIndex(Bid bid) {
        return bid_list.getIndex(bid);
    }

    public int getSize() {
        return bid_list.getSize();
    }

    public ArrayList<Bid> getItemBids(String id) {
        return bid_list.getItemBids(id);
    }

    public Float getHighestBid(String id) {
        return bid_list.getHighestBid(id);
    }

    public String getHighestBidder(String id) {
        return bid_list.getHighestBidder(id);
    }

    public void loadBids(Context context) {
        bid_list.loadBids(context);
    }

    public boolean saveBids(Context context) {
        return bid_list.saveBids(context);
    }

    public void addObserver(Observer observer) {
        bid_list.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        bid_list.removeObserver(observer);
    }
}
