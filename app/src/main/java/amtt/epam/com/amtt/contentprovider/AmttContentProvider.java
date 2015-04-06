package amtt.epam.com.amtt.contentprovider;

import amtt.epam.com.amtt.database.DataBaseManager;
import amtt.epam.com.amtt.database.table.ActivityInfoTable;
import amtt.epam.com.amtt.database.table.StepsTable;
import amtt.epam.com.amtt.database.table.StepsWithMetaTable;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;

import java.util.*;


/**
 * Created by Artsiom_Kaliaha on 23.03.2015.
 */
public class AmttContentProvider extends ContentProvider {

    private static final String AUTHORITY = "amtt.epam.com.amtt.contentprovider";

    public static final Uri ACTIVITY_META_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ActivityInfoTable.TABLE_NAME);
    public static final Uri STEP_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + StepsTable.TABLE_NAME);
    public static final Uri STEP_WITH_META_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + StepsWithMetaTable.TABLE_NAME);

    public static final String ACTIVITY_META_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + ActivityInfoTable.TABLE_NAME;
    public static final String ACTIVITY_META_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + ActivityInfoTable.TABLE_NAME;
    public static final String STEP_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + StepsTable.TABLE_NAME;
    public static final String STEP_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + StepsTable.TABLE_NAME;
    public static final String STEP_WITH_META_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + StepsWithMetaTable.TABLE_NAME;
    public static final String STEP_WITH_META_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + StepsWithMetaTable.TABLE_NAME;

    private static final UriMatcher sUriMatcher;
    private static Map<Integer, String> sContentType;
    private static Map<Integer, String[]> sProjections;
    private static DataBaseManager mDataBaseManager;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, ActivityInfoTable.TABLE_NAME, AmttUri.ACTIVITY_META.ordinal());
        sUriMatcher.addURI(AUTHORITY, ActivityInfoTable.TABLE_NAME + "/#", AmttUri.ACTIVITY_META_BY_NAME.ordinal());
        sUriMatcher.addURI(AUTHORITY, StepsTable.TABLE_NAME, AmttUri.STEP.ordinal());
        sUriMatcher.addURI(AUTHORITY, StepsTable.TABLE_NAME + "/#", AmttUri.STEP_ID.ordinal());
        sUriMatcher.addURI(AUTHORITY, StepsWithMetaTable.TABLE_NAME, AmttUri.STEP_WITH_META.ordinal());
        sUriMatcher.addURI(AUTHORITY, StepsWithMetaTable.TABLE_NAME + "/#", AmttUri.STEP_WITH_META_ID.ordinal());

        sContentType = new HashMap<>();
        sContentType.put(AmttUri.ACTIVITY_META.ordinal(), ACTIVITY_META_CONTENT_TYPE);
        sContentType.put(AmttUri.ACTIVITY_META_BY_NAME.ordinal(), ACTIVITY_META_CONTENT_ITEM_TYPE);
        sContentType.put(AmttUri.STEP.ordinal(), STEP_CONTENT_TYPE);
        sContentType.put(AmttUri.STEP_ID.ordinal(), STEP_CONTENT_ITEM_TYPE);
        sContentType.put(AmttUri.STEP_WITH_META.ordinal(), STEP_WITH_META_CONTENT_TYPE);
        sContentType.put(AmttUri.STEP_WITH_META_ID.ordinal(), STEP_WITH_META_CONTENT_ITEM_TYPE);

        sProjections = new HashMap<>();
        sProjections.put(AmttUri.ACTIVITY_META.ordinal(), ActivityInfoTable.PROJECTION);
        sProjections.put(AmttUri.ACTIVITY_META_BY_NAME.ordinal(), ActivityInfoTable.PROJECTION);
        sProjections.put(AmttUri.STEP.ordinal(), StepsTable.PROJECTION);
        sProjections.put(AmttUri.STEP_ID.ordinal(), StepsTable.PROJECTION);
        sProjections.put(AmttUri.STEP_WITH_META.ordinal(), StepsWithMetaTable.PROJECTION);
        sProjections.put(AmttUri.STEP_WITH_META_ID.ordinal(), StepsWithMetaTable.PROJECTION);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int matchedUri = sUriMatcher.match(uri);
        if (!isProjectionCorrect(matchedUri, projection)) {
            throw new IllegalArgumentException("Incorrect projection column(s)");
        }

        Cursor cursor;
        if (matchedUri == AmttUri.STEP_WITH_META.ordinal() || matchedUri == AmttUri.STEP_WITH_META_ID.ordinal()) {
            String[] tablesName = {StepsTable.TABLE_NAME, ActivityInfoTable.TABLE_NAME};
            cursor = getDataBaseManager().joinQuery(tablesName,
                StepsWithMetaTable.PROJECTION,
                new String[]{StepsTable._ASSOCIATED_ACTIVITY, ActivityInfoTable._ACTIVITY_NAME});
        } else {
            String tableName = uri.getLastPathSegment();
            cursor = getDataBaseManager().query(tableName, projection, selection, selectionArgs, sortOrder);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);
        return sContentType.get(uriType);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = uri.getLastPathSegment();
        long id = getDataBaseManager().insert(tableName, values);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = uri.getPathSegments().size() > 1 ? uri.getLastPathSegment() : uri.getPathSegments().get(0);
        int deletedRows = getDataBaseManager().delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = uri.getPathSegments().size() > 1 ? uri.getLastPathSegment() : uri.getPathSegments().get(0);
        int updatedRows = getDataBaseManager().update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    private static boolean isProjectionCorrect(int uriType, String[] projection) {
        String[] existingColumns = sProjections.get(uriType);
        Set<String> availableProjection;
        Set<String> receivedProjection = new HashSet<>(Arrays.asList(projection));
        if (existingColumns != null) {
            availableProjection = new HashSet<>(Arrays.asList(existingColumns));
        } else {
            availableProjection = Collections.EMPTY_SET;
        }
        return availableProjection.containsAll(receivedProjection);
    }

    private DataBaseManager getDataBaseManager() {
        if (mDataBaseManager == null) {
            mDataBaseManager = new DataBaseManager(getContext());
        }
        return mDataBaseManager;
    }

}
