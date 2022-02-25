package com.mactrixapp.www.stranger.Model;

import java.util.ArrayList;

public class IsNoticeContain {




    public boolean isUserContain(ArrayList<Notification> userlist, Notification uservalue){
        boolean check = false;


        for (int i = 0; i< userlist.size(); i++){


            if(userlist.get(i).getDate() == uservalue.getDate()){
                check = true;
                break;
            }

            if (i == userlist.size()-1 && !(userlist.get(i).getDate() == uservalue.getDate())){
                check = false;
            }



        }



        return check ;
    }



    public boolean isUserIDContain(ArrayList<Notification> userlist, Notification uservalue){
        boolean check = false;


        for (int i = 0; i< userlist.size(); i++){


            if (userlist.get(i).getReceiverid() != null && uservalue.getReceiverid() != null) {
                if(userlist.get(i).getReceiverid().equalsIgnoreCase(uservalue.getReceiverid())){
                    check = true;
                    break;
                }

                if (i == userlist.size()-1 && !(userlist.get(i).getReceiverid().equalsIgnoreCase(uservalue.getReceiverid()))){
                    check = false;
                }
            }


        }



        return check ;
    }


}
