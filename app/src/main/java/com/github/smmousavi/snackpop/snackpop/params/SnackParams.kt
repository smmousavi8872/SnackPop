package com.github.smmousavi.snackpop.snackpop.params

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.github.smmousavi.snackpop.R

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.BINARY)
annotation class SnackTypeScope

open class SnackType {

    @SnackTypeScope
    sealed interface Type {
        fun icon(context: Context): Drawable?
        fun title(context: Context): String?
    }

    @SnackTypeScope
    data object Warning : Type {

        override fun icon(context: Context) =
            ContextCompat.getDrawable(context, R.drawable.ic_toast_warning)

        override fun title(context: Context) =
            context.resources.getString(R.string.txt_warning_title)
    }

    @SnackTypeScope
    data object Error : Type {
        override fun icon(context: Context) =
            ContextCompat.getDrawable(context, R.drawable.ic_toast_error)

        override fun title(context: Context) =
            context.resources.getString(R.string.txt_error_title)
    }

    @SnackTypeScope
    data object Done : Type {
        override fun icon(context: Context) =
            ContextCompat.getDrawable(context, R.drawable.ic_toast_done)

        override fun title(context: Context) =
            context.resources.getString(R.string.txt_done_title)
    }

    @SnackTypeScope
    data object Inform : Type {
        override fun icon(context: Context) =
            ContextCompat.getDrawable(context, R.drawable.ic_toast_inform)

        override fun title(context: Context) =
            context.resources.getString(R.string.txt_inform_title)
    }
}

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.BINARY)
annotation class SnackDismissDurationScope

open class SnackDismissDuration {

    @SnackDismissDurationScope
    sealed interface Duration {
        val value: Int
    }

    @SnackDismissDurationScope
    data object SHORT : Duration {
        override val value: Int
            get() = 1500
    }

    @SnackDismissDurationScope
    data object MIDDLE : Duration {
        override val value: Int
            get() = 3000
    }

    @SnackDismissDurationScope
    data object LONG : Duration {
        override val value: Int
            get() = 5000
    }
}

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.BINARY)
annotation class SnackAnimDurationScope
open class SnackAnimDuration {

    @SnackAnimDurationScope
    sealed interface Duration {
        val value: Long
    }

    @SnackAnimDurationScope
    data object SHORT : Duration {
        override val value: Long
            get() = 500
    }

    @SnackAnimDurationScope
    data object MIDDLE : Duration {
        override val value: Long
            get() = 1000
    }

    @SnackAnimDurationScope
    data object LONG : Duration {
        override val value: Long
            get() = 1500
    }
}
