package com.zhuoxin.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuoxin.R;
import com.zhuoxin.base.MyBaseAdapter;
import com.zhuoxin.entity.FileInfo;
import com.zhuoxin.utils.FileTypeUtil;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class FileAdapter extends MyBaseAdapter<FileInfo> {
    public FileAdapter(List dateList, Context context) {
        super(dateList, context);
    }

    public boolean isScroll = false;
    //获得运行时最大运存
    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    //定义LruCache最大缓存常量大小
    final int MAX_MEMORY = maxMemory / 8;
    //创建软引用的键值对(键为文件名,值为软引用Bitmap)
    //HashMap<String, SoftReference<Bitmap>> bitmapsoftMap = new HashMap<String, SoftReference<Bitmap>>();
    //创建一个LruCache（设置缓存大小10MB）{重写sizeOf方法，将我们自己的图片大小返回（长宽相乘）}
    LruCache<String, Bitmap> bitmapLruCache = new LruCache<String, Bitmap>(MAX_MEMORY) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getWidth();
        }
    };

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_file, null);
            holder = new ViewHolder();
            holder.cb_file = (CheckBox) view.findViewById(R.id.cb_file);
            holder.tv_fileName = (TextView) view.findViewById(R.id.tv_fileName);
            holder.tv_fileType = (TextView) view.findViewById(R.id.tv_fileType);
            holder.iv_fileIcon = (ImageView) view.findViewById(R.id.iv_fileIcon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //防止按钮错位
        holder.cb_file.setTag(i);
        holder.cb_file.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int index = (int) holder.cb_file.getTag();
                getItem(index).setSelect(b);
            }
        });
        holder.cb_file.setChecked(getItem(i).isSelect());
        holder.tv_fileName.setText(getItem(i).getFile().getName());
        holder.tv_fileType.setText(getItem(i).getFileType());
        if (!isScroll) {
            Bitmap bitmap = null;
            //创建一个软引用,数据从软引用键值对中获取(通过键(文件名)获取值)
        /*SoftReference<Bitmap> bitmapSoftReference = bitmapsoftMap.get(getItem(i).getFile().getName());
        //假如软引用为空
        if (bitmapSoftReference == null) {
            //通过强引用的方式先获取bitmap
            bitmap = getBitMap(getItem(i));
            //将获取的强引用bitmap设置到软引用中
            SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);
            //将软引用保存到创建的键值对中
            bitmapsoftMap.put(getItem(i).getFile().getName(), soft);
        } else {
            //如果不为空,直接获取
            bitmap = bitmapSoftReference.get();
        }
        holder.iv_fileIcon.setImageBitmap(bitmap);*/
            //通过键获得值(键为文件名)
            bitmap = bitmapLruCache.get(getItem(i).getFile().getName());
            //如果bitmap为空,获取bitmap
            if (bitmap == null) {
                bitmap = getBitMap(getItem(i));
                //保存键值对
                bitmapLruCache.put(getItem(i).getFile().getName(), bitmap);
            }
            holder.iv_fileIcon.setImageBitmap(bitmap);
        } else {
            holder.iv_fileIcon.setImageResource(R.drawable.item_arrow_right);
        }
        return view;
    }

    static class ViewHolder {
        TextView tv_fileName;
        CheckBox cb_file;
        TextView tv_fileType;
        ImageView iv_fileIcon;
    }

    private Bitmap getBitMap(FileInfo fileInfo) {
        Bitmap bitmap = null;
        if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_IMAGE)) {
            //在decode图片资源前,先获取设置图片的缩放率
            //1.实例化一个Options
            BitmapFactory.Options options = new BitmapFactory.Options();
            //2.只解析图片的边框,并将获取到的数据存到options中
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileInfo.getFile().getAbsolutePath(), options);
            //3.计算图片的缩放率,并进行设置
            //480*800屏幕分辨率手机图片显示大小(60像素)
            int scaleUnit = 60;
            //缩放率大小(解析出来的图片的边框数据大小/小图标边框大小)(像素)
            int scale = (options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / scaleUnit;
            //设置options的缩放率
            options.inSampleSize = scale;
            //4.根据设置好的options加载图片
            options.inJustDecodeBounds = false;
            //通过位图工厂解码文件(为获得图片缩略图)
            bitmap = BitmapFactory.decodeFile(fileInfo.getFile().getAbsolutePath(), options);
        } else if (fileInfo.getFileType().equals(FileTypeUtil.TYPE_AUDIO)) {
            bitmap = getMp3BitMap(getAlbumArt(getCursorResult(fileInfo.getFile().getAbsolutePath())));
        } else {
            int icon = context.getResources().getIdentifier(fileInfo.getIconName(), "drawable", context.getPackageName());
            if (icon <= 0) {
                icon = R.drawable.item_arrow_right;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), icon, options);
            int scaleUnit = 60;
            int scale = (options.outHeight > options.outWidth ? options.outHeight : options.outWidth) / scaleUnit;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(), icon, options);
        }
        return bitmap;
    }

    private Cursor getCursorResult(String path) {
        Cursor cursorResult = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursorResult.moveToNext()) {
            String cursorPath = cursorResult.getString(cursorResult.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            if (cursorPath.equals(path)) {
                break;
            }
        }
        return cursorResult;
    }

    private String getAlbumArt(Cursor cursorResult) {
        int album_id = cursorResult.getInt(cursorResult.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    private Bitmap getMp3BitMap(String album_art) {
        Bitmap bm = null;
        if (album_art == null) {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_audio);
        } else {
            bm = BitmapFactory.decodeFile(album_art);
        }
        return bm;
    }
}

