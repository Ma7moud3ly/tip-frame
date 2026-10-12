package com.ma7moud3ly.tipframe.model

import io.ma7moud3ly.nemo.model.Language

/** The languages offered by the language menu, in menu order. */
val tipFrameLanguages: List<Language> = listOf(
    Language.KOTLIN,
    Language.JAVA,
    Language.PYTHON,
    Language.JAVASCRIPT,
    Language.TYPESCRIPT,
    Language.SWIFT,
    Language.DART,
    Language.GO,
    Language.RUST,
    Language.C,
    Language.CPP,
    Language.PHP,
    Language.RUBY,
    Language.HTML,
    Language.CSS,
    Language.JSON,
    Language.XML,
    Language.YAML,
    Language.SQL,
    Language.SHELL,
    Language.MARKDOWN,
    Language.TEXT
)

/** The name shown to the user. */
val Language.displayName: String
    get() = when (this) {
        Language.KOTLIN -> "Kotlin"
        Language.JAVA -> "Java"
        Language.PYTHON -> "Python"
        Language.JAVASCRIPT -> "JavaScript"
        Language.TYPESCRIPT -> "TypeScript"
        Language.SWIFT -> "Swift"
        Language.DART -> "Dart"
        Language.GO -> "Go"
        Language.RUST -> "Rust"
        Language.C -> "C"
        Language.CPP -> "C++"
        Language.PHP -> "PHP"
        Language.RUBY -> "Ruby"
        Language.HTML -> "HTML"
        Language.CSS -> "CSS"
        Language.JSON -> "JSON"
        Language.XML -> "XML"
        Language.YAML -> "YAML"
        Language.SQL -> "SQL"
        Language.SHELL -> "Shell"
        Language.MARKDOWN -> "Markdown"
        Language.TEXT -> "Plain text"
        else -> name.lowercase().replaceFirstChar { it.uppercase() }
    }

/** The language's name in the page address. */
val Language.key: String get() = name.lowercase()

/** The language with this [key], or null when there is none. */
fun languageFromKey(key: String?): Language? = Language.entries.firstOrNull { it.key == key }
