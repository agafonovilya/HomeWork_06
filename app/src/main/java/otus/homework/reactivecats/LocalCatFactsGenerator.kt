package otus.homework.reactivecats

import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class LocalCatFactsGenerator(
    private val resourceManager: ResourceManager
) {

    /**
     * Реализуйте функцию otus.homework.reactivecats.LocalCatFactsGenerator#generateCatFact так,
     * чтобы она возвращала Fact со случайной строкой  из массива строк R.array.local_cat_facts
     * обернутую в подходящий стрим(Flowable/Single/Observable и т.п)
     */
    fun generateCatFact(): Single<Fact> {
        return Single.just(getRandomFact())
    }

    /**
     * Реализуйте функцию otus.homework.reactivecats.LocalCatFactsGenerator#generateCatFactPeriodically так,
     * чтобы она эмитила Fact со случайной строкой из массива строк R.array.local_cat_facts каждые 2000 миллисекунд.
     * Если вновь заэмиченный Fact совпадает с предыдущим - пропускаем элемент.
     */
    fun generateCatFactPeriodically(): Flowable<Fact> {
        return Flowable
            .interval(2000, TimeUnit.MILLISECONDS)
            .map { getRandomFact() }
            .distinctUntilChanged()
    }

    private fun getRandomFact(): Fact {
        return resourceManager.getStringArray(R.array.local_cat_facts).let {
            Fact(it[Random.nextInt(it.lastIndex)])
        }
    }
}