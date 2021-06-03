package com.iteration.koreanlearningpal.ui.testing;

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginEnd
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.iteration.koreanlearningpal.R
import com.iteration.koreanlearningpal.module.TriStateBoolean
import com.iteration.koreanlearningpal.module.Word;
import org.w3c.dom.Text
import timber.log.Timber
import java.awt.font.TextAttribute
import java.util.*
import kotlin.collections.HashMap

import kotlin.collections.List;
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class TestingCardStackAdapter(
    private var words: List<Word> = emptyList(),
    private val numberOfAnswers: Int
) : RecyclerView.Adapter<TestingCardStackAdapter.ViewHolder>(), TextToSpeech.OnInitListener{



    private lateinit var tts: TextToSpeech


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_word, parent, false))
    }

    override fun getItemCount(): Int {
        return words.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]

        tts = TextToSpeech(holder.itemView.context, this)

        // Set the korean word at the top
        holder.koreanWord.text = word.koreanWord
        holder.koreanWord.textSize = 48f

        holder.koreanWord.setOnClickListener {
            Timber.d("Been there, trying to TTS. Text to speak: ${word.koreanWord}")
            tts.speak(word.koreanWord, TextToSpeech.QUEUE_ADD, null, null)
        }

        // Get a list of french translations
        val answersMap = HashMap<String, Boolean>()
        answersMap[word.frenchWord] = false
        while(answersMap.size < numberOfAnswers)
        {
            answersMap[words.random().frenchWord] = false // Add a random french translation
        }

        val colCount = ceil(sqrt(numberOfAnswers.toDouble())).toInt()
        val rowCount = ceil(numberOfAnswers/ceil(sqrt(numberOfAnswers.toDouble()))).toInt()
        holder.layoutFrenchAnswers.columnCount = colCount // str(math.ceil(sq)) + "x" + str(math.ceil(i/math.ceil(sq)))
        holder.layoutFrenchAnswers.rowCount = rowCount // str(math.ceil(sq)) + "x" + str(math.ceil(i/math.ceil(sq)))

        for(it in answersMap.entries.indices) // answer in answersSet
        {
            val frenchAnswer = TextView(holder.view.context)
            val gridParams = GridLayout.LayoutParams()
            gridParams.height = GridLayout.LayoutParams.WRAP_CONTENT; gridParams.width = GridLayout.LayoutParams.WRAP_CONTENT
            gridParams.columnSpec = GridLayout.spec(it % colCount, 1f)                       // weight : percentage taken by the cell. 1 for all the cells => equal splitting
            gridParams.rowSpec = GridLayout.spec(floor(it.toDouble()/colCount).toInt(), 1f)    // weight must be set horizontally AND vertically
            gridParams.setGravity(Gravity.FILL)                                                           // Filling the space
            gridParams.setMargins((holder.view.resources.getDimension(R.dimen.half_default)).toInt())

            frenchAnswer.layoutParams = gridParams
            frenchAnswer.text = answersMap.toList()[it].first
            frenchAnswer.gravity = Gravity.CENTER
            frenchAnswer.setTextColor(Color.BLACK)
            val redCornered = ResourcesCompat.getDrawable(holder.view.resources, R.drawable.ic_rounded_corners, null)?.mutate()
            redCornered?.setTint(Color.rgb(160, 60, 60))
            if(numberOfAnswers != 1) {
                frenchAnswer.background = redCornered
                frenchAnswer.setOnClickListener(View.OnClickListener {
                    val answerCurrentValue = answersMap[(it as TextView).text]
                    val drawable = ResourcesCompat.getDrawable(it.resources, R.drawable.ic_rounded_corners, null)?.mutate()
                    if(!answerCurrentValue!!) {
                        drawable?.setTint(Color.rgb(60, 160, 60))
                    } else {
                        drawable?.setTint(Color.rgb(160, 60, 60))
                    }
                    answersMap[it.text.toString()] = !answerCurrentValue
                    it.background = drawable

                    for(answer in answersMap.entries)
                    {
                        Timber.d("Answer [%s][%s]", answer.key, answer.value)
                    }

                    val answerSelected = answersMap[word.frenchWord]!!
                    val noOther = answersMap.entries.none { e -> e.key != word.frenchWord && e.value}
                    holder.rightAnswer = ( answerSelected && noOther )
                })
            }
            else { frenchAnswer.textSize = 48f }



            holder.layoutFrenchAnswers.addView(frenchAnswer)
        }
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val koreanWord : TextView = view.findViewById(R.id.lbl_korean_word)
        val layoutFrenchAnswers : GridLayout = view.findViewById(R.id.layout_french_answers)
        var rightAnswer = false
    }

    override fun onInit(status: Int) {
        if(status != TextToSpeech.ERROR) {
            tts.language = Locale.KOREA
        }
    }
}
