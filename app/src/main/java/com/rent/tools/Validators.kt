package com.rent.tools


class Validators {
    companion object {
        //====================================== check if the phone number is valid ======================================
        fun isPhone(s: String): Boolean {
            return Regex("\\d{8}").matches(s) || Regex("\\+216\\d{8}").matches(s)
        }


    }
}