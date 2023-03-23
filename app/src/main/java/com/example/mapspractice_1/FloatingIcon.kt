package com.example.mapspractice_1

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.getSystemService
import java.lang.Exception

class FloatingIcon(context: Context) {

    private var context: Context? = context
    private var mView: View? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null
    private var layoutInflater: LayoutInflater? = null
    private var layout : LinearLayoutCompat? =null;

    init {
        // set the layout parameters of the window
        mParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather
            // than filling the screen
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
            // through any transparent parts
            PixelFormat.TRANSLUCENT
        )
        // getting a LayoutInflater
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        // inflating the view with the custom layout we created
        mView = layoutInflater?.inflate(R.layout.float_layout, null)
        // set onClickListener on the remove button, which removes
        // the view from the window
        mWindowManager?.addView(mView, mParams)
        mView?.findViewById<ImageButton>(R.id.iv_1)?.setOnClickListener {
            restart()
        }
        //layout = mView?.findViewById(R.id.l1)

        // Define the position of the
        // window within the screen
        mParams!!.gravity = Gravity.LEFT
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    }

    fun open() {
        try {
            // check if the view is already
            // inflated or present in the window
            if (mView?.windowToken == null) {
                if (mView?.parent == null) {
                    mWindowManager!!.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }
    private fun close() {
        try {
            // remove the view from the window

            // remove the view from the window
            (context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(mView)
            // invalidate the view
            // invalidate the view
            mView!!.invalidate()
            // remove all views
            // remove all views
            (mView!!.parent as ViewGroup).removeAllViews()


        } catch (e: Exception) {
            Log.d("Error2", e.toString())
        }
    }
    private fun restart(){

        val intent = Intent(context ,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)



        close()

    }
}