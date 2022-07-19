package com.example.lolwebcrawlerdice

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart: Button = findViewById(R.id.btnStart)
        val listView: RecyclerView = findViewById(R.id.listView)
        btnStart.setOnClickListener {
            listView.layoutManager = LinearLayoutManager(this)
            doTask("https://www.leagueoflegends.com/ko-kr/champions")
        }


        val rollButton: Button = findViewById(R.id.rollButton)
        rollButton.setOnClickListener {
            rollDice()
        }
    }


    private fun doTask (url : String) {
        var documentTitle: String = ""
        var itemList: ArrayList<ChampItem> = arrayListOf()

        Single.fromCallable{
            try {
                // 사이트에 접속해서 HTML 문서 가져오기
                val doc = Jsoup.connect(url).get()

                val elements : Elements = doc.select("div.class")
                run elemLoop@{
                    elements.forEachIndexed{ index, elem ->
                        var portrait =
                            elem.select("span.data-testid=\"list-0:image\" class=\"style__ImageContainer-n3ovyt-1 ajxce\"").text()
                        var name =
                            elem.select("span.class=\"style__Name-n3ovyt-2 cMGedC\"").attr("src")

                        var item = ChampItem(name, portrait)
                        itemList.add(item)
                    }
                }
                documentTitle = doc.title()
            } catch (e: Exception) {e.printStackTrace()}
            return@fromCallable documentTitle
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                // documentTitle 응답 성공 시
                { text ->
                    // 리사이클러뷰에 아이템 연결
                    listView.adapter = ChampAdapter(itemList)
                },
                // documentTitle 응답 오류 시
                { it.printStackTrace() })



    }

    private fun rollDice() {
        val drawableResource = when (Random().nextInt(6)){
            1 -> R.drawable.garen
            2 -> R.drawable.akali
            3 -> R.drawable.azir
            4 -> R.drawable.kayn
            5 -> R.drawable.viktor
            else -> R.drawable.kindred
        }
        championPortrait.setImageResource(drawableResource)
    }
}