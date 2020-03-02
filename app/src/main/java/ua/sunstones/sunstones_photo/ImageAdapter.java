package ua.sunstones.sunstones_photo;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter{
    private Context mContext;
    private LayoutInflater layoutInflater;
//    private Integer [] images = {R.drawable.pic_1,R.drawable.pic_2,R.drawable.pic_3};

    ArrayList<Photo> mListPhotos = new ArrayList();


    ImageAdapter(Context context, ArrayList<Photo> listPhotos) {

        this.mContext    = context;
        this.mListPhotos = listPhotos;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        ImageView imageView = new ImageView(mContext);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        imageView.setImageResource(images[position]);
//        container.addView(imageView, 0);
//        return imageView;



        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView image_pic = (ImageView) view.findViewById(R.id.image_pic);
        ImageView image_tag = (ImageView) view.findViewById(R.id.image_tag);

//        imageView.setImageResource(images[position]);
        Photo photo = mListPhotos.get(position);
        File imgFile = new  File(photo.full_name);
        if(imgFile.exists()) {
            image_pic.setImageURI(Uri.fromFile(imgFile));
        }

        image_tag.setVisibility(View.VISIBLE);
        if(photo.status == Photo.Status.ADD_PHOTO){
            image_tag.setImageDrawable(mContext.getDrawable(R.drawable.add));
        }else if(photo.status == Photo.Status.DELETE_PHOTO){
            image_tag.setImageDrawable(mContext.getDrawable(R.drawable.delete));
        }else{
            image_tag.setVisibility(View.INVISIBLE);
        }


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((ImageView) object);

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    @Override
    public int getCount() {
        return mListPhotos.size();
    }
}
