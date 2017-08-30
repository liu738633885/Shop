package com.yizhen.shop.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.yizhen.shop.MainApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Bitmap操作常用工具类
 * Created by Clock on 2015/12/31.
 */
public class BitmapUtils {
    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment
            .MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    public static final String SAVE_IMG = "/savePic";//保存的确切位置
    //public static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/"+ MainApplication.getInstance().getPackageName()+"/savePic";//保存的确切位置
    public static final String SAVE_REAL_PATH = MainApplication.getInstance().getCacheDir() + SAVE_IMG;//保存的确切位置
    //public static final String SAVE_REAL_PATH = FileUtil.getRootPath().getAbsolutePath() + File.separator + "OA_PIC" + SAVE_IMG;//保存的确切位置

    public static String getSaveRealPath() {
        File dirFile = new File(BitmapUtils.SAVE_REAL_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
            Logger.d("创建目录成功" + BitmapUtils.SAVE_REAL_PATH);
        }
        return SAVE_REAL_PATH;
    }

    private final static String TAG = BitmapUtils.class.getCanonicalName();
    public final static String JPG_SUFFIX = ".jpg";
    private final static String TIME_FORMAT = "yyyyMMddHHmmss";

    /**
     * 显示图片到相册
     *
     * @param context
     * @param photoFile 要保存的图片文件
     */
    public static void displayToGallery(Context context, File photoFile) {
        if (photoFile == null || !photoFile.exists()) {
            return;
        }
        String photoPath = photoFile.getAbsolutePath();
        String photoName = photoFile.getName();
        // 其次把文件插入到系统图库
        try {
            ContentResolver contentResolver = context.getContentResolver();
            MediaStore.Images.Media.insertImage(contentResolver, photoPath, photoName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoPath)));
    }

    /**
     * 将Bitmap保存到指定目录下
     *
     * @param bitmap
     * @param folder
     * @return 保存成功，返回其对应的File，保存失败则返回null
     */
    public static File saveToFile(Bitmap bitmap, File folder) {
        String fileName = new SimpleDateFormat(TIME_FORMAT).format(new Date());//直接以当前时间戳作为文件名
        return saveToFile(bitmap, folder, fileName);
    }

    /**
     * 将Bitmap保存到指定目录下，并且指定好文件名
     *
     * @param bitmap
     * @param folder
     * @param fileName 指定的文件名包含后缀
     * @return 保存成功，返回其对应的File，保存失败则返回null
     */
    public static File saveToFile(Bitmap bitmap, File folder, String fileName) {
        if (bitmap != null) {
            if (!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(folder, fileName + JPG_SUFFIX);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 压缩Bitmap的大小
     *
     * @param imageFile     图片文件
     * @param requestWidth  压缩到想要的宽度
     * @param requestHeight 压缩到想要的高度
     * @return
     */
    public static Bitmap decodeBitmapFromFile(File imageFile, int requestWidth, int requestHeight) {
        if (imageFile != null) {
            return decodeBitmapFromFile(imageFile.getAbsolutePath(), requestWidth, requestHeight);
        } else {
            return null;
        }
    }

    /**
     * 压缩Bitmap的大小
     *
     * @param imagePath     图片文件路径
     * @param requestWidth  压缩到想要的宽度
     * @param requestHeight 压缩到想要的高度
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String imagePath, int requestWidth, int requestHeight) {
        if (!TextUtils.isEmpty(imagePath)) {
            Log.i(TAG, "requestWidth: " + requestWidth);
            Log.i(TAG, "requestHeight: " + requestHeight);
            if (requestWidth <= 0 || requestHeight <= 0) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                return bitmap;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//不加载图片到内存，仅获得图片宽高
            BitmapFactory.decodeFile(imagePath, options);
            Log.i(TAG, "original height: " + options.outHeight);
            Log.i(TAG, "original width: " + options.outWidth);
            if (options.outHeight == -1 || options.outWidth == -1) {
                try {
                    ExifInterface exifInterface = new ExifInterface(imagePath);
                    int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                    int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度
                    Log.i(TAG, "exif height: " + height);
                    Log.i(TAG, "exif width: " + width);
                    options.outWidth = width;
                    options.outHeight = height;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight); //计算获取新的采样率
            Log.i(TAG, "inSampleSize: " + options.inSampleSize);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imagePath, options);

        } else {
            return null;
        }
    }

    /**
     * Decode and sample down a bitmap from resources to the requested width and height.
     *
     * @param res       The resources object containing the image data
     * @param resId     The resource id of the image data
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // BEGIN_INCLUDE (read_bitmap_dimensions)
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // END_INCLUDE (read_bitmap_dimensions)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     *
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth       The requested width of the resulting bitmap
     * @param reqHeight      The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     * that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * Google官方代码，计算合适的采样率
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }

    /*以下是自己写的一些方法*/

    /**
     * @param path
     * @param width
     * @param height 上传图片时判断图片的分辨率,然后进行压缩得到 path
     * @return
     */
    public static String compressImg(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);// 此时返回bm为空
        boolean isRotate = false;
        int rotate = readPictureDegree(path);
        Logger.d("旋转角度" + rotate);
        if (rotate == 90 || rotate == 270) {
            //是否为横向图片
            isRotate = true;
        }
        int inSampleSize = Math.round(calculateInSampleSize(options.outWidth, options.outHeight, width, height, isRotate));
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        if (rotate != 0) {
            Logger.d("开始旋转" + rotate);
            bitmap = rotateBitmap(bitmap, rotate);
        }
        String newPath = saveToFile(bitmap, path);
        Logger.d("存储之后的 path" + newPath);
        bitmap.recycle();
        bitmap = null;
        return newPath;
    }

    private static float calculateInSampleSize(int w, int h, int ww, int hh, boolean isRotate) {
        float ss = 1f;
        if (isRotate) {
            int temp = ww;
            ww = hh;
            hh = temp;
        }
        if (h > hh || w > ww) {
            float heightRatio = (float) h
                    / (float) hh;
            float widthRatio = (float) w / (float) ww;
            ss = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return ss;
    }

    /**
     * 判断照片角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            //ExifInterface获取图片信息的类
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            // 根据旋转角度，生成旋转矩阵
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * @param filePath 保存bitmap图片
     * @return
     */
    public static String saveToFile(Bitmap bitmap, String filePath) {
        Logger.d("存储之前的 path" + filePath);
        String newFilePath = "";
        try {
            String[] strings = filePath.split("/");
            if (!TextUtils.isEmpty(strings[strings.length - 1])) {
                Logger.d("分割 path" + strings[strings.length - 1]);
                getSaveRealPath();
                newFilePath = getSaveRealPath() + "/" + strings[strings.length - 1];
                Logger.d("拼接 path" + newFilePath);
            } else {
                newFilePath = filePath + "temp";
            }
        } catch (Exception e) {
            e.printStackTrace();
            newFilePath = filePath + "temp";
        }
        try {
            File file = new File(newFilePath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return newFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

}
