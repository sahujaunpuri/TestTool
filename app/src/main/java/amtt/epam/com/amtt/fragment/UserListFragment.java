package amtt.epam.com.amtt.fragment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.adapter.UserAdapter;
import amtt.epam.com.amtt.contentprovider.AmttContentProvider;
import amtt.epam.com.amtt.database.table.UsersTable;
import amtt.epam.com.amtt.service.TopButtonService;

/**
 * Created by Artsiom_Kaliaha on 30.04.2015.
 */
public class UserListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static interface ListFragmentUserCallback {
        void onListItemClick(long id);
    }

    private UserAdapter mAdapter;

    public UserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_qas, container, false);
        initViews(layout);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        return layout;
    }

    private void initViews(View layout) {
        ListView listView = (ListView) layout.findViewById(android.R.id.list);
        mAdapter = new UserAdapter(getActivity(), null, NO_FLAGS);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListFragmentUserCallback) getActivity()).onListItemClick(id);
                TopButtonService.authSuccess(getActivity());
            }
        });
        mProgressBar = (ProgressBar) layout.findViewById(android.R.id.progress);
    }

    //Callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        setProgressVisibility(View.VISIBLE);
        return new CursorLoader(getActivity(), AmttContentProvider.USER_CONTENT_URI, UsersTable.PROJECTION, null, null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setProgressVisibility(View.GONE);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}