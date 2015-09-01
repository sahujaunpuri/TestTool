package amtt.epam.com.amtt.os;

import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;

import amtt.epam.com.amtt.datasource.DataSource;
import amtt.epam.com.amtt.processing.Processor;
import amtt.epam.com.amtt.common.Callback;

/**
 @author Iryna Monchanka
 @version on 28.08.2015
 */
public class Task<Params, DataSourceResult, ProcessingResult>{

    private final Callback<ProcessingResult> mCallback;
    private final DataSource<Params, DataSourceResult> mDataSource;
    private final Params mParams;
    private final Processor<DataSourceResult, ProcessingResult> mProcessor;
    private Exception mException;
    private ProcessingResult mProcessingResult;

    public Task(Params params, DataSource<Params, DataSourceResult> dataSource,
                Processor<DataSourceResult, ProcessingResult> processor, Callback<ProcessingResult> callback) {
        mDataSource = dataSource;
        mParams = params;
        mProcessor = processor;
        mCallback = callback;
    }

    public Task(Params params,DataSource<Params, DataSourceResult> dataSource, Callback<ProcessingResult> callback) {
        mParams = params;
        mDataSource = dataSource;
        mCallback = callback;
        mProcessor = null;
    }

    @SafeVarargs
    private final ProcessingResult doInBackground(Params... params) {
        ProcessingResult processingResult;
        try {
            DataSourceResult dataSourceResult = mDataSource.getData(mParams);
            if (mProcessor != null) {
                processingResult = mProcessor.process(dataSourceResult);
                return processingResult;
            } else {
                return (ProcessingResult) dataSourceResult;
            }
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }

    @SafeVarargs
    public final Task executeOnThreadExecutor(ExecutorService executor, final Params... params) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                if (mCallback != null) {
                    mCallback.onLoadStart();
                }
            }

            @Override
            protected Void doInBackground(Void... param) {
                try {
                    mProcessingResult = Task.this.doInBackground(params);
                } catch (final Exception e) {
                    mException = e;
                    mProcessingResult = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (mException != null) {
                    mCallback.onLoadError(mException);
                    return;
                }
                if (mCallback == null) {
                    return;
                }
                mCallback.onLoadExecuted(mProcessingResult);
            }
        }.executeOnExecutor(executor);

        return this;
    }
}
