package com.example.selector.utils

import java.lang.Exception

object ValueOf {
    fun toString(o: Any): String {
        var value = ""
        try {
            value = o.toString()
        } catch (e: Exception) {
        }
        return value
    }

    @JvmOverloads
    fun toDouble(o: Any?, defaultValue: Int = 0): Double {
        if (o == null) {
            return defaultValue.toDouble()
        }
        val value: Double
        value = try {
            o.toString().trim { it <= ' ' }.toDouble()
        } catch (e: Exception) {
            defaultValue.toDouble()
        }
        return value
    }

    @JvmOverloads
    fun toLong(o: Any?, defaultValue: Long = 0): Long {
        if (o == null) {
            return defaultValue
        }
        var value: Long = 0
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toLong()
            } else {
                s.toLong()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    @JvmOverloads
    fun toFloat(o: Any?, defaultValue: Long = 0): Float {
        if (o == null) {
            return defaultValue.toFloat()
        }
        var value = 0f
        value = try {
            val s = o.toString().trim { it <= ' ' }
            s.toFloat()
        } catch (e: Exception) {
            defaultValue.toFloat()
        }
        return value
    }

    @JvmOverloads
    fun toInt(o: Any?, defaultValue: Int = 0): Int {
        if (o == null) {
            return defaultValue
        }
        val value: Int
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toInt()
            } else {
                s.toInt()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    @JvmOverloads
    fun toBoolean(o: Any?, defaultValue: Boolean = false): Boolean {
        if (o == null) {
            return false
        }
        val value: Boolean
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if ("false" == s.trim { it <= ' ' }) {
                false
            } else {
                true
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    fun <T> to(o: Any?, defaultValue: T): T {
        if (o == null) {
            return defaultValue
        }
        val value = o as T
        return value
    }
}
