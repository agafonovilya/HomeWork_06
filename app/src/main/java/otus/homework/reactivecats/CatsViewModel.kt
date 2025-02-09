package otus.homework.reactivecats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CatsViewModel(
    private val catsService: CatsService,
    private val localCatFactsGenerator: LocalCatFactsGenerator,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<Result>()
    val catsLiveData: LiveData<Result> = _catsLiveData

    private var catsServiceDisposable: Disposable? = null

    init {
        getFacts()
    }

    private fun getFacts() {
        catsServiceDisposable = Observable.interval(2, TimeUnit.SECONDS)
            .switchMapSingle {
                catsService.getCatFact()
                    .timeout(2, TimeUnit.SECONDS)
                    .map { response ->
                        val fact = response.body()
                        if (response.isSuccessful && fact != null) {
                            fact
                        } else {
                            throw NoCatsFactException(
                                response.errorBody()?.string() ?: resourceManager.getString(
                                    R.string.default_error_text
                                )
                            )
                        }
                    }
                    .onErrorResumeNext { _ -> localCatFactsGenerator.generateCatFact() }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { fact -> _catsLiveData.value = Result.Success(fact) },
                { th ->
                    if (th is NoCatsFactException) {
                        _catsLiveData.value = Result.Error(th.message.orEmpty())
                    } else {
                        _catsLiveData.value = Result.ServerError
                    }
                })
    }

    override fun onCleared() {
        super.onCleared()
        catsServiceDisposable?.dispose()
    }
}

class CatsViewModelFactory(
    private val catsRepository: CatsService,
    private val localCatFactsGenerator: LocalCatFactsGenerator,
    private val resourceManager: ResourceManager
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository, localCatFactsGenerator, resourceManager) as T
}

sealed class Result {
    data class Success(val fact: Fact) : Result()
    data class Error(val message: String) : Result()
    object ServerError : Result()
}