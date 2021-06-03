package com.iteration.koreanlearningpal.ui.testing

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iteration.koreanlearningpal.R
import com.iteration.koreanlearningpal.module.Word
import com.yuyakaido.android.cardstackview.*
import timber.log.Timber
import java.lang.Integer.min

class TestingFragment : Fragment(), CardStackListener {

    private lateinit var testingViewModel: TestingViewModel

    private lateinit var cardstackTraining : CardStackView
    private lateinit var manager : CardStackLayoutManager
    private lateinit var swipedCard : TestingCardStackAdapter.ViewHolder
    private var swipedDirection : Direction? = null
//    private var rewindCount = 0

    private lateinit var fabSkip : FloatingActionButton
    private lateinit var fabTurnback : FloatingActionButton
    private lateinit var fabValidate : FloatingActionButton

    private lateinit var lblValidScore : TextView
    private lateinit var lblInvalidScore : TextView




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        testingViewModel = ViewModelProvider(this).get(TestingViewModel::class.java)
        val root = initViews(inflater, container)
        setObservers()
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun setObservers() {
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        testingViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
        testingViewModel.validScore.observe(viewLifecycleOwner, Observer { lblValidScore.text = "${resources.getText(R.string.valid_score)} $it" })
        testingViewModel.invalidScore.observe(viewLifecycleOwner, Observer { lblInvalidScore.text = "${resources.getText(R.string.invalid_score)} $it" })
        testingViewModel.rewindCount.observe(viewLifecycleOwner, Observer { fabTurnback.isClickable = it != 0 })
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val root = inflater.inflate(R.layout.fragment_testing, container, false)

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
        ).shuffled()


        lblValidScore = root.findViewById(R.id.lbl_testing_valid)
        lblInvalidScore = root.findViewById(R.id.lbl_testing_invalid)

        cardstackTraining = root.findViewById(R.id.cardstack_testing)
        cardstackTraining.adapter = TestingCardStackAdapter(listWords, 4)
        manager = CardStackLayoutManager(context, this)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(4.0f)
        cardstackTraining.layoutManager = manager

        fabSkip = root.findViewById(R.id.fab_testing_skip)
        fabTurnback = root.findViewById(R.id.fab_testing_turnback)
        fabValidate = root.findViewById(R.id.fab_testing_validate)

        fabTurnback.setOnClickListener {
            if(testingViewModel.rewindCount.value!! > 0) {
                testingViewModel.rewindCount.value = testingViewModel.rewindCount.value?.minus(1)
                val setting = RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(DecelerateInterpolator())
                        .build()
                manager.setRewindAnimationSetting(setting)
                cardstackTraining.rewind()
            }
        }
        fabSkip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardstackTraining.swipe()
        }
        fabValidate.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardstackTraining.swipe()
        }
        return root
    }



    override fun onCardDisappeared(view: View?, position: Int) {
        swipedCard = cardstackTraining.findContainingViewHolder(manager.topView) as TestingCardStackAdapter.ViewHolder
    }
    override fun onCardDragging(direction: Direction?, ratio: Float) { }
    override fun onCardCanceled() { }
    override fun onCardAppeared(view: View?, position: Int) {
    }
    override fun onCardRewound() {
        val rewoundCard = cardstackTraining.findContainingViewHolder(manager.topView) as TestingCardStackAdapter.ViewHolder
        Timber.d("${rewoundCard.koreanWord.text} rewound! This card was correct: ${rewoundCard.rightAnswer}" )
        if(swipedDirection == Direction.Right)
        {
            if(rewoundCard.rightAnswer) {   testingViewModel.validScore.value = testingViewModel.validScore.value?.minus(1)   }
            else                        { testingViewModel.invalidScore.value = testingViewModel.invalidScore.value?.minus(1) }
        }
    }

    override fun onCardSwiped(direction: Direction?) {
        Timber.d("${swipedCard.koreanWord.text} dragged to ${direction}. This card is correct: ${swipedCard.rightAnswer}" )
        swipedDirection = direction
        when(direction)
        {
            Direction.Left -> {
                Timber.d("LEFT")
                // Ajout à la prochaine stack
            } // Carte passée, la remettre dans la stack
            Direction.Right -> {
                Timber.d("RIGHT")
                if(swipedCard.rightAnswer) {   testingViewModel.validScore.value = testingViewModel.validScore.value?.plus(1)   }
                else                       { testingViewModel.invalidScore.value = testingViewModel.invalidScore.value?.plus(1) }
            } // Carte validée, check si le résultat est bon
            else -> {}
        }
        testingViewModel.rewindCount.value = min(testingViewModel.rewindCount.value?.plus(1)!!, 1)
    }
}