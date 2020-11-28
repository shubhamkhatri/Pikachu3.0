/*
 *  Copyright (c) 2020 Pikachu(shubham khatri). All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.example.android.pikachu;

public class AlarmList {
    private String medicineName, quantity, quality;
    private int hour, minute, everyday, sunday, monday, tuesday, wednesday, thursday, friday, saturday, state;

    AlarmList(String medicineName, int hour, int minute, String quantity, String quality, int everyday,
              int sunday, int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int state) {
        this.medicineName = medicineName;
        this.hour = hour;
        this.minute = minute;
        this.quantity = quantity;
        this.quality = quality;
        this.everyday = everyday;
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.state = state;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getQuality() {
        return quality;
    }

    public int getEveryday() {
        return everyday;
    }

    public int getSunday() {
        return sunday;
    }

    public int getMonday() {
        return monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public int getFriday() {
        return friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public int getState() {
        return state;
    }

}
