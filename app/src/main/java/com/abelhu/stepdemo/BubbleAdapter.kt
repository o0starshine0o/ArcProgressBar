package com.abelhu.stepdemo

import android.animation.AnimatorInflater
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

class BubbleAdapter : RecyclerView.Adapter<BubbleAdapter.BubbleHolder>() {
    private var count = 20

    override fun getItemCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bubble, parent, false)
        val animator = AnimatorInflater.loadAnimator(view.context, R.animator.translation_y_10_2s_repeat)
        animator.setTarget(view)
        animator.start()

        return when (viewType) {
            0 -> BubbleHolder(view)
            else -> BubbleHolder(view)
        }
    }

    override fun onBindViewHolder(holder: BubbleHolder, position: Int) {
        holder.initHolder(position)
    }

    fun addBubble(){
        count++
        notifyDataSetChanged()
    }

    fun reduceBubble(){
        if(count > 0) count--
        notifyDataSetChanged()
    }

    class BubbleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.findViewById<TextView>(R.id.textView)

        fun initHolder(position: Int) {
            textView.text = "${Random().nextInt(999)}"
            textView.background = when (position % 2) {
                0 -> ContextCompat.getDrawable(textView.context, R.drawable.bubble_primary)
                else -> ContextCompat.getDrawable(textView.context, R.drawable.bubble_secondary)
            }
        }
    }
}