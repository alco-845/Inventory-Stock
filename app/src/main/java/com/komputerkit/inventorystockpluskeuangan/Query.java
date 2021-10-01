package com.komputerkit.inventorystockpluskeuangan;

import android.text.TextUtils;

public class Query {
    static String slct = "SELECT * FROM ";

    public static String quoteForLike(String w){
        return "\'%" + Function.addSlashes(w) + "%\'" ;
    }

    public static String select(String table){
        return slct + table;
    }

    public static String selectwhere(String table){
        return Function.slct + table + " WHERE ";
    }

    public static String sOrderASC(String key){
        return " ORDER BY "+key+" ASC" ;
    }

    public static String sOrderDESC(String key){
        return " ORDER BY "+key+" DESC" ;
    }

    public static String sWhere(String key, String value){
        return key+"="+ Function.quote(value) ;
    }

    public static String sLike(String key, String value){
        return key+" LIKE "+quoteForLike(value) ;
    }

    public static String sBetween(String key, String v1, String v2){
        return key+" BETWEEN "+ Function.quote(v1)+" AND "+ Function.quote(v2) ;
    }

    public static String sCount(String table,String key){
        return "SELECT COUNT("+key+") FROM "+table ;
    }

    public static String sSum(String table,String key){
        return "SELECT SUM("+key+") FROM "+table ;
    }

    public static String sAvg(String table,String key){
        return "SELECT AVG("+key+") FROM "+table ;
    }

    public static String splitParam(String query, String[] p){
        String pecah[] = query.split("\\?") ;
        if(pecah.length > 1){
            String fix = "" ;
            if((p.length+1) != pecah.length){
                return "gagal" ;
            } else {
                int i ;
                for (i = 0; i < p.length; i++) {
                    fix += pecah[i] + Function.quote(p[i]) ;
                }
                fix += pecah[i] ;

                return fix ;
            }
        } else {
            return query ;
        }
    }

    public static String slimit(String tabel,String keyCari, String pencarian){
        String hasil = "" ;
        if(TextUtils.isEmpty(pencarian)){
            hasil = Query.select(tabel) +" LIMIT 30"  ;
        } else {
            hasil = Query.selectwhere(tabel) +Query.sLike(keyCari,pencarian) ;
        }
        return hasil ;
    }

    public static String splitParam(String query){
        return query ;
    }
}
