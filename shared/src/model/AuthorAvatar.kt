package com.ma7moud3ly.tipframe.model

/**
 * What the author badge under the code shows as its avatar. [key] is its name
 * in the page address.
 *
 * [NONE] hides the badge. [INITIALS] shows the first letters of the author's
 * name, such as "MA". The others show the mark of a social network, for a
 * handle on that network.
 */
enum class AuthorAvatar(val key: String) {
    NONE("none"),
    INITIALS("initials"),
    LINKEDIN("linkedin"),
    X("x"),
    INSTAGRAM("instagram"),
    FACEBOOK("facebook");

    companion object {
        fun fromKey(key: String?): AuthorAvatar? = entries.firstOrNull { it.key == key }
    }
}
