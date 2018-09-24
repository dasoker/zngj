package znjt.nxld.com.util.imageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
*@Author:dingkuirong
*@date:2018/9/13 15:01
*@Description:图片加载类
*/

public class GridViewImageLoader {
    private static GridViewImageLoader mInstance;
    private LruCache<String,Bitmap> mLruCache;//图片缓存的核心对象
    private ExecutorService mThreadPool;//线程池
    private static final int DEFAULT_THREAD_COUNT=1;
    private Type mType = Type.LIFO;//队列的调度对象
    private LinkedList<Runnable> mTaskQueue;//任务队列
    private Thread mPoolThread;//后台轮询线程
    private Handler mPoolThreadHandler;
    private Handler mUiHandler;//UI线程中的Handler
    private Semaphore mSemaphorePoolThreadHandler=new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;
    public enum Type{
        FIFO,LIFO;
    }
    private GridViewImageLoader(int threadCount,Type type ){
        init(threadCount,type);
    }

    /**
     * 初始化
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        //后台轮询线程
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler  =new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();
        //获取我们应用的最大使用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory/8;
        mLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getRowBytes()*value.getHeight();
            }
        };
        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
        mSemaphoreThreadPool = new Semaphore(threadCount);
    }

    /**
     * 从任务队列取出一个方法
     * @return
     */
    private Runnable getTask() {
        if (mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }else if (mType == Type.LIFO){
            return mTaskQueue.removeLast();
        }
        return null;
    }

    public static GridViewImageLoader getInstance(){
        if (mInstance == null){
            synchronized (GridViewImageLoader.class){
                if (mInstance == null){
                    mInstance = new GridViewImageLoader(DEFAULT_THREAD_COUNT,Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static GridViewImageLoader getInstance(int threadCount,Type type){
        if (mInstance == null){
            synchronized (GridViewImageLoader.class){
                if (mInstance == null){
                    mInstance = new GridViewImageLoader(threadCount,type);
                }
            }
        }
        return mInstance;
    }
    /**
     * 根据path 为imageView设置图片
     * @param path
     * @param imageView
     */
    public void loadImage( final String path, final ImageView imageView){
        imageView.setTag(path);
        if (mUiHandler == null){
            mUiHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //获取得到的图片，为imageView回调设置图片
                    ImageBeanHolder holder= (ImageBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageView= holder.imageView;
                    String path = holder.path;
                    //将path与getTag路径进行比较
                    if (imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null){
            refreashBitmap(bm, path, imageView);

        }else {
            addTasks(new Runnable(){
                @Override
                public void run() {
                    //加载图片
                    //图片的压缩
                    //1、获得图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //2、压缩图片
                    Bitmap bm=decodeSampleBitMapFromPath(path,imageSize.width,imageSize.height);
                    //3、加入缓存
                    addBitmapToLruCache(path,bm);
                    refreashBitmap(bm, path, imageView);
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    private void refreashBitmap(Bitmap bm, String path, ImageView imageView) {
        Message message = Message.obtain();
        ImageBeanHolder imageBeanHolder = new ImageBeanHolder();
        imageBeanHolder.bitmap =bm;
        imageBeanHolder.path = path;
        imageBeanHolder.imageView = imageView;
        message.obj= imageBeanHolder;
        mUiHandler.sendMessage(message);
    }

    /**
     * 将图片加入缓存
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path)==null){
            if (bm!=null){
                mLruCache.put(path,bm);
            }
        }
    }

    /**
     * 根据图片需要显示宽和高对图片进行压缩
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampleBitMapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = caculateInSampleSize(options,width,height);
        //使用获取的insamplesize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }
    /**
     * 根据需求的宽和高以及图片实际的宽和高计算sampleSize
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width>reqWidth||height>reqHeight){
            int widthRadio = Math.round(width*1.0f/reqWidth);
            int heightRadio = Math.round(height*1.0f/reqHeight);
            inSampleSize = Math.min(widthRadio,heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 根据imageView获取适当的宽和高
     * @param imageView
     * @return
     */

    protected ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width =imageView.getWidth();//获取imageView的实际宽度；
        if (width<= 0){
            width = lp.width;//获取imageView在layout中声明的宽度
        }
        if (width<=0){
            //width = imageView.getMaxWidth();//检查最大值
            width = getImageViewFieldValue(imageView,"mMaxWidth");
        }
        if (width<=0){
            width = displayMetrics.widthPixels;//屏幕宽度
        }

        int height =imageView.getHeight();//获取imageView的实际高度；
        if (height<= 0){
            height = lp.height;//获取imageView在layout中声明的gao度
        }
        if (height<=0){
            //height = imageView.getMaxHeight();//检查最大值
            height=getImageViewFieldValue(imageView,"mMaxHeight ");
        }
        if (height<=0){
            height = displayMetrics.heightPixels;//屏幕宽度
        }
        imageSize.height=height;
        imageSize.width = width;
        return imageSize;
    }

    /**
     * 通过反射获取imageView的某个值
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object,String fieldName){
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue>0&&fieldValue<Integer.MAX_VALUE){
                value =fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private synchronized void addTasks(Runnable runnable) {
        mTaskQueue.add(runnable);
        //if（mPoolThreadHandler == null）wait();
        try {
            if (mPoolThreadHandler==null) {
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }
    private class ImageSize{
        int width;
        int height;
    }
    private class ImageBeanHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
}
