package com.example.androidmvi.View;

import com.example.androidmvi.Model.MainView;
import com.example.androidmvi.Model.PartialMainState;
import com.example.androidmvi.Utils.DataSource;
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;
import com.hannesdorfmann.mosby3.mvi.MviPresenter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends MviBasePresenter<MainView,MainViewState> {
    DataSource dataSource;

    public MainPresenter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void bindIntents() {
        Observable<PartialMainState> gotData= intent(MainView::getImageIntent).
                switchMap(index -> dataSource.getImageLinkFromList(index)
                .map(imageLink -> (PartialMainState) new PartialMainState.GotImageLink(imageLink))
                .startWith(new PartialMainState.Loading())
                .onErrorReturn(error -> new PartialMainState.Error(error))
                .subscribeOn(Schedulers.io()));

        MainViewState initState= new MainViewState(false,
                false,"",null);
        Observable<PartialMainState> initIntent= gotData.observeOn(AndroidSchedulers.mainThread());
        subscribeViewState(initIntent.scan(initState,this::viewStateReducer),MainView::render);
    }

    MainViewState viewStateReducer(MainViewState prevState, PartialMainState changedState){
        MainViewState newState= prevState;

        if (changedState instanceof PartialMainState.Loading)
        {
            newState.isLoading=true;
            newState.isImageViewShow=false;
        }

        if(changedState instanceof  PartialMainState.GotImageLink){
            newState.isLoading=false;
            newState.isImageViewShow=true;
            newState.imageLink= ((PartialMainState.GotImageLink) changedState).imageLink;

        }

        if (changedState instanceof PartialMainState.Error)
        {
            newState.isLoading=true;
            newState.isImageViewShow=false;
            newState.error=((PartialMainState.Error) changedState).error;
        }
        return newState;
    }
}
