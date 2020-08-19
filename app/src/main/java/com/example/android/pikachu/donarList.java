package com.example.android.pikachu;

public class donarList {

    private String Name, Blood, City, Date, Gender, Email;

    donarList(String defaultName, String defaultBlood, String defaultCity, String defaultDate, String defaultGender, String defaultEmail) {
        Name = defaultName;
        Blood = defaultBlood;
        City = defaultCity;
        Date = defaultDate;
        Gender = defaultGender;
        Email = defaultEmail;
    }

    public String getName() {
        return Name;
    }

    public String getBlood() {
        return Blood;
    }

    public String getCity() {
        return City;
    }

    public String getDate() {
        return Date;
    }

    public String getGender() {
        return Gender;
    }

    public String getEmail() {
        return Email;
    }
}
