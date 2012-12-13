package com.baidu.player.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.baidu.browser.net.ProxyHttpClient;
import com.baidu.player.R;
import com.baidu.player.util.Utility;

/**
 * @ClassName: NetImageView 
 * @Description: ����������ȡ���ݡ��������ݹ��ܵ�imageview.
 * @author LEIKANG 
 * @date 2012-12-12 ����1:36:40
 */
public class NetImageView extends ImageView {
	/** TAG.*/
	private static final String TAG = NetImageView.class.getSimpleName();
	/** DEBUG SWITCH.*/
	private static final boolean DEBUG = true;
	
    /** VIEW_TAG_KEY */
    private static final int VIEW_TAG_KEY = ((0x2f << 24) | android.R.id.summary);

    /** �Ƿ��Ѵ�����ץȡ����Դ�������ѱ��������ݸ���*/
	private boolean mHasGrabbed;
	
	/** �������ݵ�url.*/
	private String mUrl;
	
	/** �Ƿ񱣴�imageview���ݵ��ڴ�.*/
    private boolean mCacheToRAM = false;
    
    /** ��ʱ������ȡ����.*/
    private long mDelayGrabData = 0;
    
    /** {@link ProxyHttpClient} .*/
    private ProxyHttpClient mHttpClient;
    
	/** imageview������Դ.*/
	private Drawable mImageBackground;
	
	/** Ĭ��image src.*/
	private int mDefaultImageSrc = R.drawable.search_sug_keywords_normal;
	
	/** ��ͼƬ�Ĵ�С��ʹ�ô洢��Լ��.*/
	static final int UNCONSTRAINED = -1;
	
	/** ͼƬ�������.*/
	private int mMaxWidth = UNCONSTRAINED;
	
	/** ͼƬ�����߶�.*/
	private int mMaxHeight = UNCONSTRAINED;
	
	/** �����ص�ͼƬ���ڴ滺��.*/
	private static Map<String, Bitmap> cacheBitmap = new HashMap<String, Bitmap>();
	
	/** �����ص�ͼƬ���ڴ滺���������.*/
	private static final int MAX_CACHE_TO_RAM_SIZE = 5;
	
	/**
	 * ���캯��
	 * @param context Context
	 * @param attrs AttributeSet
	 * @param defStyle int
	 */
	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		if (getDrawable() == null) {
		    setDefaultImage();
		}
	}
	
	/**
	 * ȡ���ڲ���ImageView
	 * @return �ڲ���ImageView
	 */
	public ImageView getImageView() {
	    return this;
	}
	
	/**
	 * ���캯��
	 * @param context Context
	 * @param attrs AttributeSet
	 */
	public NetImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	/**
	 * ���캯��
	 * @param context Context
	 */
	public NetImageView(Context context) {
		this(context, null, 0);
	}

	/**
	 * ͨ������url����������Ⱥ����߶����û�ȡimageview.
	 * @param url image url
	 * @param delayTime ��ʱ��ȡ�������ݵ�ʱ��
	 * @param cacheToRam �Ƿ�����ݽ����ڴ滺��
	 * @param maxWidth �����
	 * @param maxHeight ���߶�
	 */
	public void setParam(String url, long delayTime, boolean cacheToRam, int maxWidth, int maxHeight) {
		if (DEBUG) {
			Log.d(TAG, "setParam: url:" + url);
		}
	    setTag(VIEW_TAG_KEY, url);
	    
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mDelayGrabData = delayTime;
        
        setParam(url, cacheToRam);
    }

	/**
	 * ͨ������url����ȡimageview����.
	 * @param url image url
	 * @param cacheToRam �Ƿ�����ݽ����ڴ滺��
	 */
	public void setParam(String url, boolean cacheToRam) {
		mUrl = url;
		mCacheToRAM = cacheToRam;
	}
	
	/**
	 * ͨ������url����ȡimageview����.
	 * @param url image url
	 */
	public void setParam(String url) {
	    setParam(url, false);
	}

	/**
	 * �Ͽ�ǰһ������.
	 */
	private void closeConnect() {
		if (mHttpClient != null) {
			mHttpClient.close();
		}
	}
	
	/**
	 * ����super.setImageResource
	 * @param resId resId
	 */
	private void callSuperSetImageResource(int resId) {
	    super.setImageResource(resId);
	}
	
	/**
	 * ����super.setImageDrawable
	 * @param drawable drawable
	 */
	private void callSuperSetImageDrawable(Drawable drawable) {
	    super.setImageDrawable(drawable);
	}
	
	/**
	 * ����imageview��Ĭ��icon����
	 * @param resId ��Դid
	 */
	public void setDefaultImageResource(int resId) {
		mDefaultImageSrc = resId;
	}
	
	/**
	 * ����imageview��Ĭ��icon
	 */
	public void setDefaultImage() {
	    mHasGrabbed = false;
	    callSuperSetImageResource(mDefaultImageSrc);
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
	    mHasGrabbed = true;
	    callSuperSetImageDrawable(drawable);
	}
	
	@Override
	public void setImageResource(int resId) {
	    mHasGrabbed = true;
	    callSuperSetImageResource(resId);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
	    mHasGrabbed = true;
	    super.setImageBitmap(bm);
	}
	
	/**
	 * ץȡimage����.
	 */
	public void grabViewData() {
	    mHasGrabbed = false;

		Bitmap bitmap = cacheBitmap.get(mUrl);
		if (bitmap != null) { //ʹ���ڴ��еĻ���
			requestSetImageBitmap(bitmap);
		} else {
			requestSetDefaultImage();
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (mDelayGrabData > 0) {
						try {
							Thread.sleep(mDelayGrabData);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String url = mUrl;
					byte[] data = getViewDataFromNet(url);
					boolean suc = false;
					if (data != null) {
						suc = setGrabbedImage(url, data);
					}
					if (!suc) {
						requestSetDefaultImage();
					}
				}
			};
			new Thread(r, "getViewDataFromNet").start();
		}
		
	}
	
	/**
	 * ͨ�����ݹ���bitmap������imageview������mCacheToRAM����bitmap���ڴ�.
	 * @param url ��ȡ���������ݶ�Ӧ��url��ʵ�ְ�
	 * @param data byte[],bitmap����
	 * @return �Ƿ����óɹ�
	 */
	private boolean setGrabbedImage(String url, byte[] data) {  
		if (data != null) {
		    Bitmap bitmap = null;
		    try {
		        if (mMaxWidth == UNCONSTRAINED
		                && mMaxHeight == UNCONSTRAINED) {
		            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		        } else {  
                    if (mMaxWidth == UNCONSTRAINED) {
                        mMaxWidth = mMaxHeight;
                    } else if (mMaxHeight == UNCONSTRAINED) {
                        mMaxHeight = mMaxWidth;
                    }
                    bitmap = makeBitmap(data, Math.min(mMaxWidth, mMaxHeight), UNCONSTRAINED);
		        }
		    } catch (OutOfMemoryError er) {  
	            if (DEBUG) {
	                Log.w(TAG, "setImageView outofmemory. data.length=" + data.length);
	                er.printStackTrace();
	            }
	        }
			if (bitmap != null) {
				if (mCacheToRAM) {
					cacheBitmap(url, bitmap);
				}
				String tagUrl = (String) getTag(VIEW_TAG_KEY);
				if (TextUtils.equals(tagUrl, url)) {
				    requestSetImageBitmap(bitmap);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ��bitmap�����ڴ滺��
	 * @param url ��Ϊkey
	 * @param bitmap Bitmap
	 */
	private void cacheBitmap(String url, Bitmap bitmap) {
		if (cacheBitmap.size() >= MAX_CACHE_TO_RAM_SIZE) {
			cacheBitmap.clear();
		}
		
		cacheBitmap.put(url, bitmap);
	}
	/**
	 * ��������image��Դ�����default��Դ�ѱ��滻����������
	 * @param bitmap Bitmap
	 */
	private void requestSetImageBitmap(final Bitmap bitmap) {
		
		((Activity) getContext()).runOnUiThread(new Runnable() {
			public void run() {
			    if (mHasGrabbed) {
			        return;
			    }
			    
			    setImageBitmap(bitmap);
			    setBackgroundDrawable(mImageBackground);
			    
			    invalidate();
			}
		});
	}
	
	/**
	 * ��������default��Դ�����default��Դ�ѱ��滻����������
	 */
	private void requestSetDefaultImage() {
		((Activity) getContext()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			    if (mHasGrabbed) {
			        return;
			    }
			    
			    setDefaultImage();
			}
		});
	}
	/**
	 * ��net��ȡ����.
	 * @param url ��ȡ���ݵ�url
	 * @return ��ȡ������
	 */
	private byte[] getViewDataFromNet(String url) {
		if (TextUtils.isEmpty(url)) {
			Log.e(TAG, "getViewDataFromNet: url is null or empty.");
			return null;
		}
		closeConnect();

		Context context = getContext();
		url = Utility.encodeUrl(url);
		HttpGet httpget = new HttpGet(url);
		mHttpClient = Utility.createHttpClient(context);
		
		try {
			HttpResponse response = mHttpClient.execute(httpget);
	        HttpEntity resEntity = response.getEntity();
	        InputStream inputStream = resEntity.getContent();
	        ByteArrayBuffer buf = new ByteArrayBuffer(1024); // SUPPRESS CHECKSTYLE
	        
	    	byte[] buffer = new byte[1024]; // SUPPRESS CHECKSTYLE
	    	do {
	    		int len = 0;
				try {
					len = inputStream.read(buffer, 0, buffer.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		if (len != -1) {
	    			buf.append(buffer, 0, len);
	    		} else {
	    			break;
	    		}
	    	} while (true);
	    	
	    	return buf.toByteArray();
	        
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mHttpClient.close();
		}
		return null;
	}

    /**
     * ����bitmap.
     * @param data ͼƬ����
     * @param minSideLength ��С��
     * @param maxNumOfPixels ���pix���������������ڴ�ʹ����
     * @return bitmap
     */
	 
    public static Bitmap makeBitmap(byte[] data, int minSideLength, int maxNumOfPixels) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (OutOfMemoryError ex) {
            Log.e(TAG, "Got oom exception ", ex);
            return null;
        }
    }
    
    /**
     * Compute the sample size as a function of minSideLength and maxNumOfPixels. minSideLength is used to specify that
     * minimal width or height of a bitmap. maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage. The function returns a sample size based on the constraints. Both size and
     * minSideLength can be passed in as IImage.UNCONSTRAINED, which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that generates a smaller bitmap, unless minSideLength =
     * IImage.UNCONSTRAINED. Also, the function rounds up the sample size to a power of 2 or multiple of 8 because
     * BitmapFactory only honors sample size this way. For example, BitmapFactory downsamples an image by 2 even though
     * the request is 3. So we round up the sample size to avoid OOM.
     *
     * @param options BitmapFactory.Options
     * @param minSideLength ��С��
     * @param maxNumOfPixels ���pix���������������ڴ�ʹ����
     * @return samle size
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        final int bits = 8;
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= bits) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + bits - 1) / bits * bits;
        }

        return roundedSize;
    }

    /**
     * computeInitialSampleSize ��computeSampleSize����.
     * @param options BitmapFactory.Options
     * @param minSideLength ��С��
     * @param maxNumOfPixels ���pix���������������ڴ�ʹ����
     * @return int
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        final int defaultUpperBound = 128;
        double w = options.outWidth;
        double h = options.outHeight;

        
        int lowerBound = 1;
        if (maxNumOfPixels != UNCONSTRAINED) {
            lowerBound = (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        }
        int upperBound = defaultUpperBound;
        if (minSideLength != UNCONSTRAINED) {
            upperBound = (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        }

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}

