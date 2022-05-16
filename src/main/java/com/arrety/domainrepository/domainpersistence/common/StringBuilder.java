package com.arrety.domainrepository.domainpersistence.common;

/**
 * @author arrety
 * @date 2022/2/10 21:25
 */
public class StringBuilder {

    private java.lang.StringBuilder stringBuilder = new java.lang.StringBuilder();

    public StringBuilder() {
    }

    public StringBuilder(String str) {
        this.stringBuilder = new java.lang.StringBuilder(str);
    }

    public StringBuilder append(String append){
        stringBuilder.append(append).append(" ");
        return this;
    }

    public StringBuilder append(String... appends){
        for (String append : appends) {
            stringBuilder.append(append);
        }
        return this;
    }

    @Override
    public String toString(){
        return stringBuilder.toString();
    }

    public void clear() {
        stringBuilder = new java.lang.StringBuilder();
    }
}
