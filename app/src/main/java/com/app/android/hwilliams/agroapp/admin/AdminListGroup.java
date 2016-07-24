package com.app.android.hwilliams.agroapp.admin;

import java.util.List;

/**
 * Created by Hernan on 7/23/2016.
 */
public class AdminListGroup {
    private String rubro;

    private List<AdminParque> parques;

    public AdminListGroup(String rubro, List<AdminParque> p){
        this.rubro = rubro;
        this.parques = p;
    }

    public List<AdminParque> getParques() {
        return parques;
    }

    public String getRubro() {
        return rubro;
    }
}
