package com.example.pedro.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String id;
    private String imagePath;
    private String title;
    private String overview;
    private long rating;
    private String releaseDate;

    public String getReleaseDate() {
        return releaseDate;
    }

    public long getRating() {
        return rating;
    }



    public Movie() {

    }

    public Movie(String id,String path,String title,String overview,long rating,String release) {
        this.id = id;
        this.imagePath = path;
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = release;
    }


    private Movie(Parcel in) {
        id = in.readString();
        imagePath = in.readString();
        title = in.readString();
        overview = in.readString();
        rating = in.readLong();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out,int i) {
        out.writeString(id);
        out.writeString(imagePath);
        out.writeString(title);
        out.writeString(overview);
        out.writeLong(rating);
        out.writeString(releaseDate);
    }


    public static final Parcelable.Creator<Movie> CREATOR =  new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }

    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

}
