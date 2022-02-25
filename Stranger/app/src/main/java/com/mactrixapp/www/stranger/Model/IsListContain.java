package com.mactrixapp.www.stranger.Model;

import java.util.ArrayList;

public class IsListContain {




    public boolean isUserContain(ArrayList<User> userlist, User uservalue){
        boolean check = false;


        for (int i = 0; i< userlist.size(); i++){


            if(userlist.get(i).getUserid().equalsIgnoreCase(uservalue.getUserid())){
                check = true;
                break;
            }

            if (i == userlist.size()-1 && !userlist.get(i).getUserid().equalsIgnoreCase(uservalue.getUserid())){
                check = false;
            }



        }



        return check ;
    }

    public boolean isReputationContain(ArrayList<ReputationModel> userlist, ReputationModel uservalue){
        boolean check = false;


        for (int i = 0; i< userlist.size(); i++){


            if(userlist.get(i).getUser().getUserid().equalsIgnoreCase(uservalue.getUser().getUserid())){
                check = true;
                break;
            }

            if (i == userlist.size()-1 && !userlist.get(i).getUser().getUserid().equalsIgnoreCase(uservalue.getUser().getUserid())){
                check = false;
            }



        }



        return check ;
    }




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

    public boolean isGroupContain(ArrayList<Group> grouplist, Group group){
        boolean check = false;


        for (int i = 0; i< grouplist.size(); i++){


            if (grouplist.get(i).getId() != null && group.getId() != null) {
                if(grouplist.get(i).getId().equalsIgnoreCase(group.getId())){
                    check = true;
                    break;
                }

                if (i == grouplist.size()-1 && !(grouplist.get(i).getId().equalsIgnoreCase(group.getId()))){
                    check = false;
                }
            }


        }



        return check ;
    }

    public boolean isChatContain(ArrayList<Chat> chatlist, Chat chat){
        boolean check = false;

        for (int i = 0; i< chatlist.size(); i++){


            if(chatlist.get(i).getDate() == chat.getDate()){
                    check = true;
                    break;
            }

            if (i == chatlist.size()-1 && !(chatlist.get(i).getDate() == chat.getDate())){
                    check = false;
            }



        }



        return check;



    }

}
