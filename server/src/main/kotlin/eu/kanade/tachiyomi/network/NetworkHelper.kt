package eu.kanade.tachiyomi.network

/*
 * Copyright (C) Contributors to the Suwayomi project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import android.content.Context
import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class NetworkHelper(
    val context: Context,
) {
    // Tachidesk -->
    val cookieStore = MemoryCookieJar()
    // Tachidesk <--

    val client by lazy {
        val builder =
            OkHttpClient
                .Builder()
                .cookieJar(cookieStore)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
        builder.build()
    }

    val cloudflareClient by lazy {
        client
            .newBuilder()
            .addInterceptor(CloudflareInterceptor())
            .build()
    }

    private val defaultUserAgent by lazy {
        context
            .getSharedPreferences(
                "m_mangayomi",
                Context.MODE_PRIVATE,
            ).getString("m_user_agent", System.getProperty("http.agent").orEmpty())
    }

    fun setUA(ua: String) {
        context
            .getSharedPreferences(
                "m_mangayomi",
                Context.MODE_PRIVATE,
            ).edit()
            .putString("m_user_agent", ua)
            .apply()
    }

    fun defaultUserAgentProvider() = defaultUserAgent
}
