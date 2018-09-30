package capstone.uic.com.imperium;

public class UploadImage {

    private String mImageUrl;

    public UploadImage() {

    }

    public UploadImage(String name, String imageUrl) {

        mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

}
