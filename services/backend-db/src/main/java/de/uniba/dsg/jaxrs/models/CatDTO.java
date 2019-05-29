package de.uniba.dsg.jaxrs.models;

import org.bson.types.ObjectId;

public class CatDTO {
	public ObjectId id;
	public int identifier;
	public String name;
	public String imageUrl;
	public String movie;

	public CatDTO() {
	}

	public CatDTO(int identifier, String name, String imageUrl, String movie) {
		this.identifier = identifier;
		this.name = name;
		this.imageUrl = imageUrl;
		this.movie = movie;
	}

	public CatDTO(Cat newCat) {
		this.identifier = newCat.id;
		this.name = newCat.name;
		this.imageUrl = newCat.imageUrl;
		this.movie = newCat.movie;
	}

	public Cat convert() {
		return new Cat(identifier, name, imageUrl, movie);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMovie() {
		return movie;
	}

	public void setMovie(String movie) {
		this.movie = movie;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Cat [id=" + id + ", identifier=" + identifier + ", name=" + name + ", imageUrl=" + imageUrl + ", movie="
				+ movie + "]";
	}

}
