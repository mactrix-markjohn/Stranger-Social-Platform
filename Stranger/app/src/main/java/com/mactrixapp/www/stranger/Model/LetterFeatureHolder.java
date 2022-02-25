package com.mactrixapp.www.stranger.Model;

public class LetterFeatureHolder {

    private String receiver;
    private String sender;

    private String namereceive;
    private String namesender;

    private String professionreceive;
    private String professionsender;

    private String interestreceive;
    private String interestsender;

    private String institutereceive;
    private String institutesender;

    private String genderreceive;
    private String gendersender;

    private String workreceive;
    private String worksender;

    private String locationreceive;
    private String locationsenderstate;

    public LetterFeatureHolder() {
    }


    public LetterFeatureHolder(String receiver, String sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getNamereceive() {
        return namereceive;
    }

    public void setNamereceive(String namereceive) {
        this.namereceive = namereceive;
    }

    public String getNamesender() {
        return namesender;
    }

    public void setNamesender(String namesender) {
        this.namesender = namesender;
    }


    public void name(String namereceive,String namesender){
        this.namereceive = namereceive;
        this.namesender = namesender;
    }


    public String getProfessionreceive() {
        return professionreceive;
    }

    public void setProfessionreceive(String professionreceive) {
        this.professionreceive = professionreceive;
    }

    public String getProfessionsender() {
        return professionsender;
    }

    public void setProfessionsender(String professionsender) {
        this.professionsender = professionsender;

    }

    public void profession(String professionreceive,String professionsender){
        this.professionreceive = professionreceive;
        this.professionsender = professionsender;
    }

    public String getInterestreceive() {
        return interestreceive;
    }

    public void setInterestreceive(String interestreceive) {
        this.interestreceive = interestreceive;
    }

    public String getInterestsender() {
        return interestsender;
    }

    public void setInterestsender(String interestsender) {
        this.interestsender = interestsender;
    }

    public void interest(String interestreceive,String interestsender){
        this.interestreceive = interestreceive;
        this.interestsender = interestsender;
    }

    public String getInstitutereceive() {
        return institutereceive;
    }

    public void setInstitutereceive(String institutereceive) {
        this.institutereceive = institutereceive;
    }

    public String getInstitutesender() {
        return institutesender;
    }

    public void setInstitutesender(String institutesender) {
        this.institutesender = institutesender;
    }

    public void institute(String institutereceive,String institutesender){

        this.institutereceive = institutereceive;
        this.institutesender = institutesender;

    }

    public String getGenderreceive() {
        return genderreceive;
    }

    public void setGenderreceive(String genderreceive) {
        this.genderreceive = genderreceive;
    }

    public String getGendersender() {
        return gendersender;
    }

    public void setGendersender(String gendersender) {
        this.gendersender = gendersender;
    }

    public void gender(String genderreceive,String gendersender){
        this.genderreceive = genderreceive;
        this.gendersender = gendersender;
    }

    public String getWorkreceive() {
        return workreceive;
    }

    public void setWorkreceive(String workreceive) {
        this.workreceive = workreceive;
    }

    public String getWorksender() {
        return worksender;
    }

    public void setWorksender(String worksender) {
        this.worksender = worksender;
    }

    public void workstate(String workreceive,String worksender){
        this.workreceive = workreceive;
        this.worksender = worksender;
    }

    public String getLocationreceive() {
        return locationreceive;
    }

    public void setLocationreceive(String locationreceive) {
        this.locationreceive = locationreceive;
    }

    public String getLocationsenderstate() {
        return locationsenderstate;
    }

    public void setLocationsenderstate(String locationsenderstate) {
        this.locationsenderstate = locationsenderstate;
    }

    public void location(String locationreceive,String locationsenderstate){
        this.locationreceive = locationreceive;
        this.locationsenderstate = locationsenderstate;


    }
}
