package com.master.bojan.model;

/**
 * Created by bojan on 2.4.18..
 */

public class QuerySelectionPair {

    private String querySelection;
    private String tablesJoin;

    public QuerySelectionPair(){}

    public QuerySelectionPair(String querySelection, String tablesJoin) {
        this.querySelection = querySelection;
        this.tablesJoin = tablesJoin;
    }

    public String getQuerySelection() {
        return querySelection;
    }

    public void setQuerySelection(String querySelection) {
        this.querySelection = querySelection;
    }

    public String getTablesJoin() {
        return tablesJoin;
    }

    public void setTablesJoin(String tablesJoin) {
        this.tablesJoin = tablesJoin;
    }
}
