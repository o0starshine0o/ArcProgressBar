package com.abelhu.stepdemo

import android.animation.AnimatorInflater
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qicode.bubbleview.BubbleLayoutProxy

class BubbleAdapter : RecyclerView.Adapter<BubbleAdapter.BubbleHolder>(), BubbleLayoutProxy {
    private val list = MutableList(5) { i -> Bubble(i) }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = when (position) {
        0 -> 0
        else -> 1
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
        holder.initHolder(list[position])
    }

    override fun getRect(index: Int) = list[index].rect

    override fun setRect(index: Int, rect: Rect) {
        list[index].rect = rect
    }

    override fun clear() = list.forEach { bubble -> bubble.rect = null }

    fun addBubble() {
        list.add(Bubble(list.size))
        notifyDataSetChanged()
    }

    fun reduceBubble() {
        if (list.size > 0) list.removeAt(0)
        notifyDataSetChanged()
    }

    class BubbleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.findViewById<TextView>(R.id.textView)

        fun initHolder(bubble: Bubble) {
            textView.text = "${bubble.index}"
            textView.background = when (bubble.index % 2) {
                0 -> ContextCompat.getDrawable(textView.context, R.drawable.bubble_primary)
                else -> ContextCompat.getDrawable(textView.context, R.drawable.bubble_secondary)
            }
        }
    }
}