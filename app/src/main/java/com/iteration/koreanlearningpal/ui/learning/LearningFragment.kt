package com.iteration.koreanlearningpal.ui.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iteration.koreanlearningpal.R
import com.iteration.koreanlearningpal.module.Word
import com.iteration.koreanlearningpal.ui.testing.TestingCardStackAdapter
import com.yuyakaido.android.cardstackview.*

class LearningFragment : Fragment(), CardStackListener {

    private lateinit var learningViewModel: LearningViewModel

    private lateinit var cardstackLearning : CardStackView
    private lateinit var manager : CardStackLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        learningViewModel = ViewModelProvider(this).get(LearningViewModel::class.java)
        val root = initViews(inflater, container)
        setObservers()
        return root
    }

    private fun setObservers() {
//        learningViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_learning, container, false)

        val listWords = listOf<Word>(
            Word("사랑해", "Je vous aime"),
            Word("KR 2", "FR 2"),
            Word("KR 3", "FR 3"),
            Word("KR 4", "FR 4"),
            Word("KR 5", "FR 5"),
            Word("KR 6", "FR 6"),
            Word("KR 7", "FR 7"),
            Word("KR 8", "FR 8"),
            Word("KR 9", "FR 9"),
            Word("KR 10", "FR 10"),
            Word("KR 11", "FR 11"),
            Word("KR 12", "FR 12"),
            Word("KR 13", "FR 13")
        )

        cardstackLearning = root.findViewById(R.id.cardstack_learning)
        cardstackLearning.adapter = TestingCardStackAdapter(listWords, 1)
        manager = CardStackLayoutManager(context, this)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(4.0f)
        cardstackLearning.layoutManager = manager


        return root
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardRewound() {
    }
}