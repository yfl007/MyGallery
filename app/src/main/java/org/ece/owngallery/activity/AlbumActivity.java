package org.ece.owngallery.activity;

import java.io.File;
import java.util.ArrayList;

import org.ece.owngallery.ApplicationOwnGallery;
import org.ece.owngallery.R;
import org.ece.owngallery.adapter.BaseFragmentAdapter;
import org.ece.owngallery.component.PhoneMediaControl;
import org.ece.owngallery.component.PhoneMediaControl.PhotoEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AlbumActivity extends Activity {

	public static final String PACKAGE = "org.ece.owngallery";
	private static final String TAG = "AlbumActivity";
	private Toolbar toolbar;
	private GridView mView;
	private Context mContext;

	public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	public static ArrayList<PhotoEntry> mPhotos = new ArrayList<PhotoEntry>();

	private int itemWidth = 100;
	private ListAdapter mAdapter;
	private int AlbummID=0;
	public static  boolean mActionMultichoice;
	private MenuInflater mMenuInflater;
	private ModeCallback mCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		
		 mContext=AlbumActivity.this;
		 initializeActionBar();
		 initializeView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenuInflater =getMenuInflater();
		mMenuInflater.inflate(R.menu.main,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_multichoice:
//			onBackPressed();
			Log.d(TAG,"onOptionsItemSelected:action_multichoice");
			mActionMultichoice = true;
			mView.setItemChecked(0,true);
			mView.clearChoices();
			mCallback.updateSeletedCount();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initializeActionBar() {
		
		Bundle mBundle=getIntent().getExtras();
		String nameAlbum = mBundle.getString("Key_Name");
		AlbummID =Integer.parseInt(mBundle.getString("Key_ID")) ;
		albumsSorted=GalleryFragment.albumsSorted;
		
		mPhotos =albumsSorted.get(AlbummID).photos;
		
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(nameAlbum+" ("+ mPhotos.size()+")");
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void initializeView(){
		mView=(GridView)findViewById(R.id.grid_view);
		mView.setAdapter(mAdapter = new ListAdapter(AlbumActivity.this));

        int position = mView.getFirstVisiblePosition();
        int columnsCount = 2;
        mView.setNumColumns(columnsCount);
        itemWidth = (ApplicationOwnGallery.displaySize.x - ((columnsCount + 1) * ApplicationOwnGallery.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

		mAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            	Intent mIntent=new Intent(AlbumActivity.this,PhotoPreviewActivity.class);
            	Bundle mBundle=new Bundle();
            	mBundle.putInt("Key_FolderID", AlbummID);
            	mBundle.putInt("Key_ID", position);
            	mIntent.putExtras(mBundle);
            	startActivity(mIntent);
            }
        });

		//For mark option
		mCallback = new ModeCallback();
		mView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mView.setMultiChoiceModeListener(mCallback);

		LoadAllAlbum();
	}
 
	private void LoadAllAlbum(){
		if (mView != null && mView.getEmptyView() == null) {
			mView.setEmptyView(null);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
	}
	

	private class ListAdapter extends BaseFragmentAdapter {
		private Context mContext;
		private LayoutInflater layoutInflater;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public ListAdapter(Context context) {
			this.mContext = context;
			this.layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.nophotos)
					.showImageForEmptyUri(R.drawable.nophotos)
					.showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
					.cacheOnDisc(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));

		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int i) {
			return true;
		}

		@Override
		public int getCount() {
			return mPhotos != null ? mPhotos.size() : 0;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			viewHolder mHolder;
			if (view == null) {
				mHolder = new viewHolder();
				view = layoutInflater.inflate(R.layout.album_image, viewGroup,false);
				mHolder.imageView = (ImageView) view.findViewById(R.id.album_image);
				mHolder.checkedImage = (ImageView)view.findViewById(R.id.checked);
				ViewGroup.LayoutParams params = view.getLayoutParams();
				params.width = itemWidth;
				params.height = itemWidth;
				view.setLayoutParams(params);
				mHolder.imageView.setTag(i);
				
				view.setTag(mHolder);
			} else {
				mHolder = (viewHolder) view.getTag();
			}
			PhotoEntry mPhotoEntry = mPhotos.get(i);
			String path = mPhotoEntry.path;
			if (path != null && !path.equals("")) {
				ImageLoader.getInstance().displayImage("file://" + path, mHolder.imageView);
			}
			if (mView.isItemChecked(i)){
				mHolder.checkedImage.setVisibility(View.VISIBLE);
			}else {
				mHolder.checkedImage.setVisibility(View.GONE);
			}
			return view;
		}

		@Override
		public int getItemViewType(int i) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEmpty() {
			return albumsSorted == null || albumsSorted.isEmpty();
		}

		class viewHolder {
			public ImageView imageView;
			public ImageView checkedImage;
		}

	}

	public class ModeCallback implements GridView.MultiChoiceModeListener{
		private View mMultiSelectActionBarView;
		private TextView mSelectedCount;
		private ArrayList<String> mSelectedIndex = new ArrayList<String>();
		@Override
		public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
			Log.d(TAG,"pos=="+i+",id=="+l+",checked=="+b);
			updateSeletedCount();
			actionMode.invalidate();
			mAdapter.notifyDataSetChanged();
			if (mActionMultichoice){
				mActionMultichoice = false;
				return;
			}
			if (b){
				if (!mSelectedIndex.contains(""+i)){
					mSelectedIndex.add(""+i);
				}
			}else {
				mSelectedIndex.remove(""+i);
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// actionmode的菜单处理
			MenuInflater inflater = mMenuInflater;
			inflater.inflate(R.menu.multi_select_menu, menu);
			if (mMultiSelectActionBarView == null) {
				mMultiSelectActionBarView = LayoutInflater.from(mContext)
						.inflate(R.layout.list_multi_select_actionbar, null);

				mSelectedCount =
						(TextView)mMultiSelectActionBarView.findViewById(R.id.selected_conv_count);
			}
			mode.setCustomView(mMultiSelectActionBarView);
			((TextView)mMultiSelectActionBarView.findViewById(R.id.title)).setText(R.string.select_item);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
			if (mMultiSelectActionBarView == null) {
				ViewGroup v = (ViewGroup)LayoutInflater.from(mContext)
						.inflate(R.layout.list_multi_select_actionbar, null);
				actionMode.setCustomView(v);
				mSelectedCount = (TextView)v.findViewById(R.id.selected_conv_count);
			}
			//更新菜单的状态
			MenuItem selectItem = menu.findItem(R.id.action_select);
			if(mView.getCheckedItemCount() == mAdapter.getCount()){
				selectItem.setTitle(R.string.action_deselect_all);
			}else{
				selectItem.setTitle(R.string.action_select_all);
			}
			MenuItem deleteItem = menu.findItem(R.id.action_delete);
//			deleteItem.setTitle(R.)
			if (mView.getCheckedItemCount()!=0){
				deleteItem.setVisible(true);
			}else {
				deleteItem.setVisible(true);
			}
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
			switch (menuItem.getItemId()) {
				case R.id.action_select:
					if(mView.getCheckedItemCount() == mAdapter.getCount()){
						unSelectedAll();
					}else{
						selectedAll();
					}
					mAdapter.notifyDataSetChanged();
					break;
				case R.id.action_delete:
					deleteSelectedItem();
					break;
				default:
					break;
			}
			return true;
		}

		private void deleteSelectedItem() {
			new AlertDialog.Builder(AlbumActivity.this)
					.setTitle(R.string.image_manipulation)
					.setMessage(R.string.delete_confirm)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									boolean hasImgDeleted = false;
									for (int i=0;i<mSelectedIndex.size();i++){
										int index = Integer.parseInt(mSelectedIndex.get(i));
										PhotoEntry mPhotoEntry = mPhotos.get(index);
										String path = mPhotoEntry.path;
										File removedFile = new File(path);
										if (removedFile.exists()){
											removedFile.delete();
											hasImgDeleted = true;
											Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
											Uri contentUri = Uri.fromFile(removedFile);
											media.setData(contentUri);
											AlbumActivity.this.sendBroadcast(media);
											mPhotos.remove(mPhotoEntry);
										}
									}
									if (hasImgDeleted){
										mAdapter.notifyDataSetChanged();
										finish();
//										Intent intent = new Intent(AlbumActivity.this, AlbumActivity.class);
										startActivity(getIntent());
									}
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									;
								}
							}
					).show();
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
			mView.clearChoices();
		}
		public void updateSeletedCount(){
			mSelectedCount.setText(Integer.toString(mView.getCheckedItemCount()));
		}
		public void selectedAll(){
			for(int i= 0; i< mAdapter.getCount(); i++){
				mView.setItemChecked(i, true);
			}
			updateSeletedCount();
		}

		public void unSelectedAll(){
			mView.clearChoices();
			mView.setItemChecked(0,false);
			mSelectedIndex.clear();
			updateSeletedCount();
		}
	}
}
