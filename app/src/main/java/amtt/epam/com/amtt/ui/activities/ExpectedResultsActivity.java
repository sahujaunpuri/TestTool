package amtt.epam.com.amtt.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.adapter.ExpectedResultsAdapter;
import amtt.epam.com.amtt.api.GetContentCallback;
import amtt.epam.com.amtt.googleapi.api.loadcontent.GSpreadsheetContent;
import amtt.epam.com.amtt.googleapi.bo.GEntryWorksheet;
import amtt.epam.com.amtt.googleapi.bo.GTag;
import amtt.epam.com.amtt.googleapi.bo.GWorksheet;
import amtt.epam.com.amtt.topbutton.service.TopButtonService;
import amtt.epam.com.amtt.util.Logger;

/**
 * @author Iryna Monchanka
 * @version on 03.06.2015
 */

public class ExpectedResultsActivity extends BaseActivity implements ExpectedResultsAdapter.ViewHolder.ClickListener {

    //region Variables
    private static final String TAG = ExpectedResultsActivity.class.getSimpleName();
    private ExpectedResultsAdapter mResultsAdapter;
    private RecyclerView mRecyclerView;
    private MultiAutoCompleteTextView mTagsAutocompleteTextView;
    private ArrayAdapter adapter;

    private Boolean mIsShowDetail = false;
    //endregion

    @Override
    public void onShowCard(int position) {
        Intent detail = new Intent(ExpectedResultsActivity.this, DetailActivity.class);
        GSpreadsheetContent.getInstance().setLastTestcaseId(mResultsAdapter.getIdTestcaseList().get(position));
        mIsShowDetail = true;
        startActivity(detail);
        finish();
    }

    @Override
    public void onShowCreationTicket(int position) {
        Intent creationTicket = new Intent(ExpectedResultsActivity.this, CreateIssueActivity.class);
        GSpreadsheetContent.getInstance().setLastTestcaseId(mResultsAdapter.getIdTestcaseList().get(position));
        startActivity(creationTicket);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expected_results);
        TopButtonService.sendActionChangeTopButtonVisibility(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        initViews();
        refreshSteps();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mIsShowDetail) {
            TopButtonService.sendActionChangeTopButtonVisibility(true);
        }
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExpectedResultsActivity.this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        initTagsAutocompleteTextView();
    }

    private void initTagsAutocompleteTextView() {
        mTagsAutocompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.tv_tags);
        GSpreadsheetContent.getInstance().getAllTags(new GetContentCallback<List<GTag>>() {
            @Override
            public void resultOfDataLoading(final List<GTag> result) {
                if (result != null && !result.isEmpty()) {
                    ExpectedResultsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            ArrayList<String> tagsNames = new ArrayList<>();
                            for (int i = 0; i < result.size(); i++) {
                                tagsNames.add(result.get(i).getName());
                            }
                            adapter = new ArrayAdapter<>(ExpectedResultsActivity.this, R.layout.spinner_dropdown_item, tagsNames);
                            mTagsAutocompleteTextView.setAdapter(adapter);
                            mTagsAutocompleteTextView.setThreshold(3);
                            mTagsAutocompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                        }
                    });
                }
            }
        });
    }

    private void refreshSteps() {
        showProgress(true);
        GSpreadsheetContent.getInstance().getAllTestCases(new GetContentCallback<List<GEntryWorksheet>>() {
            @Override
            public void resultOfDataLoading(final List<GEntryWorksheet> result) {
                if (result != null && !result.isEmpty()) {
                    ExpectedResultsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mResultsAdapter = new ExpectedResultsAdapter(result, R.layout.adapter_expected_results, ExpectedResultsActivity.this);
                            mRecyclerView.setAdapter(mResultsAdapter);
                            showProgress(false);
                        }
                    });
                } else {
                    Logger.d(TAG, "List TestCases = null");
                    showProgress(false);
                }
            }
        });
    }

}
