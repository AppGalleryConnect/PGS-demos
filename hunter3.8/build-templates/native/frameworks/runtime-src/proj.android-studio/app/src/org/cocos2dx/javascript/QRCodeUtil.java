package org.cocos2dx.javascript;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cocos2dx.lib.Cocos2dxHelper.getPackageName;

public class QRCodeUtil {
    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content,
                    BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

//            try {
//                String dir = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡;
//
//                dir = "/storage/emulated/0/Android/data/org.cocos2dx.demo/files/file/";
////                dir = getExternalFilesDir("file").getAbsolutePath();
//                File dirHandle = new File(dir);
//                if (!dirHandle.exists()) dirHandle.mkdirs();
//
//
//                Log.d("xxx", "#### 路径:" + dir);
//                File file = new File(dir, "test.jpg");
//                file.createNewFile();
//                FileOutputStream out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                out.flush();
//                out.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                boolean ret =bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

                String dest = "";
                if (result!=null) {
                    Pattern p = Pattern.compile("\t|\r|\n");
                    Matcher m = p.matcher(result);
                    dest = m.replaceAll("");
                }

                return dest;
            }
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 返回二维码的base64形式
     * @param content
     */
    public static String genQRCode (String content) {
        Bitmap img = QRCodeUtil.createQRCodeBitmap(content, 256, 256, "UTF-8", "L", "1", Color.BLACK, Color.WHITE);
        String base64 = bitmapToBase64(img);

        Log.d("xxx", "#### base64:" + base64);

//        Bitmap bitmap = base64ToBitmap(base64);

//        try {
//            String dir = "/storage/emulated/0/Android/data/org.cocos2dx.demo/files/file/";
////                dir = getExternalFilesDir("file").getAbsolutePath();
//            File dirHandle = new File(dir);
//            if (!dirHandle.exists()) dirHandle.mkdirs();
//
//
//            Log.d("xxx", "#### 路径:" + dir);
//            File file = new File(dir, "test2.txt");
//            file.createNewFile();
//            FileOutputStream out = new FileOutputStream(file);
//            out.write(base64.getBytes());
////            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//
//            Log.d("xxx", "#### OK!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return base64;
    }
}
