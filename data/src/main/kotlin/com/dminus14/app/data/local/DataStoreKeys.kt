package com.dminus14.app.data.local

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    object Auth {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    // 예시: 이후 도메인별 키는 nested object로 추가
    // object User {
    //     val NICKNAME = stringPreferencesKey("nickname")
    //     val AGE = intPreferencesKey("age")
    //     val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    // }
}
