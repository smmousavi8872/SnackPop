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
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.github.smmousavi.snackpop.R
import java.util.UUID

class SnackPopView : FrameLayout {

    private val id: UUID = UUID.randomUUID()
    private var isShowing: Boolean = false
    private var rootContext: Context? = null
    private var rootView: ViewGroup? = null
    private var viewBinding: ViewBinding? = null
    private var snackMessage: CharSequence? = null
    private var hasAction: Boolean = false
    private var cancelable: Boolean = true

    @SnackTypeScope
    private var snackType: SnackPopType? = null

    @SnackDuration
    private var length = DURATION_MIDDLE
    private var actionTitle: CharSequence? = null
    private var snackClickAction: (() -> Unit)? = null
    private var snackDismissAction: (() -> Unit)? = null
    private var popQueueActions: (() -> Unit)? = null

    private constructor(root: FragmentActivity) : super(root) {
        this.rootView = root.window.decorView.rootView as ViewGroup
        this.rootContext = rootView?.context
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
        @SnackTypeScope type: SnackPopType,
    ): SnackPopView {
        this.snackMessage = message
        this.snackType = type
        return this
    }

    private fun duration(@SnackDuration length: Int): SnackPopView {
        this.length = length
        return this
    }

    private fun action(
        actionTitle: CharSequence?,
        toastAction: (() -> Unit)? = null
    ): SnackPopView {
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

    private fun cancel(toastCancel: (() -> Unit)? = null): SnackPopView {
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
            ViewTaaghcheToastActionBinding.inflate(LayoutInflater.from(rootContext)).also {
                it.txtToastAction.setOnClickListener {
                    snackClickAction?.invoke()
                    dismiss()
                }
            }
        (viewBinding as ViewTaaghcheToastActionBinding).txtToastAction.text = actionTitle
        initializeToastView()
    }

    private fun inflateToastLayout() {
        viewBinding = ViewTaaghcheToastBinding.inflate(LayoutInflater.from(rootContext))
        initializeToastView()
    }

    private fun initializeToastView() {
        val toastTitle = viewBinding?.root?.findViewById<AppCompatTextView?>(R.id.txtToastTitle)
        val toastIcon = viewBinding?.root?.findViewById<AppCompatImageView?>(R.id.imgToastIcon)
        toastTitle?.text = snackMessage
        snackType?.icon()?.let { toastIcon?.setImageResource(it) }
        val parentParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        parentParams.gravity = Gravity.TOP
        val sideMargin = PresentationUtils.convertDpToPixel(32f, rootContext)
        parentParams.setMargins(sideMargin, 0, sideMargin, 0)
        layoutParams = parentParams
        addSwipeActions()
        addView(viewBinding?.root)
    }

    private fun addSwipeActions() {
        setOnTouchListener(object :
            SwipeTouchListener(rootContext!!) {
            override fun rootView(): FrameLayout {
                return this@SnackPopView
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

    private fun startToastTimer(@SnackDuration duration: Int) {
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

    private fun isViewAttached(): Boolean {
        val found =
            rootView?.findViewById<ConstraintLayout?>(R.id.clTaaghcheToastRoot)
        return found != null
    }

    private fun startShowAnimation() {
        val distance = PresentationUtils.getStatusBarHeight(rootContext) +
                PresentationUtils.convertDpToPixel(68f, rootContext)
        attachView()
        alpha = 0.0f
        animate()
            .setDuration(ANIM_LENGTH)
            .translationY(distance.toFloat())
            .alpha(1.0f)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!hasAction) {
                        startToastTimer(length)
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
            .setDuration(ANIM_LENGTH)
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
            .setDuration(ANIM_LENGTH)
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
            .setDuration(ANIM_LENGTH)
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
        rootView?.removeView(this@SnackPopView)
    }

    sealed class Type {

        @DrawableRes
        abstract fun icon(): Int

        @SnackTypeScope
        object Inform : Type() {
            override fun icon(): Int {
                return R.drawable.ic_toast_inform
            }
        }

        @SnackTypeScope
        object Warning : Type() {
            override fun icon(): Int {
                return R.drawable.ic_toast_warning
            }
        }

        @SnackTypeScope
        object Error : Type() {
            override fun icon(): Int {
                return R.drawable.ic_toast_error
            }
        }

        @SnackTypeScope
        object Done : Type() {
            override fun icon(): Int {
                return R.drawable.ic_toast_done
            }
        }
    }

    companion object {
        private const val SWIPE_LEFT_DIST = -700F
        private const val SWIPE_RIGHT_DIST = 700F

        private const val ANIM_LENGTH = 200L

        @SnackDuration
        const val DURATION_LONG = 5000

        @SnackDuration
        const val DURATION_MIDDLE = 3000

        @SnackDuration
        const val DURATION_SHORT = 1500
    }

    object Builder {

        private var toastId: UUID? = null
        private val snackPopViewQueue = ArrayList<SnackPopView>()

        fun buildDone(
            rootActivity: FragmentActivity,
            message: CharSequence
        ): Builder {
            val snackPopView = SnackPopView(rootActivity).makeSnack(message, Type.Done)
            initBuilder(snackPopView)
            return this
        }

        fun buildInform(
            rootActivity: FragmentActivity,
            message: CharSequence
        ): Builder {
            val snackPopView = SnackPopView(rootActivity).makeSnack(message, Type.Inform)
            initBuilder(snackPopView)
            return this
        }

        fun buildWarning(
            rootActivity: FragmentActivity,
            message: CharSequence
        ): Builder {
            val snackPopView = SnackPopView(rootActivity).makeSnack(message, Type.Warning)
            initBuilder(snackPopView)
            return this
        }

        fun buildError(
            rootActivity: FragmentActivity,
            message: CharSequence
        ): Builder {
            val snackPopView = SnackPopView(rootActivity).makeSnack(message, Type.Error)
            initBuilder(snackPopView)
            return this
        }

        private fun initBuilder(snackPopView: SnackPopView) {
            toastId = snackPopView.id
            snackPopViewQueue.add(snackPopView)
            snackPopView.popQueueActions = {
                if (snackPopViewQueue.size > 0) {
                    snackPopViewQueue.removeAt(0)
                }
                if (snackPopViewQueue.size > 0) {
                    show()
                }
            }
        }

        fun duration(@SnackDuration length: Int): Builder {
            currentToast().duration(length)
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
            if (snackPopViewQueue[0].isShowing.not()) {
                snackPopViewQueue[0].show()
            }
        }

        private fun currentToast(): SnackPopView {
            return snackPopViewQueue.filter { it.id == toastId }[0]
        }
    }
}