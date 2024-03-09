package com.github.smmousavi.snackpop.snackpop

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.github.smmousavi.snackpop.R
import com.github.smmousavi.snackpop.snackpop.params.SnackAnimDuration
import com.github.smmousavi.snackpop.snackpop.params.SnackAnimDurationScope
import com.github.smmousavi.snackpop.snackpop.params.SnackDismissDuration
import com.github.smmousavi.snackpop.snackpop.params.SnackDismissDurationScope
import com.github.smmousavi.snackpop.snackpop.params.SnackType
import com.github.smmousavi.snackpop.snackpop.params.SnackTypeScope
import java.util.UUID

class SnackLayout : FrameLayout {

    private val id: UUID = UUID.randomUUID()
    private var isShowing: Boolean = false
    private var rootView: ViewGroup? = null
    private var viewBinding: ViewBinding? = null
    private var snackMessage: CharSequence? = null
    private var hasAction: Boolean = false
    private var cancelable: Boolean = true

    @SnackTypeScope
    var type: SnackType.Type = SnackType.Done

    @SnackDismissDurationScope
    var dismissDuration: SnackDismissDuration.Duration = SnackDismissDuration.SHORT

    @SnackAnimDurationScope
    var animDuration: SnackAnimDuration.Duration = SnackAnimDuration.SHORT

    private var actionTitle: CharSequence? = null
    private var snackClickAction: (() -> Unit)? = null
    private var snackDismissAction: (() -> Unit)? = null
    private var popQueueActions: (() -> Unit)? = null

    private constructor(root: FragmentActivity) : super(root) {
        this.rootView = root.window.decorView.rootView as ViewGroup
    }

    private constructor(context: Context) : super(context)

    private constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun makeSnack(
        message: CharSequence,
        @SnackTypeScope type: SnackType.Type
    ): SnackLayout {
        this.snackMessage = message
        this.type = type
        return this
    }

    private fun duration(@SnackDismissDurationScope duration: SnackDismissDuration.Duration): SnackLayout {
        this.dismissDuration = duration
        return this
    }

    private fun action(
        actionTitle: CharSequence?,
        toastAction: (() -> Unit)? = null
    ): SnackLayout {
        this.hasAction = true
        this.actionTitle = actionTitle
        this.snackClickAction = toastAction
        return this
    }

    private fun inflate() {
        if (hasAction) {
            inflateToastActionLayout()
        } else {
            inflateToastLayout()
        }
    }

    private fun cancel(toastCancel: (() -> Unit)? = null): SnackLayout {
        this.snackDismissAction = toastCancel
        return this
    }

    private fun show() {
        inflate()
        startShowAnimation()
    }

    private fun dismiss() {
        startDismissAnimation {
            detachView()
            popQueueActions?.invoke()
        }
    }

    private fun inflateToastActionLayout() {
        viewBinding =
            ViewTaaghcheToastActionBinding.inflate(LayoutInflater.from(context)).also {
                it.txtToastAction.setOnClickListener {
                    snackClickAction?.invoke()
                    dismiss()
                }
            }
        (viewBinding as ViewTaaghcheToastActionBinding).txtToastAction.text = actionTitle
        initializeToastView()
    }

    private fun inflateToastLayout() {
        viewBinding = ViewTaaghcheToastBinding.inflate(LayoutInflater.from(context))
        initializeToastView()
    }

    private fun initializeToastView() {
        val toastTitle = viewBinding?.root?.findViewById<AppCompatTextView?>(R.id.txtToastTitle)
        val toastIcon = viewBinding?.root?.findViewById<AppCompatImageView?>(R.id.imgToastIcon)
        toastTitle?.text = snackMessage
        type.icon(context).let { toastIcon?.setImageResource(it) }
        val parentParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        parentParams.gravity = Gravity.TOP
        val sideMargin = UiUtils.convertDpToPixel(32f, context)
        parentParams.setMargins(sideMargin, 0, sideMargin, 0)
        layoutParams = parentParams
        addSwipeActions()
        addView(viewBinding?.root)
    }

    private fun addSwipeActions() {
        setOnTouchListener(object :
            SwipeTouchListener(context) {
            override fun rootView(): FrameLayout {
                return this@SnackLayout
            }

            override fun onSingleTap() {

            }

            override fun onSwipeRightStart() {
                if (cancelable) {
                    startSwipeRightDismissAnimation {
                        detachView()
                        popQueueActions?.invoke()
                        snackDismissAction?.invoke()
                    }
                }
            }

            override fun onSwipeLeftStart() {
                if (cancelable) {
                    startSwipeLeftDismissAnimation {
                        detachView()
                        popQueueActions?.invoke()
                        snackDismissAction?.invoke()
                    }
                }
            }

            override fun onSwipeRightEnd() {

            }

            override fun onSwipeLeftEnd() {

            }
        })
    }

    private fun startToastTimer(@SnackDismissDurationScope duration: Int) {
        val toastTimer = viewBinding?.root?.findViewById<ProgressBar?>(R.id.prgToastTimer)
        toastTimer?.max = duration
        val animation: ObjectAnimator =
            ObjectAnimator.ofInt(toastTimer, "progress", 0, duration)
        animation.duration = duration.toLong()
        animation.interpolator = DecelerateInterpolator()
        animation.start()
        if (!hasAction) {
            animation.addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    dismiss()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    private fun startShowAnimation() {
        val distance = UiUtils.getStatusBarHeight(context) +
                UiUtils.convertDpToPixel(68f, context)
        attachView()
        alpha = 0.0f
        animate()
            .setDuration(animDuration.value)
            .translationY(distance.toFloat())
            .alpha(1.0f)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!hasAction) {
                        startToastTimer(dismissDuration.value)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
    }

    private fun startDismissAnimation(onDismiss: () -> Unit) {
        alpha = 1.0f
        animate()
            .setDuration(animDuration.value)
            .translationY(0f)
            .alpha(0.0f)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    onDismiss()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
    }

    private fun startSwipeRightDismissAnimation(onDismiss: () -> Unit) {
        alpha = 1.0f
        animate()
            .setDuration(animDuration.value)
            .translationX(SWIPE_RIGHT_DIST)
            .alpha(0.0f)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    onDismiss()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
    }

    private fun startSwipeLeftDismissAnimation(onDismiss: () -> Unit) {
        alpha = 1.0f
        animate()
            .setDuration(animDuration.value)
            .translationX(SWIPE_LEFT_DIST)
            .alpha(0.0f)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    onDismiss()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
    }

    private fun attachView() {
        isShowing = true
        rootView?.addView(this)
    }

    private fun detachView() {
        isShowing = false
        rootView?.removeView(this@SnackLayout)
    }


    object Builder {

        private var toastId: UUID? = null
        private val snackPopLayoutQueue = ArrayList<SnackLayout>()

        private fun initBuilder(snackPopLayout: SnackLayout) {
            toastId = snackPopLayout.id
            snackPopLayoutQueue.add(snackPopLayout)
            snackPopLayout.popQueueActions = {
                if (snackPopLayoutQueue.size > 0) {
                    snackPopLayoutQueue.removeAt(0)
                }
                if (snackPopLayoutQueue.size > 0) {
                    show()
                }
            }
        }

        private fun currentToast(): SnackLayout {
            return snackPopLayoutQueue.filter { it.id == toastId }[0]
        }

        fun makeByType(
            @SnackTypeScope type: SnackType.Type,
            rootActivity: FragmentActivity,
            message: CharSequence
        ): Builder {
            SnackLayout(rootActivity).makeSnack(message, type).also {
                initBuilder(it)
            }
            return this
        }

        fun duration(@SnackDismissDurationScope duration: SnackDismissDuration.Duration): Builder {
            currentToast().duration(duration)
            return this
        }

        fun action(actionTitle: CharSequence?, toastAction: (() -> Unit)? = null): Builder {
            currentToast().action(actionTitle, toastAction)
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            currentToast().cancelable = cancelable
            return this
        }

        fun onCancel(toastDismiss: (() -> Unit)? = null): Builder {
            currentToast().cancel(toastDismiss)
            return this
        }

        fun show() {
            if (snackPopLayoutQueue[0].isShowing.not()) {
                snackPopLayoutQueue[0].show()
            }
        }
    }

    companion object {
        private const val SWIPE_LEFT_DIST = -700F
        private const val SWIPE_RIGHT_DIST = 700F
    }
}