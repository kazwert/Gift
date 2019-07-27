package com.icecoffee.gift

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.btnRandom
import kotlinx.android.synthetic.main.activity_main.imgRandom
import kotlinx.android.synthetic.main.activity_main.tvLabel
import kotlinx.android.synthetic.main.activity_main.tvSouvenirName
import kotlinx.android.synthetic.main.activity_main.viewShadow
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    /*** App for randomize gift, with stock for Kang Amin Wedding
    1.
     ***/
    private val souvenirs: MutableList<Souvenir> = mutableListOf()
    private val souvenirsStock: MutableList<Souvenir> = mutableListOf()
    private val randomizeImages: MutableList<Int> = mutableListOf()
    private var clickCounter: Int = 0
    private var disposable: Disposable? = null
    private var randomizeCounter = 0
    private var defaultImage: Int = R.drawable.ic_launcher_background

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLib()
        initUI()
        initAction()
        initProcess()
    }

    private fun initLib() {
        //add here your souvenir list
        souvenirs.add(Souvenir(R.drawable.a1, 3))
        souvenirs.add(Souvenir(R.drawable.a2, 3))
        souvenirs.add(Souvenir(R.drawable.a3, 3))
        //
        randomizeImages.addAll(souvenirs.map { it.image })
        souvenirsStock.addAll(souvenirs)
        // set this for default image
        defaultImage = R.drawable.thankyou

    }

    private fun initUI() {
        setDefault()
    }

    private fun initAction() {
        btnRandom.setOnClickListener {
            setDefault()
        }

        imgRandom.setOnClickListener {
            if (clickCounter == 1) {
                startRandom()
                clickCounter = 0
            } else {
                clickCounter++
            }
        }
    }

    private fun initProcess() {
    }

    private fun startRandom() {

        imgRandom.isClickable = false
        disposable = Observable.interval(30, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .repeat()
            .takeUntil { it == 50L }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.toInt() % 2 == 0) {
                    imgRandom.animate().scaleX(1f).scaleY(1f).setDuration(30).start()
                }
                else {
                    imgRandom.animate().scaleX(1.2f).scaleY(1.2f).setDuration(30).start()
                }

                // Choose random souvenir
                Log.d("TEST ", "" + it)
                when (it) {
                    50.toLong() -> getSouvenir()
                    else -> {
                        if (randomizeCounter == randomizeImages.size) {
                            randomizeCounter = 0
                            imgRandom.setImageResource(randomizeImages[randomizeCounter])
                        } else {
                            imgRandom.setImageResource(randomizeImages[randomizeCounter])
                            randomizeCounter++
                        }
                    }
                }
            }
    }

    private fun getSouvenir() {
        when (souvenirs.size) {
            in 2..souvenirs.size -> {
                val randomIndex = Random.nextInt(1, souvenirs.size) - 1
                with(souvenirs[randomIndex]) {
                    imgRandom.setImageResource(this.image)
                    tvSouvenirName.text = this.name
                    this.stock--
                    if (stock == 0) souvenirs.removeAt(randomIndex)
                }
            }
            1 -> {
                with(souvenirs[0]) {
                    imgRandom.setImageResource(this.image)
                    tvSouvenirName.text = this.name
                    this.stock--
                    if (stock == 0) souvenirs.removeAt(0)
                }
            }
            0 -> {
                imgRandom.setImageResource(defaultImage)
            }
        }
        showGetSouvenirBackground(true)
        imgRandom.animate().scaleX(1.3f).scaleY(1.3f).setDuration(500).start()
        imgRandom.isClickable = false
    }

    private fun setDefault() {
        imgRandom.setImageResource(defaultImage)
        imgRandom.animate().scaleX(1f).scaleY(1f).setDuration(500).start()
        imgRandom.isClickable = souvenirs.size > 0
        showGetSouvenirBackground(false)

    }

    private fun showGetSouvenirBackground(isShow: Boolean) {
        tvLabel.visibility = if (isShow) View.VISIBLE else View.GONE
        tvSouvenirName.visibility = if (isShow) View.VISIBLE else View.GONE
        viewShadow.visibility = if (isShow) View.VISIBLE else View.GONE
        btnRandom.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    data class Souvenir(
        var image: Int,
        var stock: Int,
        var name: String = "Souvenir"
    )
}
