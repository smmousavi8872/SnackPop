package com.github.smmousavi.snackpop.snackpop

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.BINARY)
annotation class SnackTypeScope

class SnackPopType {
    
    @SnackTypeScope
    sealed interface SnackType {
        fun icon(): Int
        fun title(): String
    }

    @SnackTypeScope
    data object Warning : SnackType {
        override fun icon(): Int {
            TODO("Not yet implemented")
        }

        override fun title(): String {
            TODO("Not yet implemented")
        }
    }

    @SnackTypeScope
    data object Error : SnackType {
        override fun icon(): Int {
            TODO("Not yet implemented")
        }

        override fun title(): String {
            TODO("Not yet implemented")
        }
    }

    @SnackTypeScope
    data object Done : SnackType {
        override fun icon(): Int {
            TODO("Not yet implemented")
        }

        override fun title(): String {
            TODO("Not yet implemented")
        }
    }

    @SnackTypeScope
    data object Inform : SnackType {
        override fun icon(): Int {
            TODO("Not yet implemented")
        }

        override fun title(): String {
            TODO("Not yet implemented")
        }
    }
}

