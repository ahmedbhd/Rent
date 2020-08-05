package com.rent.global.utils

import java.text.SimpleDateFormat
import java.util.*


const val SERIAL = "SERIAL"
const val SERIAL_LENGTH = 6


const val WHITE_SPACE_REGEX = "^\\s*$"
const val SUCCESS = "success"
const val ONE_MINUTE_DELAY = 1_000L * 60
const val DEFAULT_ID = 0
const val SECONDS_NUMBER_IN_MINUTE = 60


const val URL_DOMAIN = "url_domain"
const val PORT_NUMBER = "port_number"
const val SSL = "ssl"
const val URL_HTTP = "http://"
const val URL_HTTPS = "https://"
const val URL_SEPARATOR = "://"

val formatDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
val formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
val formatTime = SimpleDateFormat("HH:mm", Locale.FRANCE)
