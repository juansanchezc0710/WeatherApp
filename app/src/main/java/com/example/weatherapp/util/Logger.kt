package com.example.weatherapp.util

import android.util.Log

/**
 * Utility class for logging in the application.
 * Provides simple methods for different log levels.
 */
object Logger {
    
    private const val DEFAULT_TAG = "WeatherApp"
    private const val MAX_TAG_LENGTH = 23
    
    /**
     * Logs a DEBUG message.
     *
     * @param message The message to log
     * @param tag Optional tag (defaults to DEFAULT_TAG)
     */
    fun d(message: String, tag: String = DEFAULT_TAG) {
        Log.d(truncateTag(tag), message)
    }
    
    /**
     * Logs an INFO message.
     *
     * @param message The message to log
     * @param tag Optional tag (defaults to DEFAULT_TAG)
     */
    fun i(message: String, tag: String = DEFAULT_TAG) {
        Log.i(truncateTag(tag), message)
    }
    
    /**
     * Logs a WARNING message.
     *
     * @param message The message to log
     * @param tag Optional tag (defaults to DEFAULT_TAG)
     */
    fun w(message: String, tag: String = DEFAULT_TAG) {
        Log.w(truncateTag(tag), message)
    }
    
    /**
     * Logs an ERROR message with an exception.
     *
     * @param message The message to log
     * @param throwable The exception to log
     * @param tag Optional tag (defaults to DEFAULT_TAG)
     */
    fun e(message: String, throwable: Throwable, tag: String = DEFAULT_TAG) {
        Log.e(truncateTag(tag), message, throwable)
    }
    
    /**
     * Truncates the tag if it exceeds the maximum length.
     * Android has a limit of 23 characters for log tags.
     *
     * @param tag The tag to truncate
     * @return The truncated tag
     */
    private fun truncateTag(tag: String): String {
        return if (tag.length > MAX_TAG_LENGTH) {
            tag.take(MAX_TAG_LENGTH)
        } else {
            tag
        }
    }
}

